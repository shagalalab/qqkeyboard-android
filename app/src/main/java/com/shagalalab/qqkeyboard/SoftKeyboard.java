/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shagalalab.qqkeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Vibrator;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.shagalalab.qqkeyboard.util.SettingsUtil;

import static android.text.InputType.TYPE_CLASS_DATETIME;
import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_PHONE;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static android.text.InputType.TYPE_TEXT_VARIATION_URI;
import static android.text.InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS;

/**
 * Example of writing an input method for a soft keyboard.  This code is
 * focused on simplicity over completeness, so it should in no way be considered
 * to be a complete soft keyboard implementation.  Its purpose is to provide
 * a basic example for how you would get started writing an input method, to
 * be fleshed out as appropriate.
 */
public class SoftKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private static final String TAG = "SoftKeyboard";

    private InputMethodManager mInputMethodManager;
    private LatinKeyboardView mInputView;

    private StringBuilder mComposing = new StringBuilder();
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;

    private LatinKeyboard symbolsKeyboard;
    private LatinKeyboard numbersKeyboard;
    private LatinKeyboard numbersExtKeyboard;
    private LatinKeyboard qwertyKeyboard;
    private LatinKeyboard cyrillicKeyboard;
    private LatinKeyboard currentKeyboard;
    private LatinKeyboard prevKeyboard;
    private LatinKeyboard qwertyKeyboardFirstRowNumbers;
    private LatinKeyboard qwertyKeyboardFirstRowLetters;
    private LatinKeyboard cyrillicKeyboardFirstRowNumbers;
    private LatinKeyboard cyrillicKeyboardFirstRowLetters;
    private String mWordSeparators;
    private boolean isSoundEnabled = true;
    private boolean isVibrationEnabled = true;
    private AudioManager am;
    private Vibrator vb;
    private int soundVolume;
    private int vibrationLevel;
    private boolean isDefaultKeyboardLatin = true;
    private boolean isKeyboardWithFirstRowNumber = true;

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        if (qwertyKeyboard != null || cyrillicKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }

        qwertyKeyboardFirstRowNumbers = new LatinKeyboard(this, R.xml.kbd_qwerty_first_row_numbers);
        qwertyKeyboardFirstRowLetters = new LatinKeyboard(this, R.xml.kbd_qwerty_first_row_letters);
        cyrillicKeyboardFirstRowNumbers = new LatinKeyboard(this, R.xml.kbd_cyrillic_first_row_numbers);
        cyrillicKeyboardFirstRowLetters = new LatinKeyboard(this, R.xml.kbd_cyrillic_first_row_letters);
        symbolsKeyboard = new LatinKeyboard(this, R.xml.kbd_symbols);
        numbersKeyboard = new LatinKeyboard(this, R.xml.kbd_numbers);
        numbersExtKeyboard = new LatinKeyboard(this, R.xml.kbd_numbers_ext);

        setupKeyboards(SettingsUtil.isKeyboardWithFirstRowNumbers(getBaseContext()));
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override
    public View onCreateInputView() {
        boolean isLightTheme = SettingsUtil.isLightTheme(getBaseContext());
        if (isLightTheme) {
            mInputView = (LatinKeyboardView) getLayoutInflater().inflate(R.layout.input_light, null);
        } else {
            mInputView = (LatinKeyboardView) getLayoutInflater().inflate(R.layout.input_dark, null);
        }
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setPreviewEnabled(false);

        isDefaultKeyboardLatin = SettingsUtil.getDefaultKeyboard(getBaseContext()).equalsIgnoreCase(getString(R.string.pref_keypress_layout_latin));
        isKeyboardWithFirstRowNumber = SettingsUtil.isKeyboardWithFirstRowNumbers(getBaseContext());
        setupKeyboards(isKeyboardWithFirstRowNumber);
        setKeyboard(currentKeyboard);

        // Dismiss popup keyboard on touching outside
        mInputView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mInputView.closing(); // Close popup keyboard if it's showing
                }
                return false;
            }
        });

        return mInputView;
    }

    private void setKeyboard(LatinKeyboard nextKeyboard) {
        mInputView.setKeyboard(nextKeyboard);
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override
    public View onCreateCandidatesView() {
        return super.onCreateCandidatesView();
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0);

        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }

        isDefaultKeyboardLatin = SettingsUtil.getDefaultKeyboard(getBaseContext()).equalsIgnoreCase(getString(R.string.pref_keypress_layout_latin));
        isKeyboardWithFirstRowNumber = SettingsUtil.isKeyboardWithFirstRowNumbers(getBaseContext());

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case TYPE_CLASS_NUMBER:
            case TYPE_CLASS_DATETIME:
                currentKeyboard = numbersKeyboard;
                break;

            case TYPE_CLASS_PHONE:
                currentKeyboard = numbersKeyboard;
                break;

            case TYPE_CLASS_TEXT:
                int inputType = attribute.inputType & InputType.TYPE_MASK_VARIATION;

                currentKeyboard = getDefaultKeyboard();
                if (mInputView == null) {
                    break;
                }

                if (inputType == TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || inputType == TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS) {
                    currentKeyboard = qwertyKeyboard;
                    mInputView.setKeyboardType(currentKeyboard, LatinKeyboardView.Type.EMAIL);
                    mInputView.setShifted(false);
                } else if (inputType == TYPE_TEXT_VARIATION_URI) {
                    currentKeyboard = qwertyKeyboard;
                    mInputView.setKeyboardType(currentKeyboard, LatinKeyboardView.Type.WEB);
                    mInputView.setShifted(false);
                } else {
                    // We also want to look at the current state of the editor
                    // to decide whether our alphabetic keyboard should start out
                    // shifted.
                    mInputView.setKeyboardType(currentKeyboard, LatinKeyboardView.Type.NORMAL);
                }
                updateShiftKeyState(attribute);
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                currentKeyboard = getDefaultKeyboard();
                updateShiftKeyState(attribute);
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        currentKeyboard.setImeOptions(getResources(), attribute.imeOptions);

        isSoundEnabled = SettingsUtil.isKeySoundEnabled(getBaseContext());
        soundVolume = SettingsUtil.getSoundVolume(getBaseContext());
        isVibrationEnabled = SettingsUtil.isKeyVibrationEnabled(getBaseContext());
        vibrationLevel = SettingsUtil.getVibrationLevel(getBaseContext());
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override
    public void onFinishInput() {
        super.onFinishInput();

        // Clear current composing text and candidates.
        mComposing.setLength(0);

        currentKeyboard = getDefaultKeyboard();
        if (mInputView != null) {
            mInputView.closing();
        }
    }

    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        // Recreate input view.
        setInputView(onCreateInputView());

