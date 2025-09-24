package com.shagalalab.qqkeyboard.keyboard.service

import android.inputmethodservice.InputMethodService
import android.view.View
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

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        keyboardViewModel = KeyboardViewModel()
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
        }
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
