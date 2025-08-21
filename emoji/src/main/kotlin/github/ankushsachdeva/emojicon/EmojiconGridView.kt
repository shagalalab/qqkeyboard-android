package github.ankushsachdeva.emojicon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.GridView
import github.ankushsachdeva.emojicon.emoji.Emojicon
import github.ankushsachdeva.emojicon.emoji.People

open class EmojiconGridView(
    context: Context,
    emojicons: Array<Emojicon>?,
    recents: EmojiconRecents?,
    protected val emojiconPopup: EmojiconsPopup
) {
    
    val rootView: View = LayoutInflater.from(context).inflate(R.layout.emojicon_grid, null)
    private var recents: EmojiconRecents? = null
    private val data: Array<Emojicon>

    init {
        setRecents(recents)
        val gridView = rootView.findViewById<GridView>(R.id.emojiGridView)
        data = emojicons ?: People.data
        
        val mAdapter = EmojiAdapter(rootView.context, data)
        mAdapter.setEmojiClickListener(object : OnEmojiconClickedListener {
            override fun onEmojiconClicked(emojicon: Emojicon) {
                emojiconPopup.onEmojiconClickedListener?.onEmojiconClicked(emojicon)
                recents?.addRecentEmoji(rootView.context, emojicon)
            }
        })
        gridView.adapter = mAdapter
    }

    private fun setRecents(recents: EmojiconRecents?) {
        this@EmojiconGridView.recents = recents
    }

    interface OnEmojiconClickedListener {
        fun onEmojiconClicked(emojicon: Emojicon)
    }
}
