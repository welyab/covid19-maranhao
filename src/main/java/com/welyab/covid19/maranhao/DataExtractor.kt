package com.welyab.covid19.maranhao

interface DataExtractor : AutoCloseable {

    fun extractCumulativeCases(): Map<String, CumulativeCases>
}
