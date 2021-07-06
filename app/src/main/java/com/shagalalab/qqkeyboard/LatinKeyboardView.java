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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.shagalalab.qqkeyboard.util.SettingsUtil;

import java.util.List;

import androidx.core.content.ContextCompat;

import static android.content.Context.INPUT_METHOD_SERVICE;

@SuppressWarnings("deprecation")
public class LatinKeyboardView extends KeyboardView {

    enum Type {
        NORMAL,
        EMAIL,
        WEB
    }

    static final int KEYCODE_OPTIONS = -100;
    static final int KEYCODE_LANGUAGE_SWITCH = -101;
    static final int KEYCODE_EMOJI_SWITCH = -102;

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
            if (imeManager != null) {
                imeManager.showInputMethodPicker();
            }
            return true;
        } else {
            return super.onLongPress(key);
        }
    }

    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();
        //keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }

    void setKeyboardType(LatinKeyboard keyboard, Type type) {
        boolean isLettersKeyboard = (keyboard.getXmlLayoutResId() == R.xml.kbd_qwerty_first_row_numbers ||
            keyboard.getXmlLayoutResId() == R.xml.kbd_qwerty_first_row_letters);
        if (!isLettersKeyboard) {
            return;
        }

        List<Key> keys = keyboard.getKeys();
        int keyToChangeIndex; // hardcoded index of comma key
        if (keyboard.getXmlLayoutResId() == R.xml.kbd_qwerty_first_row_numbers) {
            keyToChangeIndex = 39;
        } else if (keyboard.getXmlLayoutResId() == R.xml.kbd_qwerty_first_row_letters) {
            keyToChangeIndex = 35;
        } else {
            return;
        }
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
        int keyboardLayoutId = keyboard.getXmlLayoutResId();
        if (keyboardLayoutId == R.xml.kbd_numbers || keyboardLayoutId == R.xml.kbd_numbers_ext) {
            return;
        }

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

        Resources res = getContext().getResources();

        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(res.getInteger(R.integer.key_additional_size));
        int keyColor = isLightTheme ?
            ContextCompat.getColor(getContext(), R.color.material_light_key_secondary_text_color) :
            ContextCompat.getColor(getContext(), R.color.material_dark_key_secondary_text_color);
        paint.setColor(keyColor);

        Paint paintNumber = new Paint();
        paintNumber.setTextAlign(Paint.Align.CENTER);
        paintNumber.setTextSize(res.getInteger(R.integer.key_additional_number_size));
        paintNumber.setColor(keyColor);

        LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();
        boolean isNumberKeyboard = keyboard.getXmlLayoutResId() == R.xml.kbd_numbers;
        boolean isKeyboardWithNumbers = SettingsUtil.isKeyboardWithFirstRowNumbers(getContext());

        List<Key> keys = keyboard.getKeys();
        for (Key key : keys) {
            if (key.label != null) {
                // latin keyboard
                if (key.label.equals("a") && isKeyboardWithNumbers) {
                    canvas.drawText("á", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("g") && isKeyboardWithNumbers) {
                    canvas.drawText("ǵ", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("i") && isKeyboardWithNumbers) {
                    canvas.drawText("ı", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("n") && isKeyboardWithNumbers) {
                    canvas.drawText("ń", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("o") && isKeyboardWithNumbers) {
                    canvas.drawText("ó", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("u") && isKeyboardWithNumbers) {
                    canvas.drawText("ú", getKeyX(res, key), getKeyY(res, key), paint);

                // cyrillic keyboard
                } else if (key.label.equals("у") && isKeyboardWithNumbers) {
                    canvas.drawText("ўү", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("к") && isKeyboardWithNumbers) {
                    canvas.drawText("қ", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("е") && isKeyboardWithNumbers) {
                    canvas.drawText("ё", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("н") && isKeyboardWithNumbers) {
                    canvas.drawText("ң", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("г") && isKeyboardWithNumbers) {
                    canvas.drawText("ғ", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("х") && isKeyboardWithNumbers) {
                    canvas.drawText("ҳ", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("а") && isKeyboardWithNumbers) {
                    canvas.drawText("ә", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("о") && isKeyboardWithNumbers) {
                    canvas.drawText("ө", getKeyX(res, key), getKeyY(res, key), paint);
                } else if (key.label.equals("ь") && isKeyboardWithNumbers) {
                    canvas.drawText("ъ", getKeyX(res, key), getKeyY(res, key), paint);

                // numbers keyboard
                } else if (key.label.equals("2") && isNumberKeyboard) {
                    canvas.drawText("ABC", getKeyNumberX(res, key), getKeyNumberY(res, key), paintNumber);
                } else if (key.label.equals("3") && isNumberKeyboard) {
                    canvas.drawText("DEF", getKeyNumberX(res, key), getKeyNumberY(res, key), paintNumber);
                } else if (key.label.equals("4") && isNumberKeyboard) {
                    canvas.drawText("GHI", getKeyNumberX(res, key), getKeyNumberY(res, key), paintNumber);
                } else if (key.label.equals("5") && isNumberKeyboard) {
                    canvas.drawText("JKL", getKeyNumberX(res, key), getKeyNumberY(res, key), paintNumber);
                } else if (key.label.equals("6") && isNumberKeyboard) {
                    canvas.drawText("MNO", getKeyNumberX(res, key), getKeyNumberY(res, key), paintNumber);
                } else if (key.label.equals("7") && isNumberKeyboard) {
                    canvas.drawText("PQRS", getKeyNumberX(res, key), getKeyNumberY(res, key), paintNumber);
                } else if (key.label.equals("8") && isNumberKeyboard) {
                    canvas.drawText("TUV", getKeyNumberX(res, key), getKeyNumberY(res, key), paintNumber);
                } else if (key.label.equals("9") && isNumberKeyboard) {
                    canvas.drawText("WXYZ", getKeyNumberX(res, key), getKeyNumberY(res, key), paintNumber);
                }
            }
        }
    }

    private float getKeyX(Resources res, Key key) {
        return key.x + (key.width - res.getInteger(R.integer.key_additional_x_margin));
    }

    private float getKeyY(Resources res, Key key) {
        return key.y + res.getInteger(R.integer.key_additional_y_margin);
    }

    private float getKeyNumberX(Resources res, Key key) {
        return key.x + (key.width - res.getInteger(R.integer.key_additional_number_x_margin));
    }

    private float getKeyNumberY(Resources res, Key key) {
        return key.y + res.getInteger(R.integer.key_additional_number_y_margin);
    }
}
