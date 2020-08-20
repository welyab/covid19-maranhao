package com.welyab.covid19.maranhao

import java.io.InputStream

class CsvDataExtractor(private val csvInput: InputStream) : DataExtractor {

    override fun extractCumulativeCases(): Map<String, CumulativeCases> {

        TODO("Not yet implemented")
    }

    override fun close() {
        csvInput.close()
    }
}
