package com.welyab.covid19.maranhao

import java.text.Normalizer

fun normalizeString(string: String): String {
    return Normalizer.normalize(string.toLowerCase(), Normalizer.Form.NFD)
            .replace("[^\\p{ASCII}]".toRegex(), "");
}
