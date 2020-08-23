package com.welyab.covid19.maranhao

import java.time.LocalDate

data class CityInfo(val id: String, val name: String, val population: Int)
data class CumulativeCases(val date: LocalDate, val infected: Int, val deaths: Int)
data class Cases(val infected: Int, val deaths: Int)
data class Series(val dates: List<LocalDate>, val cases: Map<String, List<Cases>>)
