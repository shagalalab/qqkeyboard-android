package com.shagalalab.qqkeyboard.keyboard.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.FileOutputStream

class SeedDictionary(context: Context) {

    private val db: SQLiteDatabase?

    init {
        db = try {
            val dbFile = context.getDatabasePath("seed.db")
            if (!dbFile.exists()) {
                dbFile.parentFile?.mkdirs()
                context.assets.open("seed.db").use { input ->
                    FileOutputStream(dbFile).use { input.copyTo(it) }
                }
            }
            SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: Exception) {
            null // seed.db not yet generated — suggestions degrade gracefully
        }
    }

    fun query(prefix: String, script: String, limit: Int = 10): List<Pair<String, Int>> {
        val database = db ?: return emptyList()
        val result = mutableListOf<Pair<String, Int>>()
        database.rawQuery(
            "SELECT word, frequency FROM seed_words WHERE word LIKE ? AND script = ? ORDER BY frequency DESC LIMIT ?",
            arrayOf("$prefix%", script, limit.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursor.getString(0) to cursor.getInt(1))
            }
        }
        return result
    }

    fun queryBigrams(lastWord: String, script: String, limit: Int = 10): List<Pair<String, Int>> {
        val database = db ?: return emptyList()
        val result = mutableListOf<Pair<String, Int>>()
        try {
            database.rawQuery(
                "SELECT next_word, frequency FROM bigrams WHERE prefix = ? AND script = ? ORDER BY frequency DESC LIMIT ?",
                arrayOf(lastWord, script, limit.toString())
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    result.add(cursor.getString(0) to cursor.getInt(1))
                }
            }
        } catch (_: Exception) {
            // bigrams table absent in older seed.db — degrade gracefully
        }
        return result
    }
}
