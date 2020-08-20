package com.welyab.covid19.maranhao

import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

private object CharSetAdjuster {

    fun getContent(fileName: String, charset: Charset): String {
        return InputStreamReader(
                ByteArrayInputStream(ResourceUtil.getCsvContent(fileName)),
                charset
        ).readText()
    }
}

fun main() {
    ResourceUtil.csvFileList.forEach { csvFile ->
        val csvContent = CharSetAdjuster.getContent(csvFile, Charset.forName("windows-1252"))
        Files.newBufferedWriter(Paths.get("C:\\Users\\welyab\\Desktop\\csv\\$csvFile"), StandardCharsets.UTF_8).use {
            it.write(csvContent)
        }
    }
}
