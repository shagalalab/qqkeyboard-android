package com.shagalalab.qqkeyboard.keyboard.data

import android.content.Context
import com.shagalalab.qqkeyboard.keyboard.data.db.SeedDictionary
import com.shagalalab.qqkeyboard.keyboard.data.db.UserDictionary
import com.shagalalab.qqkeyboard.keyboard.utils.FuzzyMatcher

class SuggestionRepository(context: Context) {

    private val seed = SeedDictionary(context)
    private val user = UserDictionary(context)

    // Blocking — call from IO dispatcher
    fun getSuggestions(prefix: String, script: String, limit: Int = 5): List<String> {
        if (prefix.isEmpty()) return emptyList()

        val seedExact = seed.query(prefix, script, 10)
        val userExact = user.query(prefix, script, 10)

        val scores = mutableMapOf<String, Int>()
        seedExact.forEach { (word, freq) -> scores[word] = freq }
        userExact.forEach { (word, freq) ->
            scores[word] = (scores[word] ?: 0) + freq * 2
        }

        val exactResults = scores.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }

        if (exactResults.size >= limit) return exactResults

        // Fuzzy fallback: transpositions + diacritic variants
        val candidates = FuzzyMatcher.candidatePrefixes(prefix).toList()
        if (candidates.isEmpty()) return exactResults

        val seedFuzzy = seed.queryPrefixes(candidates, script, 10)
        val userFuzzy = user.queryPrefixes(candidates, script, 10)

        val fuzzyScores = mutableMapOf<String, Int>()
        seedFuzzy.forEach { (word, freq) -> fuzzyScores[word] = freq }
        userFuzzy.forEach { (word, freq) ->
            fuzzyScores[word] = (fuzzyScores[word] ?: 0) + freq * 2
        }

        exactResults.forEach { fuzzyScores.remove(it) }

        val fuzzyResults = fuzzyScores.entries
            .sortedByDescending { it.value }
            .take(limit - exactResults.size)
            .map { it.key }

        return exactResults + fuzzyResults
    }

    // Blocking — call from IO dispatcher
    fun getBigramPredictions(lastWord: String, script: String, limit: Int = 5): List<String> {
        if (lastWord.isEmpty()) return emptyList()

        val seedResults = seed.queryBigrams(lastWord, script, 10)
        val userResults = user.queryBigrams(lastWord, script, 10)

        val scores = mutableMapOf<String, Int>()
        seedResults.forEach { (word, freq) -> scores[word] = freq }
        userResults.forEach { (word, freq) ->
            scores[word] = (scores[word] ?: 0) + freq * 2
        }

        return scores.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }

    // Blocking — call from IO dispatcher
    fun learnWord(word: String, script: String) {
        user.learnWord(word, script)
    }

    // Blocking — call from IO dispatcher
    fun learnBigram(prefix: String, nextWord: String, script: String) {
        user.learnBigram(prefix, nextWord, script)
    }
}
