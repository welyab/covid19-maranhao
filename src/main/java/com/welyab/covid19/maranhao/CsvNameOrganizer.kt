package com.welyab.covid19.maranhao

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

private operator fun String.get(range: IntRange) = substring(range)

fun renameToCovidMaranhaoYYYYddMM() {
    // tested inside IDE only
    val directory = Paths.get(System.getProperty("user.dir")).resolve("src/main/resources/data/csv")
    val fileNames = Files
            .list(directory)
            .map { it.fileName.toString() }
            .filter { it.startsWith("Dados-Gerais") }
            .toList()
    fileNames.forEach {
        val datePart = it.replace(Regex("Dados-Gerais-(\\d\\d)(\\d\\d).csv"), "$2$1")
        val newFileName = "covid-maranhao-2020-${datePart[0..1]}-${datePart[2..3]}.csv"
        Files.move(directory.resolve(it), directory.resolve(newFileName))
        println("$it renamed to $newFileName")
    }
}

fun main() {
    renameToCovidMaranhaoYYYYddMM()
}
