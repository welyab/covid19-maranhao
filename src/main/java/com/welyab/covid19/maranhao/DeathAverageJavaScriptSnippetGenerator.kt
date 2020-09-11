package com.welyab.covid19.maranhao

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

fun main() {
    val generator = SeriesGenerator()
    val series = generator.getSeries()
    print("var dates = [")
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    series.dates.forEachIndexed { index, localDate ->
        if(index > 0) print(", ")
        print("\"${formatter.format(localDate)}\"")
    }
    println("]")

    print("var cityNames = [")
    series.cases.keys.forEachIndexed { index, cityName ->
        if(index > 0) print(", ")
        print("\"$cityName\"")
    }
    println("]")

    println("var deaths = []")
    println("var dailyDeaths = []")
    series.cases.forEach { entry ->
        print("deaths[\"${entry.key}\"] = [")
        for(i in entry.value.indices) {
            var last15dayAcumulatedDeaths = 0
            var totalDays = 0
            for(j in i downTo 0) {
                if(series.dates[i].until(series.dates[j]).days.absoluteValue >= 15) break
                last15dayAcumulatedDeaths += entry.value[j].deaths
                totalDays++
            }
            if(i > 0) print(", ")
            val averageDeaths = BigDecimal(last15dayAcumulatedDeaths)
                    .divide(BigDecimal(totalDays), 2, RoundingMode.HALF_UP)
                    .toString()
            print("$averageDeaths")
        }
        println("]")
        print("dailyDeaths[\"${entry.key}\"] = [")
        print(entry.value.asSequence().map { it.deaths }.joinToString())
        println("]")
    }
}
