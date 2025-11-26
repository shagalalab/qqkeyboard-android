package github.ankushsachdeva.emojicon

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import github.ankushsachdeva.emojicon.emoji.Emojicon
import java.util.StringTokenizer

class EmojiconRecentsManager private constructor(context: Context) : ArrayList<Emojicon>() {

    companion object {
        private const val PREFERENCE_NAME = "emojicon"
        private const val PREF_RECENTS = "recent_emojis"
        private const val PREF_PAGE = "recent_page"

        private val LOCK = Any()

        private var sInstance: EmojiconRecentsManager? = null

        internal fun getInstance(context: Context): EmojiconRecentsManager {
            return sInstance ?: synchronized(LOCK) {
                sInstance ?: EmojiconRecentsManager(context).also { sInstance = it }
            }
        }
    }

    private val mContext: Context = context.applicationContext

    init {
        loadRecents()
    }

    internal fun getRecentPage(): Int {
        return getPreferences().getInt(PREF_PAGE, 0)
    }

    internal fun setRecentPage(page: Int) {
        getPreferences().edit { putInt(PREF_PAGE, page) }
    }

    internal fun push(obj: Emojicon) {
        // FIXME totally inefficient way of adding the emoji to the adapter
        // TODO this should be probably replaced by a deque
        if (contains(obj)) {
            super.remove(obj)
        }
        add(0, obj)
    }

    override fun add(element: Emojicon): Boolean {
        return super.add(element)
    }

    override fun add(index: Int, element: Emojicon) {
        super.add(index, element)
    }

    override fun remove(element: Emojicon): Boolean {
        return super.remove(element)
    }

    private fun getPreferences(): SharedPreferences {
        return mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    private fun loadRecents() {
        val prefs = getPreferences()
        val str = prefs.getString(PREF_RECENTS, "") ?: ""
        val tokenizer = StringTokenizer(str, "~")
        while (tokenizer.hasMoreTokens()) {
            try {
                add(Emojicon(tokenizer.nextToken()))
            } catch (e: NumberFormatException) {
                // ignored
            }
        }
    }

    internal fun saveRecents() {
        val str = StringBuilder()
        val c = size
        for (i in 0 until c) {
            val e = get(i)
            str.append(e.emoji)
            if (i < (c - 1)) {
                str.append('~')
            }
        }
        val prefs = getPreferences()
        prefs.edit { putString(PREF_RECENTS, str.toString()) }
    }
}