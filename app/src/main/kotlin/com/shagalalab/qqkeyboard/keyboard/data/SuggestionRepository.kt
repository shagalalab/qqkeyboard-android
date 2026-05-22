package com.shagalalab.qqkeyboard.keyboard.data

import android.content.Context
import com.shagalalab.qqkeyboard.keyboard.data.db.SeedDictionary
import com.shagalalab.qqkeyboard.keyboard.data.db.UserDictionary

class SuggestionRepository(context: Context) {

    private val seed = SeedDictionary(context)
    private val user = UserDictionary(context)

    // Blocking — call from IO dispatcher
    fun getSuggestions(prefix: String, script: String, limit: Int = 5): List<String> {
        if (prefix.isEmpty()) return emptyList()

        val seedResults = seed.query(prefix, script, 10)
        val userResults = user.query(prefix, script, 10)

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
