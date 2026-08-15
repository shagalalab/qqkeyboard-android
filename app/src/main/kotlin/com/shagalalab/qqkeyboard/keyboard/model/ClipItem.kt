package com.shagalalab.qqkeyboard.keyboard.model

import androidx.compose.runtime.Immutable

/**
 * One entry in the clipboard history.
 *
 * [copiedAt] is the moment the text was placed on the system clipboard — taken from
 * `ClipDescription.getTimestamp()` when the platform supplies one — not the moment we recorded it.
 * Expiry is measured from that timestamp, so a clip copied an hour before the keyboard next opened
 * is already stale when we first see it.
 */
@Immutable
data class ClipItem(
    val id: Long,
    val text: String,
    val copiedAt: Long,
    val pinned: Boolean,
)
