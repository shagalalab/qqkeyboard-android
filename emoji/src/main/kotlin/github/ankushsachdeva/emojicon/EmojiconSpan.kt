package github.ankushsachdeva.emojicon

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan
import androidx.core.content.res.ResourcesCompat

internal class EmojiconSpan(
    private val context: Context,
    private val resourceId: Int,
    private val size: Int
) : DynamicDrawableSpan() {
    
    private var mDrawable: Drawable? = null

    override fun getDrawable(): Drawable? {
        if (mDrawable == null) {
            try {
                mDrawable = ResourcesCompat.getDrawable(context.resources, resourceId, null)
                mDrawable?.setBounds(0, 0, size, size)
            } catch (e: Exception) {
                // swallow
            }
        }
        return mDrawable
    }
}
