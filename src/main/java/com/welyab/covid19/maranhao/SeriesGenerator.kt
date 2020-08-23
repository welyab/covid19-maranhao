package com.welyab.covid19.maranhao

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

class SeriesGenerator {

    private var series: Series? = null

    fun getSeries(): Series {
        if (series != null) return series!!
        val extractors = getExtractors()
        val dates = ArrayList<LocalDate>()
        val cumulativeCasesPreviousDay = HashMap<String, CumulativeCases>()
        val activeInfections = HashMap<String, ArrayList<DateNewCases>>()
        val casesMap = HashMap<String, ArrayList<Cases>>()
        extractors.forEach { extractor ->
            dates += extractor.date
            val cumulativeCases = extractor.dataExtractor.extractCumulativeCases()
            ResourceUtil.cityInfos.forEach { cityInfo ->
                val previousCumulativeCases = cumulativeCasesPreviousDay[cityInfo.id]
                        ?: CumulativeCases(extractor.date, 0, 0)
                val currentCumulativeCases = cumulativeCases[cityInfo.id]
                        ?: CumulativeCases(extractor.date, 0, 0)
                val newInfections = currentCumulativeCases.infected - previousCumulativeCases.infected
                val newDeaths = (currentCumulativeCases.deaths - previousCumulativeCases.deaths).coerceAtLeast(0)
                activeInfections.computeIfAbsent(cityInfo.id) { ArrayList() } += DateNewCases(
                        extractor.date,
                        (newInfections - newDeaths).coerceAtLeast(0)
                )
                activeInfections[cityInfo.id]!!.removeIf {
                    it.date.until(extractor.date, ChronoUnit.DAYS) >= 15
                }
                val currentActiveCases = activeInfections[cityInfo.id]!!.asSequence().map { it.cases }.sum()
                val cases = Cases(currentActiveCases, newDeaths)
                casesMap.computeIfAbsent(cityInfo.id) { ArrayList() } += cases
                cumulativeCasesPreviousDay[cityInfo.id] = currentCumulativeCases
            }
        }
        return Series(dates, casesMap)
    }

    private fun getExtractors(): List<Extractor> {
        val list = ArrayList<Extractor>()
        ResourceUtil.csvFileList.forEach { fileName ->
            val date = getDate(fileName)
            list += Extractor(date, CsvDataExtractor(ResourceUtil.getCsvContent(fileName).inputStream(), date))
        }
        ResourceUtil.xlsxFileList.forEach { fileName ->
            val date = getDate(fileName)
            list += Extractor(date, XlsxDataExtractor(ResourceUtil.getXlsxContent(fileName).inputStream(), date))
        }
        return list.sortedBy { it.date }
    }

    private fun getDate(fileName: String): LocalDate {
        return LocalDate.from(
                DateTimeFormatter
                        .ofPattern("yyyy-MM-dd")
                        .parse(
                                fileName.replace(
                                        ".*(\\d{4}-\\d{2}-\\d{2}).*".toRegex(),
                                        "$1"
                                )
                        )
        )
    }
}

private data class Extractor(val date: LocalDate, val dataExtractor: DataExtractor)
private data class DateNewCases(val date: LocalDate, val cases: Int)
