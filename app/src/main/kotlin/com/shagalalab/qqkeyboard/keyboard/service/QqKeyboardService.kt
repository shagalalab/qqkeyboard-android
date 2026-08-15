package com.shagalalab.qqkeyboard.keyboard.service

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.shagalalab.qqkeyboard.keyboard.compose.QqKeyboard
import com.shagalalab.qqkeyboard.keyboard.viewmodel.KeyboardViewModel
import com.shagalalab.qqkeyboard.ui.theme.QqKeyboardTheme

class QqKeyboardService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner {

    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private lateinit var keyboardViewModel: KeyboardViewModel

    private var inputView: View? = null

    private val clipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
        captureClipboard()
    }

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        keyboardViewModel = KeyboardViewModel()
        keyboardViewModel.onShowInputMethodPicker = {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }
        clipboardManager.addPrimaryClipChangedListener(clipChangedListener)
    }

    override fun onCreateInputView(): View {
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }
        return object : AbstractComposeView(this) {
            @Composable
            override fun Content() {
                QqKeyboardTheme {
                    QqKeyboard(keyboardViewModel)
                }
            }
        }.also { inputView = it }
    }

    /**
     * The keyboard reserves room for the navigation bar with a spacer sized by
     * `WindowInsets.systemBars`. Compose only refreshes that state when the platform dispatches
     * `onApplyWindowInsets` to the ComposeView, and an IME window does not reliably receive such a
     * dispatch after a rotation — the inset measured in the previous orientation sticks. Landscape
     * reports a much smaller bottom inset than portrait, so rotating back left the bottom key row
     * behind the navigation bar, where it could not be tapped.
     *
     * Asking for a fresh dispatch here gives Compose the new values. It runs twice because the
     * window has not necessarily been resized yet when this callback fires; the posted pass
     * catches the values that only settle after the new layout.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        requestApplyInsets()
        inputView?.post { requestApplyInsets() }
    }

    private fun requestApplyInsets() {
        window?.window?.decorView?.requestApplyInsets()
        inputView?.requestApplyInsets()
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        keyboardViewModel.initialize(this)
        keyboardViewModel.setInputConnection(currentInputConnection)
        keyboardViewModel.setEditorInfo(info)
        // Runs after setEditorInfo so the view model judges the clip against the field we are
        // actually about to type into. This poll is the path that catches everything copied while
        // the keyboard was hidden, which is the common case; the change listener only covers copies
        // made with the keyboard already open.
        captureClipboard()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        keyboardViewModel.setInputConnection(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        clipboardManager.removePrimaryClipChangedListener(clipChangedListener)
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    private fun captureClipboard() {
        val (text, copiedAt) = readPrimaryClip() ?: return
        keyboardViewModel.onClipboardChanged(text, copiedAt)
    }

    /**
     * Reads the current clip, or null if there is nothing worth offering back.
     *
     * Since Android 10 the clipboard can only be read by an app that holds focus or is the default
     * input method. We qualify through the input-method exemption, but only while we really are the
     * selected keyboard — so a null result is an ordinary outcome here, not a failure. Some vendor
     * builds raise a SecurityException in that situation instead of returning null.
     */
    private fun readPrimaryClip(): Pair<String, Long>? {
        val clip = try {
            clipboardManager.primaryClip
        } catch (_: SecurityException) {
            null
        } ?: return null

        if (clip.itemCount == 0) return null

        val description = clip.description ?: return null
        val isText = description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) ||
            description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)
        if (!isText || description.isSensitive()) return null

        // Reading item.text directly rather than coerceToText() keeps us from firing a
        // ContentResolver query out of the keyboard process for a URI-backed item.
        val text = clip.getItemAt(0)?.text?.toString() ?: return null
        if (text.isBlank()) return null

        // getTimestamp() reports when the text was actually copied, which is what expiry should be
        // measured from — and it lets the listener and the poll record the same copy just once.
        val copiedAt = description.timestamp.takeIf { it > 0L } ?: System.currentTimeMillis()
        return text to copiedAt
    }

    /**
     * Whether the source app marked this clip as sensitive — a password, a one-time code.
     *
     * Both keys are checked by name rather than through [ClipDescription.EXTRA_IS_SENSITIVE], which
     * only exists from API 33: apps and AndroidX set the same flags on older releases, and there is
     * no reason to ignore them there.
     */
    private fun ClipDescription.isSensitive(): Boolean {
        val extras = extras ?: return false
        return extras.getBoolean(EXTRA_IS_SENSITIVE, false) ||
            extras.getBoolean(EXTRA_IS_SENSITIVE_ANDROIDX, false)
    }

    private fun handleLifecycleEvent(event: Lifecycle.Event) = lifecycleRegistry.handleLifecycleEvent(event)

    companion object {
        private const val EXTRA_IS_SENSITIVE = "android.content.extra.IS_SENSITIVE"
        private const val EXTRA_IS_SENSITIVE_ANDROIDX = "androidx.core.content.extra.IS_SENSITIVE"
    }
}
