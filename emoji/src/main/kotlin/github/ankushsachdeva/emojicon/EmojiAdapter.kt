package github.ankushsachdeva.emojicon

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener
import github.ankushsachdeva.emojicon.emoji.Emojicon

internal class EmojiAdapter : ArrayAdapter<Emojicon> {
    
    private var emojiClickListener: OnEmojiconClickedListener? = null

    constructor(context: Context, data: List<Emojicon>) : super(context, R.layout.emojicon_item, data)

    constructor(context: Context, data: Array<Emojicon>) : super(context, R.layout.emojicon_item, data)

    fun setEmojiClickListener(listener: OnEmojiconClickedListener?) {
        this.emojiClickListener = listener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            v = View.inflate(context, R.layout.emojicon_item, null)
            val holder = ViewHolder()
            holder.icon = v.findViewById(R.id.emojicon_icon)
            v.tag = holder
        }
        val emoji = getItem(position)
        (v?.tag as? ViewHolder)?.icon?.apply {
            text = emoji?.emoji
            setOnClickListener {
                emoji?.let { emojiClickListener?.onEmojiconClicked(it) }
            }
        }

        return v
    }

    internal class ViewHolder {
        var icon: TextView? = null
    }
}
