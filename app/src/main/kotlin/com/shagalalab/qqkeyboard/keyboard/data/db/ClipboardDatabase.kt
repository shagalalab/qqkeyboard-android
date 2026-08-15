package com.shagalalab.qqkeyboard.keyboard.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.shagalalab.qqkeyboard.keyboard.model.ClipItem

/**
 * Stores copied text so the keyboard can offer it back later.
 *
 * Unpinned clips live for [UNPINNED_TTL_MS] from the moment they were copied; pinned clips are kept
 * until the user deletes them. Expiry is applied both when writing (see [purge]) and in the read
 * query, so an expired clip can never be shown even if no write has happened to trigger a purge.
 */
class ClipboardDatabase(context: Context) : SQLiteOpenHelper(context, "clipboard.db", null, 1) {

    companion object {
        /** How long an unpinned clip survives after being copied. */
        const val UNPINNED_TTL_MS = 60L * 60L * 1000L

        /** Backstop caps. Expiry does the real work; these only bound pathological cases. */
        private const val MAX_UNPINNED = 50
        private const val MAX_PINNED = 100

        private const val TABLE = "clips"

        // Pinned clips come first, ordered by when they were pinned; the rest by when they were
        // copied. Both sections are newest-first.
        private const val ORDER_BY =
            "pinned DESC, CASE WHEN pinned = 1 THEN pinned_at ELSE copied_at END DESC"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE clips (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                text      TEXT    NOT NULL,
                copied_at INTEGER NOT NULL,
                pinned    INTEGER NOT NULL DEFAULT 0,
                pinned_at INTEGER
            )
            """
        )
        // Copying the same text again should bump the existing row rather than pile up duplicates.
        db.execSQL("CREATE UNIQUE INDEX idx_clip_text ON clips(text)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Version 1 is the first release; nothing to migrate yet.
    }

    /**
     * Records [text] as copied at [copiedAt].
     *
     * If the same text is already stored, its timestamp is refreshed and it returns to the top of
     * the recent section. The existing row is updated rather than replaced so that re-copying a
     * clip the user has pinned does not silently unpin it.
     */
    fun capture(text: String, copiedAt: Long) {
        val cv = ContentValues().apply {
            put("text", text)
            put("copied_at", copiedAt)
            put("pinned", 0)
        }
        val inserted = writableDatabase.insertWithOnConflict(
            TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE
        )
        if (inserted == -1L) {
            writableDatabase.execSQL(
                "UPDATE clips SET copied_at = ? WHERE text = ?",
                arrayOf<Any>(copiedAt, text)
            )
        }
        purge()
    }

    /** Every clip still worth showing, pinned section first. */
    fun clips(): List<ClipItem> {
        val cutoff = System.currentTimeMillis() - UNPINNED_TTL_MS
        val result = mutableListOf<ClipItem>()
        readableDatabase.rawQuery(
            "SELECT id, text, copied_at, pinned FROM clips WHERE pinned = 1 OR copied_at >= ? ORDER BY $ORDER_BY",
            arrayOf(cutoff.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    ClipItem(
                        id = cursor.getLong(0),
                        text = cursor.getString(1),
                        copiedAt = cursor.getLong(2),
                        pinned = cursor.getInt(3) == 1,
                    )
                )
            }
        }
        return result
    }

    fun setPinned(id: Long, pinned: Boolean) {
        val now = System.currentTimeMillis()
        val cv = ContentValues().apply {
            put("pinned", if (pinned) 1 else 0)
            if (pinned) {
                put("pinned_at", now)
            } else {
                putNull("pinned_at")
                // Unpinning restarts the expiry clock. Measuring from the original copy time would
                // make a long-pinned clip vanish the instant it is unpinned, which reads as a
                // delete rather than an unpin — and leaves no way back from a mis-tap.
                put("copied_at", now)
            }
        }
        writableDatabase.update(TABLE, cv, "id = ?", arrayOf(id.toString()))
    }

    fun delete(id: Long) {
        writableDatabase.delete(TABLE, "id = ?", arrayOf(id.toString()))
    }

    fun clearAll() {
        writableDatabase.delete(TABLE, null, null)
    }

    /** Drops expired unpinned clips, then trims each section to its cap. */
    fun purge() {
        val cutoff = System.currentTimeMillis() - UNPINNED_TTL_MS
        writableDatabase.delete(TABLE, "pinned = 0 AND copied_at < ?", arrayOf(cutoff.toString()))
        trim(pinned = false, keep = MAX_UNPINNED, orderBy = "copied_at DESC")
        trim(pinned = true, keep = MAX_PINNED, orderBy = "pinned_at DESC")
    }

    private fun trim(pinned: Boolean, keep: Int, orderBy: String) {
        val flag = if (pinned) "1" else "0"
        writableDatabase.execSQL(
            """
            DELETE FROM clips
            WHERE pinned = $flag AND id NOT IN (
                SELECT id FROM clips WHERE pinned = $flag ORDER BY $orderBy LIMIT ?
            )
            """,
            arrayOf<Any>(keep)
        )
    }
}
