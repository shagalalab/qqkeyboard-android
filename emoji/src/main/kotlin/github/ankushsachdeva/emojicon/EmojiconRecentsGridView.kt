package github.ankushsachdeva.emojicon

import android.content.Context
import android.widget.GridView
import github.ankushsachdeva.emojicon.emoji.Emojicon

class EmojiconRecentsGridView(
    context: Context,
    emojicons: Array<Emojicon>?,
    recents: EmojiconRecents?,
    emojiconsPopup: EmojiconsPopup
) : EmojiconGridView(context, emojicons, recents, emojiconsPopup), EmojiconRecents {
    
    private val adapter: EmojiAdapter

    init {
        val recentsManager = EmojiconRecentsManager.getInstance(rootView.context)
        adapter = EmojiAdapter(rootView.context, recentsManager)
        adapter.setEmojiClickListener(object : OnEmojiconClickedListener {
            override fun onEmojiconClicked(emojicon: Emojicon) {
                emojiconPopup.onEmojiconClickedListener?.onEmojiconClicked(emojicon)
            }
        })
        val gridView = rootView.findViewById<GridView>(R.id.emojiGridView)
        gridView.adapter = adapter
    }

    override fun addRecentEmoji(context: Context, emojicon: Emojicon) {
        val recents = EmojiconRecentsManager.getInstance(context)
        recents.push(emojicon)

        // notify dataset changed
        adapter.notifyDataSetChanged()
    }
}