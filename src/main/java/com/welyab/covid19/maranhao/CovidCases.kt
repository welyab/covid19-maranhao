package com.welyab.covid19.maranhao

import java.time.LocalDate

data class CumulativeCases(val date: LocalDate, val infected: Int, val deaths: Int)
