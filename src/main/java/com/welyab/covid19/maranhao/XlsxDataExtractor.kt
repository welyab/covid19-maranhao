package com.welyab.covid19.maranhao

import info.debatty.java.stringsimilarity.Levenshtein
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.lang.RuntimeException
import java.time.LocalDate

class XlsxDataExtractor(private val xlsxInput: InputStream, private val date: LocalDate) : DataExtractor {

    private var map: HashMap<String, CumulativeCases>? = null

    override fun extractCumulativeCases(): Map<String, CumulativeCases> {
        if (map != null) return map!!

        val workbook = XSSFWorkbook(xlsxInput)
        val sheet = workbook.getSheetAt(0)
        val cityNameCell = getCityFirstCityHeaderCellNumber(sheet)
        val infectedCount = cityNameCell + 1
        val deathCountCell = cityNameCell + 2

        data class Entry(val cityName: String, val infection: String, val death: String)

        val xlsxEntries = (sheet.firstRowNum..sheet.lastRowNum)
                .asSequence()
                .map { sheet.getRow(it) }
                .filterNotNull()
                .map {
                    Entry(
                            getStringValue(it, cityNameCell),
                            getStringValue(it, infectedCount),
                            getStringValue(it, deathCountCell),
                    )
                }
                .toList()
        val levenshtein = Levenshtein()
        map = HashMap<String, CumulativeCases>()
        ResourceUtil.cityInfos
                .asSequence()
                .map { it.id }
                .forEach { id ->
                    xlsxEntries
                            .asSequence()
                            .map { levenshtein.distance(normalizeString(it.cityName), id) to it }
                            .filter { it.first <= 1 }
                            .sortedBy { it.first }
                            .firstOrNull()
                            ?.apply {
                                map!![id] = CumulativeCases(
                                        date,
                                        second.infection.toIntOrNull() ?: 0,
                                        second.death.toIntOrNull() ?: 0
                                )
                            }
                }
        return map!!
    }

    private fun getCityFirstCityHeaderCellNumber(sheet: XSSFSheet): Int {
        return (sheet.firstRowNum..sheet.lastRowNum)
                .asSequence()
                .map { sheet.getRow(it) }
                .filterNotNull()
                .flatMap { row ->
                    (row.firstCellNum..row.lastCellNum)
                            .asSequence()
                            .filter { it >= 0 }
                            .map { row.getCell(it) }
                }
                .filterNotNull()
                .filter { cell ->
                    normalizeString(getStringValue(cell))
                            .let { it == MUNICIPIO_HEADER || it == MUNICIPIOS_HEADER }
                }
                .map { it.columnIndex }
                .firstOrNull()
                ?: throw RuntimeException("Unable to find município[s] header cell number")
    }

    private fun getStringValue(row: XSSFRow, cellNumber: Int): String {
        val cell = row.getCell(cellNumber) ?: return ""
        return getStringValue(cell)
    }

    private fun getStringValue(cell: XSSFCell): String {
        if (cell.cellType == CellType.STRING) return cell.stringCellValue
        return cell.rawValue ?: ""
    }

    override fun close() {
        xlsxInput.close()
    }

    companion object {
        private val MUNICIPIO_HEADER = "municipio"
        private val MUNICIPIOS_HEADER = "municipios"
    }
}
