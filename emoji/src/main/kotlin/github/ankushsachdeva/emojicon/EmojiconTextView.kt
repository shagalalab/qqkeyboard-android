package github.ankushsachdeva.emojicon

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes

class EmojiconTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    private var emojiconSize: Int = 0
    private var textStart: Int = 0
    private var textLength: Int = -1

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs == null) {
            emojiconSize = textSize.toInt()
        } else {
            context.withStyledAttributes(attrs, R.styleable.EmojiconTextView) {
                emojiconSize =
                    getDimension(R.styleable.EmojiconTextView_emojiconSize, textSize).toInt()
                textStart = getInteger(R.styleable.EmojiconTextView_emojiconTextStart, 0)
                textLength = getInteger(R.styleable.EmojiconTextView_emojiconTextLength, -1)
            }
        }
        text = text
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        val builder = SpannableStringBuilder(text)
        EmojiconHandler.addEmojis(context, builder, emojiconSize, textStart, textLength)
        super.setText(builder, type)
    }

    /**
     * Set the size of emojicon in pixels.
     */
    fun setEmojiconSize(pixels: Int) {
        emojiconSize = pixels
    }
}
