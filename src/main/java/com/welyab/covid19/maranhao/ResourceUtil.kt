package com.welyab.covid19.maranhao

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.InputStreamReader

object ResourceUtil {

    val cityInfos: List<CityInfo> by lazy {
        this.javaClass.getResourceAsStream("/data/city-names.csv").use {
            CSVReaderBuilder(InputStreamReader(it))
                    .withCSVParser(
                            CSVParserBuilder()
                                    .withSeparator('\t')
                                    .build()
                    )
                    .withSkipLines(1)
                    .build()
                    .readAll()
                    .asSequence()
                    .map { entry ->
                        CityInfo(entry[1], entry[2].toInt())
                    }
                    .toList()
        }
    }

    val csvFileList: List<String> by lazy {
        this.javaClass.getResourceAsStream("/data/csv/filelist.txt").use {
            InputStreamReader(it).readLines()
        }
    }

    val xlsxFileList: List<String> by lazy {
        this.javaClass.getResourceAsStream("/data/xlsx/filelist.txt").use {
            InputStreamReader(it).readLines()
        }
    }

    fun getCsvContent(fileName: String): ByteArray {
        return getFileContent("csv/$fileName")
    }

    fun getXlsxContent(fileName: String): ByteArray {
        return getFileContent("xlsx/$fileName")
    }

    private fun getFileContent(fileName: String): ByteArray {
        return this.javaClass.getResourceAsStream("/data/$fileName").use {
            it.readAllBytes()
        }
    }
}
