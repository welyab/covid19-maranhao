package com.welyab.covid19.maranhao

interface DataExtractor {

    fun extractCumulativeCases(): Map<String, CumulativeCases>
}