//        setKeyboard(currentKeyboard);
        mInputView.closing();
        final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {
                        return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_DEL:

                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;
            default:
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
                /*
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && (event.getMetaState()&KeyEvent.META_ALT_ON) != 0) {
                        // A silly example: in our input method, Alt+Space
                        // is a shortcut for 'android' in lower case.
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            // First, tell the editor that it is no longer in the
                            // shift state, since we are consuming this.
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                            keyDownUp(KeyEvent.KEYCODE_A);
                            keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O);
                            keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            // And we consume this event.
                            return true;
                        }
                    }
                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }*/
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState, keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null && mInputView != null && (qwertyKeyboard == mInputView.getKeyboard() ||
                cyrillicKeyboard == mInputView.getKeyboard())) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL && getCurrentInputConnection() != null) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            boolean shiftStatus = mCapsLock || caps != 0;
            mInputView.setShifted(shiftStatus);
            mInputView.toggleShiftKey(currentKeyboard, shiftStatus);
        }
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        try {
            getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
            getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
        } catch (Exception e) {

        }
    }

    // Implementation of KeyboardViewListener
    public void onKey(int primaryCode, int[] keyCodes) {
        if (isSoundEnabled) {
            tryPlayKeyDown();
        }

        if (isVibrationEnabled) {
            tryVibrate();
        }

        if (isWordSeparator(primaryCode)) {
            sendKey(primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
        } else if (primaryCode == LatinKeyboardView.KEYCODE_LANGUAGE_SWITCH) {
            handleLanguageSwitch();
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && mInputView != null) {
            Keyboard current = mInputView.getKeyboard();
            if (current == symbolsKeyboard) {
                setKeyboard(prevKeyboard == null ? getDefaultKeyboard() : prevKeyboard);
            } else if (current == numbersKeyboard) {
                prevKeyboard = (LatinKeyboard) current;
                setKeyboard(numbersExtKeyboard);
            } else if (current == numbersExtKeyboard) {
                prevKeyboard = (LatinKeyboard) current;
                setKeyboard(numbersKeyboard);
            } else {
                prevKeyboard = (LatinKeyboard) current;
                setKeyboard(symbolsKeyboard);
                symbolsKeyboard.setShifted(false);
            }
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleShift() {
        if (mInputView == null) {
            return;
        }

        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (qwertyKeyboard == currentKeyboard || cyrillicKeyboard == currentKeyboard) {
            // Alphabet keyboard
            checkToggleCapsLock();
            boolean shiftStatus = mCapsLock || !mInputView.isShifted();
            mInputView.setShifted(shiftStatus);
            mInputView.toggleShiftKey((LatinKeyboard) currentKeyboard, shiftStatus);
        }
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (mInputView.isShifted()) {
                if (primaryCode == 305) { // if we have shifted 'ı', its capitalized version should be 'Í'
                    primaryCode = 205;
                } else {
                    primaryCode = Character.toUpperCase(primaryCode);
                }
            }
        }

        getCurrentInputConnection().commitText(String.valueOf((char) primaryCode), 1);
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        mInputView.closing();
    }

    private void handleLanguageSwitch() {
        boolean shifted = mInputView.isShifted();

        int keyboardResId = ((LatinKeyboard) mInputView.getKeyboard()).getXmlLayoutResId();
        if (keyboardResId == R.xml.kbd_qwerty_first_row_numbers || keyboardResId == R.xml.kbd_qwerty_first_row_letters) {
            setKeyboard(cyrillicKeyboard);
        } else {
            setKeyboard(qwertyKeyboard);
        }

        mInputView.setShifted(shifted);
    }

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (mLastShiftTime + 500 > now) {
            mCapsLock = !mCapsLock;
            mLastShiftTime = 0;
        } else {
            mLastShiftTime = now;
            if (mCapsLock) {
                mCapsLock = false;
            }
        }
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char) code));
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeLeft() {
        handleBackspace();
    }

    @Override
    public void swipeDown() {
        handleClose();
    }

    @Override
    public void swipeUp() {
    }

    public void onPress(int primaryCode) {

    }

    public void onRelease(int primaryCode) {

    }
    
    private AudioManager getAudioManager() {
        if (am == null) {
            am = (AudioManager) getSystemService(AUDIO_SERVICE);
        }
        
        return am;
    }
    
    private Vibrator getVibrator() {
        if (vb == null) {
            vb = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }
        
        return vb;
    }

    private void tryPlayKeyDown() {
        //TODO: different sounds for keys
        AudioManager audioManager = getAudioManager();
        if (audioManager != null) {
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, soundVolume / 100);
        }
    }

    private void tryVibrate() {
        Vibrator vibrator = getVibrator();
        if (vibrator != null) {
            vibrator.vibrate(vibrationLevel);
        }
    }

    private LatinKeyboard getDefaultKeyboard() {
        return isDefaultKeyboardLatin ? qwertyKeyboard : cyrillicKeyboard;
    }

    private void setupKeyboards(boolean isKeyboardWithFirstRowNumber) {
        if (isKeyboardWithFirstRowNumber) {
            qwertyKeyboard = qwertyKeyboardFirstRowNumbers;
            cyrillicKeyboard = cyrillicKeyboardFirstRowNumbers;
        } else {
            qwertyKeyboard = qwertyKeyboardFirstRowLetters;
            cyrillicKeyboard = cyrillicKeyboardFirstRowLetters;
        }
    }
}
