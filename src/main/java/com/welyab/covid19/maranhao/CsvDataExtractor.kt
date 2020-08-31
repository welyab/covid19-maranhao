package com.welyab.covid19.maranhao

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsv
import com.opencsv.bean.StatefulBeanToCsvBuilder
import info.debatty.java.stringsimilarity.Levenshtein
import java.io.FileWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Writer
import java.time.LocalDate


class CsvDataExtractor(private val csvInput: InputStream, private val date: LocalDate) : DataExtractor {

    private var map: HashMap<String, CumulativeCases>? = null

    override fun extractCumulativeCases(): Map<String, CumulativeCases> {
        if(map != null) return map!!

        val csvEntries = CSVReaderBuilder(InputStreamReader(csvInput))
                .withCSVParser(
                        CSVParserBuilder()
                                .withSeparator(';')
                                .build()
                )
                .withSkipLines(1)
                .build()
                .readAll()
        val levenshtein = Levenshtein()

        map = HashMap()
        ResourceUtil.cityInfos
                .asSequence()
                .map { it.id }
                .forEach { id ->
                    csvEntries
                            .asSequence()
                            .map { levenshtein.distance(normalizeString(it[0]), id) to it }
                            .filter { it.first <= 1.0 }
                            .sortedBy { it.first }
                            .firstOrNull()
                            ?.apply {
                                map!![id] = CumulativeCases(
                                        date,
                                        second[1].toIntOrNull() ?: 0,
                                        second[2].toIntOrNull() ?: 0
                                )
                            }
                }
        return map!!
    }

    override fun close() {
        csvInput.close()
    }
}
