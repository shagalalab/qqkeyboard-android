package com.shagalalab.qqkeyboard.keyboard.data

import android.content.Context
import com.shagalalab.qqkeyboard.keyboard.data.db.ClipboardDatabase
import com.shagalalab.qqkeyboard.keyboard.model.ClipItem

class ClipboardRepository(context: Context) {

    private val db = ClipboardDatabase(context)

    // Blocking — call from IO dispatcher
    fun clips(): List<ClipItem> = db.clips()

    /**
     * Records copied text, ignoring anything not worth keeping.
     *
     * Over-long clips are skipped rather than truncated: a truncated paste silently loses content,
     * which is worse than the clip simply not being offered — the system clipboard still holds the
     * full text either way.
     */
    // Blocking — call from IO dispatcher
    fun capture(text: String, copiedAt: Long) {
        if (text.isBlank() || text.length > MAX_CLIP_LENGTH) return
        db.capture(text, copiedAt)
    }

    // Blocking — call from IO dispatcher
    fun setPinned(id: Long, pinned: Boolean) = db.setPinned(id, pinned)

    // Blocking — call from IO dispatcher
    fun delete(id: Long) = db.delete(id)

    // Blocking — call from IO dispatcher
    fun clearAll() = db.clearAll()

    // Blocking — call from IO dispatcher
    fun purge() = db.purge()

    companion object {
        private const val MAX_CLIP_LENGTH = 20_000
    }
}
