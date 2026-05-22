package com.shagalalab.qqkeyboard.keyboard.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDictionary(context: Context) : SQLiteOpenHelper(context, "user_words.db", null, 2) {

    companion object {
        private const val HALF_LIFE_DAYS = 30.0
    }

    private fun decayedFrequency(frequency: Int, lastUsed: Long): Int {
        val ageDays = (System.currentTimeMillis() - lastUsed) / 86_400_000.0
        val decay = Math.pow(2.0, -ageDays / HALF_LIFE_DAYS)
        return (frequency * decay).toInt()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE user_words (
                word      TEXT,
                script    TEXT,
                frequency INTEGER DEFAULT 1,
                last_used INTEGER,
                PRIMARY KEY (word, script)
            )
            """
        )
        db.execSQL("CREATE INDEX idx_user_word ON user_words(word, script)")
        db.execSQL(
            """
            CREATE TABLE user_bigrams (
                prefix    TEXT,
                next_word TEXT,
                script    TEXT,
                frequency INTEGER DEFAULT 1,
                last_used INTEGER,
                PRIMARY KEY (prefix, next_word, script)
            )
            """
        )
        db.execSQL("CREATE INDEX idx_user_bigram ON user_bigrams(prefix, script)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS user_bigrams (
                    prefix    TEXT,
                    next_word TEXT,
                    script    TEXT,
                    frequency INTEGER DEFAULT 1,
                    last_used INTEGER,
                    PRIMARY KEY (prefix, next_word, script)
                )
                """
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_user_bigram ON user_bigrams(prefix, script)")
        }
    }

    fun query(prefix: String, script: String, limit: Int = 10): List<Pair<String, Int>> {
        val result = mutableListOf<Pair<String, Int>>()
        readableDatabase.rawQuery(
            "SELECT word, frequency, last_used FROM user_words WHERE word LIKE ? AND script = ? ORDER BY frequency DESC LIMIT ?",
            arrayOf("$prefix%", script, limit.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursor.getString(0) to decayedFrequency(cursor.getInt(1), cursor.getLong(2)))
            }
        }
        return result
    }

    fun queryPrefixes(prefixes: List<String>, script: String, limit: Int = 10): List<Pair<String, Int>> {
        if (prefixes.isEmpty()) return emptyList()
        val result = mutableListOf<Pair<String, Int>>()
        val placeholders = prefixes.joinToString(" OR ") { "word LIKE ?" }
        val args = (prefixes.map { "$it%" } + listOf(script, limit.toString())).toTypedArray()
        readableDatabase.rawQuery(
            "SELECT word, frequency, last_used FROM user_words WHERE ($placeholders) AND script = ? ORDER BY frequency DESC LIMIT ?",
            args
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursor.getString(0) to decayedFrequency(cursor.getInt(1), cursor.getLong(2)))
            }
        }
        return result
    }

    fun queryBigrams(lastWord: String, script: String, limit: Int = 10): List<Pair<String, Int>> {
        val result = mutableListOf<Pair<String, Int>>()
        readableDatabase.rawQuery(
            "SELECT next_word, frequency FROM user_bigrams WHERE prefix = ? AND script = ? ORDER BY frequency DESC LIMIT ?",
            arrayOf(lastWord, script, limit.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursor.getString(0) to cursor.getInt(1))
            }
        }
        return result
    }

    fun learnWord(word: String, script: String) {
        val now = System.currentTimeMillis()
        val cv = ContentValues().apply {
            put("word", word)
            put("script", script)
            put("frequency", 1)
            put("last_used", now)
        }
        val inserted = writableDatabase.insertWithOnConflict(
            "user_words", null, cv, SQLiteDatabase.CONFLICT_IGNORE
        )
        if (inserted == -1L) {
            writableDatabase.execSQL(
                "UPDATE user_words SET frequency = frequency + 1, last_used = ? WHERE word = ? AND script = ?",
                arrayOf<Any>(now, word, script)
            )
        }
    }

    fun learnBigram(prefix: String, nextWord: String, script: String) {
        val now = System.currentTimeMillis()
        val cv = ContentValues().apply {
            put("prefix", prefix)
            put("next_word", nextWord)
            put("script", script)
            put("frequency", 1)
            put("last_used", now)
        }
        val inserted = writableDatabase.insertWithOnConflict(
            "user_bigrams", null, cv, SQLiteDatabase.CONFLICT_IGNORE
        )
        if (inserted == -1L) {
            writableDatabase.execSQL(
                "UPDATE user_bigrams SET frequency = frequency + 1, last_used = ? WHERE prefix = ? AND next_word = ? AND script = ?",
                arrayOf<Any>(now, prefix, nextWord, script)
            )
        }
    }
}
