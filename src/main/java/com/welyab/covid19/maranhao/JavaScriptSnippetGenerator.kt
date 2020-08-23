package com.welyab.covid19.maranhao

import java.time.format.DateTimeFormatter

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

    println("var activeCases = []")
    println("var deaths = []")
    series.cases.forEach { entry ->
        print("activeCases[\"${entry.key}\"] = [")
        entry.value.forEachIndexed { index, cases ->
            if(index > 0) print(", ")
            print("${cases.infected}")
        }
        println("]")

        print("deaths[\"${entry.key}\"] = [")
        entry.value.forEachIndexed { index, cases ->
            if(index > 0) print(", ")
            print("${cases.deaths}")
        }
        println("]")
    }
}
