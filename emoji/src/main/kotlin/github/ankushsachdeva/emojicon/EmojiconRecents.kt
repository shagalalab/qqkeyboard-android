package github.ankushsachdeva.emojicon

import android.content.Context
import github.ankushsachdeva.emojicon.emoji.Emojicon

interface EmojiconRecents {
    fun addRecentEmoji(context: Context, emojicon: Emojicon)
}
