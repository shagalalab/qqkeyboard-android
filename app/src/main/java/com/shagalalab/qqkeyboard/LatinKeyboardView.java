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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.shagalalab.qqkeyboard.util.SettingsUtil;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class LatinKeyboardView extends KeyboardView {

    enum Type {
        NORMAL,
        EMAIL,
        WEB
    }

    static final int KEYCODE_OPTIONS = -100;
    // TODO: Move this into android.inputmethodservice.Keyboard
    static final int KEYCODE_LANGUAGE_SWITCH = -101;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } else if (String.valueOf((char) key.codes[0]).equals(" ")) {
            InputMethodManager imeManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            imeManager.showInputMethodPicker();
            return true;
        } else {
            return super.onLongPress(key);
        }
    }

    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard)getKeyboard();
        //keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }

    void setKeyboardType(LatinKeyboard keyboard, Type type) {
        boolean isLettersKeyboard = keyboard.getXmlLayoutResId() == R.xml.kbd_qwerty;
        if (!isLettersKeyboard) {
            return;
        }

        List<Key> keys = keyboard.getKeys();
        int keyToChangeIndex = 39; // hardcoded index of comma key
        Key keyToChange = keys.get(keyToChangeIndex);
        if (keyToChange.label == null) {
            return;
        }

        if (type == Type.EMAIL) {
            changeKey(keyToChange, keyToChangeIndex, "@", 64);
        } else if (type == Type.WEB) {
            changeKey(keyToChange, keyToChangeIndex, "/", 47);
        } else if (type == Type.NORMAL) {
            changeKey(keyToChange, keyToChangeIndex, ",", 44);
        }
    }

    private void changeKey(Key keyToChange, int keyToChangeIndex, String label, int code) {
        keyToChange.label = label;
        keyToChange.codes[0] = code;
        invalidateKey(keyToChangeIndex);
    }

    public void toggleShiftKey(LatinKeyboard keyboard, boolean isShifted) {
        int shiftKeyIndex = 29; // hardcoded index for shift key
        Key key = keyboard.getKeys().get(shiftKeyIndex);
        Drawable shiftIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_keyboard_capslock_24dp);
        if (isShifted) {
            shiftIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.material_shift_key_pressed), PorterDuff.Mode.SRC_IN);
        } else {
            shiftIcon.setColorFilter(null);
        }
        key.icon = shiftIcon;
        invalidateKey(shiftKeyIndex);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean isLightTheme = SettingsUtil.isLightTheme(getContext());

        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(28);
        int keyColor = isLightTheme ?
            ContextCompat.getColor(getContext(), R.color.material_light_key_secondary_text_color) :
            ContextCompat.getColor(getContext(), R.color.material_dark_key_secondary_text_color);
        paint.setColor(keyColor);

        Paint paintNumber = new Paint();
        paintNumber.setTextAlign(Paint.Align.CENTER);
        paintNumber.setTextSize(34);
        paintNumber.setColor(keyColor);

        LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();
        boolean isNumberKeyboard = keyboard.getXmlLayoutResId() == R.xml.kbd_numbers;
        List<Key> keys = keyboard.getKeys();
        for (Key key: keys) {
            if (key.label != null) {
                // latin keyboard
                if (key.label.equals("a")) {
                    canvas.drawText("á", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("g")) {
                    canvas.drawText("ǵ", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("i")) {
                    canvas.drawText("ı", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("n")) {
                    canvas.drawText("ń", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("o")) {
                    canvas.drawText("ó", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("u")) {
                    canvas.drawText("ú", getKeyX(key), getKeyY(key), paint);

                    // cyrillic keyboard
                } else if (key.label.equals("у")) {
                    canvas.drawText("ўү", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("к")) {
                    canvas.drawText("қ", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("е")) {
                    canvas.drawText("ё", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("н")) {
                    canvas.drawText("ң", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("г")) {
                    canvas.drawText("ғ", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("х")) {
                    canvas.drawText("ҳ", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("а")) {
                    canvas.drawText("ә", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("о")) {
                    canvas.drawText("ө", getKeyX(key), getKeyY(key), paint);
                } else if (key.label.equals("ь")) {
                    canvas.drawText("ъ", getKeyX(key), getKeyY(key), paint);

                    // numbers keyboard
                } else if (key.label.equals("2") && isNumberKeyboard) {
                    canvas.drawText("ABC", getKeyNumberX(key), getKeyNumberY(key), paintNumber);
                } else if (key.label.equals("3") && isNumberKeyboard) {
                    canvas.drawText("DEF", getKeyNumberX(key), getKeyNumberY(key), paintNumber);
                } else if (key.label.equals("4") && isNumberKeyboard) {
                    canvas.drawText("GHI", getKeyNumberX(key), getKeyNumberY(key), paintNumber);
                } else if (key.label.equals("5") && isNumberKeyboard) {
                    canvas.drawText("JKL", getKeyNumberX(key), getKeyNumberY(key), paintNumber);
                } else if (key.label.equals("6") && isNumberKeyboard) {
                    canvas.drawText("MNO", getKeyNumberX(key), getKeyNumberY(key), paintNumber);
                } else if (key.label.equals("7") && isNumberKeyboard) {
                    canvas.drawText("PQRS", getKeyNumberX(key), getKeyNumberY(key), paintNumber);
                } else if (key.label.equals("8") && isNumberKeyboard) {
                    canvas.drawText("TUV", getKeyNumberX(key), getKeyNumberY(key), paintNumber);
                } else if (key.label.equals("9") && isNumberKeyboard) {
                    canvas.drawText("WXYZ", getKeyNumberX(key), getKeyNumberY(key), paintNumber);
                }
            }
        }
    }

    private float getKeyX(Key key) {
        return key.x + (key.width - 25);
    }

    private float getKeyY(Key key) {
        return key.y + 40;
    }

    private float getKeyNumberX(Key key) {
        return key.x + (key.width - 55);
    }

    private float getKeyNumberY(Key key) {
        return key.y + 100;
    }
}
