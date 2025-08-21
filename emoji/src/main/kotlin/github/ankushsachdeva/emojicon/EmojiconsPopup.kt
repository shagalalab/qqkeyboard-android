package github.ankushsachdeva.emojicon

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.ImageButton
import android.widget.PopupWindow
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener
import github.ankushsachdeva.emojicon.emoji.Emojicon
import github.ankushsachdeva.emojicon.emoji.Nature
import github.ankushsachdeva.emojicon.emoji.Objects
import github.ankushsachdeva.emojicon.emoji.People
import github.ankushsachdeva.emojicon.emoji.Places
import github.ankushsachdeva.emojicon.emoji.Symbols

class EmojiconsPopup(
    private val rootView: View,
    private val context: Context
) : PopupWindow(context), ViewPager.OnPageChangeListener, EmojiconRecents {

    private var emojiTabLastSelectedIndex = -1
    private lateinit var emojiTabs: Array<View>
    private lateinit var recentsManager: EmojiconRecentsManager
    private var keyboardHeight = 0
    private var pendingOpen = false
    private var isOpened = false
    
    var onEmojiconClickedListener: OnEmojiconClickedListener? = null
    var onEmojiconBackspaceClickedListener: OnEmojiconBackspaceClickedListener? = null
    var onSoftKeyboardOpenCloseListener: OnSoftKeyboardOpenCloseListener? = null

    private val unselectedTabIconColor: Int
    private val selectedTabIconColor: Int

    private lateinit var emojisPager: ViewPager

    init {
        val customView = createCustomView()
        contentView = customView
        softInputMode = LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        
        // Default size
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = context.resources.getDimension(R.dimen.keyboard_height).toInt()
        setBackgroundDrawable(null)

        unselectedTabIconColor = context.resources.getColor(R.color.emojicons_unselected_tab, null)
        selectedTabIconColor = context.resources.getColor(R.color.emojicons_selected_tab, null)
    }

    /**
     * Use this function to show the emoji popup.
     * NOTE: Since, the soft keyboard sizes are variable on different android devices, the
     * library needs you to open the soft keyboard atleast once before calling this function.
     * If that is not possible see showAtBottomPending() function.
     */
    private fun showAtBottom() {
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0)
    }

    /**
     * Use this function when the soft keyboard has not been opened yet. This
     * will show the emoji popup after the keyboard is up next time.
     * Generally, you will be calling InputMethodManager.showSoftInput function after
     * calling this function.
     */
    fun showAtBottomPending() {
        if (isKeyboardOpen()) {
            showAtBottom()
        } else {
            pendingOpen = true
        }
    }

    /**
     * @return Returns true if the soft keyboard is open, false otherwise.
     */
    private fun isKeyboardOpen(): Boolean = isOpened

    /**
     * Dismiss the popup
     */
    override fun dismiss() {
        super.dismiss()
        EmojiconRecentsManager.getInstance(context).saveRecents()
    }

    /**
     * Call this function to resize the emoji popup according to your soft keyboard size
     */
    fun setSizeForSoftKeyboard() {
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)

            val screenHeight = usableScreenHeight
            var heightDifference = screenHeight - (r.bottom - r.top)
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                heightDifference -= context.resources.getDimensionPixelSize(resourceId)
            }
            
            if (heightDifference > 100) {
                keyboardHeight = heightDifference
                height = keyboardHeight
                if (!isOpened) {
                    onSoftKeyboardOpenCloseListener?.onKeyboardOpen(keyboardHeight)
                }
                isOpened = true
                if (pendingOpen) {
                    showAtBottom()
                    pendingOpen = false
                }
            } else {
                isOpened = false
                onSoftKeyboardOpenCloseListener?.onKeyboardClose()
            }
        }
    }

    private val usableScreenHeight: Int
        get() {
            val metrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.heightPixels
        }

    private fun createCustomView(): View {
        val recents: EmojiconRecents = this
        val view = LayoutInflater.from(context).inflate(R.layout.emojicons, null, false)

        emojisPager = view.findViewById(R.id.emojis_pager)
        emojisPager.addOnPageChangeListener(this)

        val mEmojisAdapter: PagerAdapter = EmojisPagerAdapter(
            listOf(
                EmojiconRecentsGridView(context, null, null, this),
                EmojiconGridView(context, People.data, recents, this),
                EmojiconGridView(context, Nature.data, recents, this),
                EmojiconGridView(context, Objects.data, recents, this),
                EmojiconGridView(context, Places.data, recents, this),
                EmojiconGridView(context, Symbols.data, recents, this)
            )
        )
        emojisPager.adapter = mEmojisAdapter

        emojiTabs = arrayOf(
            view.findViewById(R.id.emojis_tab_0_recents),
            view.findViewById(R.id.emojis_tab_1_people),
            view.findViewById(R.id.emojis_tab_2_nature),
            view.findViewById(R.id.emojis_tab_3_objects),
            view.findViewById(R.id.emojis_tab_4_cars),
            view.findViewById(R.id.emojis_tab_5_punctuation)
        )
        
        emojiTabs.forEachIndexed { index, tab ->
            tab.setOnClickListener { emojisPager.currentItem = index }
        }

        val backspaceButton: ImageButton = view.findViewById(R.id.emojis_backspace)
        backspaceButton.setOnTouchListener(
            RepeatListener(1000, 50) { v ->
                onEmojiconBackspaceClickedListener?.onEmojiconBackspaceClicked(v)
            }
        )

        // Hide Emoticons with keyboard icon.
        val keyboardButton: ImageButton = view.findViewById(R.id.emojis_keyboard_image)
        keyboardButton.setOnClickListener { dismiss() }

        // Get last selected page
        recentsManager = EmojiconRecentsManager.getInstance(view.context)
        var page = recentsManager.getRecentPage()
        // Last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (page == 0 && recentsManager.size == 0) {
            page = 1
        }

        if (page == 0) {
            onPageSelected(page)
        } else {
            emojisPager.currentItem = page
        }
        return view
    }

    override fun addRecentEmoji(context: Context, emojicon: Emojicon) {
        val fragment = (emojisPager.adapter as EmojisPagerAdapter).recentFragment
        fragment?.addRecentEmoji(context, emojicon)
    }

    override fun onPageScrolled(i: Int, v: Float, i2: Int) {
        // Empty implementation
    }

    override fun onPageSelected(i: Int) {
        if (emojiTabLastSelectedIndex == i) {
            return
        }
        when (i) {
            in 0..5 -> {
                if (emojiTabLastSelectedIndex in 0 until emojiTabs.size) {
                    tintUnselected(emojiTabLastSelectedIndex)
                }
                tintSelected(i)
                emojiTabLastSelectedIndex = i
                recentsManager.setRecentPage(i)
            }
        }
    }

    override fun onPageScrollStateChanged(i: Int) {
        // Empty implementation
    }

    private class EmojisPagerAdapter(
        private val views: List<EmojiconGridView>
    ) : PagerAdapter() {

        val recentFragment: EmojiconRecentsGridView?
            get() = views.filterIsInstance<EmojiconRecentsGridView>().firstOrNull()

        override fun getCount(): Int = views.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val v = views[position].rootView
            container.addView(v, 0)
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View)
        }

        override fun isViewFromObject(view: View, key: Any): Boolean = key == view
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     *
     * Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    class RepeatListener(
        private var initialInterval: Int,
        private val normalInterval: Int,
        private val clickListener: View.OnClickListener
    ) : View.OnTouchListener {

        private val handler = Handler()
        private var downView: View? = null

        private val handlerRunnable = object : Runnable {
            override fun run() {
                val view = downView ?: return
                handler.removeCallbacksAndMessages(view)
                handler.postAtTime(this, view, SystemClock.uptimeMillis() + normalInterval)
                clickListener.onClick(view)
            }
        }

        init {
            require(initialInterval >= 0 && normalInterval >= 0) { "negative interval" }
        }

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    downView = view
                    handler.removeCallbacks(handlerRunnable)
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval)
                    clickListener.onClick(view)
                    true
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_OUTSIDE -> {
                    handler.removeCallbacksAndMessages(downView)
                    downView = null
                    true
                }
                else -> false
            }
        }
    }

    private fun tintSelected(tabIndex: Int) {
        val b = emojiTabs[tabIndex] as ImageButton
        b.post { b.setColorFilter(selectedTabIconColor) }
    }

    private fun tintUnselected(tabIndex: Int) {
        (emojiTabs[tabIndex] as ImageButton).setColorFilter(unselectedTabIconColor)
    }

    interface OnEmojiconBackspaceClickedListener {
        fun onEmojiconBackspaceClicked(v: View)
    }

    interface OnSoftKeyboardOpenCloseListener {
        fun onKeyboardOpen(keyboardHeight: Int)
        fun onKeyboardClose()
    }
}