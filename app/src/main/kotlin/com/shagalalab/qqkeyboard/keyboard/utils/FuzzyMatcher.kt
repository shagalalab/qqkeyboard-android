package com.shagalalab.qqkeyboard.keyboard.utils

object FuzzyMatcher {

    private val DIACRITIC_MAP = mapOf(
        'a' to 'á', 'á' to 'a',
        'g' to 'ǵ', 'ǵ' to 'g',
        'i' to 'ı', 'ı' to 'i',
        'n' to 'ń', 'ń' to 'n',
        'o' to 'ó', 'ó' to 'o',
        'u' to 'ú', 'ú' to 'u'
    )

    fun candidatePrefixes(prefix: String): Set<String> {
        val candidates = mutableSetOf<String>()

        // Adjacent character transpositions: "sne" → "sen"
        for (i in 0 until prefix.length - 1) {
            val chars = prefix.toCharArray()
            val tmp = chars[i]; chars[i] = chars[i + 1]; chars[i + 1] = tmp
            candidates.add(String(chars))
        }

        // Single diacritic substitutions: "arqali" → "arqalı", "arna" → "árna"
        for (i in prefix.indices) {
            val alt = DIACRITIC_MAP[prefix[i]] ?: continue
            candidates.add(prefix.substring(0, i) + alt + prefix.substring(i + 1))
        }

        candidates.remove(prefix)
        return candidates
    }
}
