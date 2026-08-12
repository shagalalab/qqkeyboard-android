package com.shagalalab.qqkeyboard.keyboard.service

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

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        keyboardViewModel = KeyboardViewModel()
        keyboardViewModel.onShowInputMethodPicker = {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }
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
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        keyboardViewModel.setInputConnection(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    private fun handleLifecycleEvent(event: Lifecycle.Event) = lifecycleRegistry.handleLifecycleEvent(event)
}
