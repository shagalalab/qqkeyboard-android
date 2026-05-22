package com.shagalalab.qqkeyboard.keyboard.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDictionary(context: Context) : SQLiteOpenHelper(context, "user_words.db", null, 1) {

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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun query(prefix: String, script: String, limit: Int = 10): List<Pair<String, Int>> {
        val result = mutableListOf<Pair<String, Int>>()
        readableDatabase.rawQuery(
            "SELECT word, frequency FROM user_words WHERE word LIKE ? AND script = ? ORDER BY frequency DESC LIMIT ?",
            arrayOf("$prefix%", script, limit.toString())
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
}
