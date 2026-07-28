package com.shagalalab.qqkeyboard.keyboard.data

import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.model.KeyData
import com.shagalalab.qqkeyboard.keyboard.model.TopRowMode

object KeyboardMappings {

    // The emoji key only exists when the suggestion strip is hidden — otherwise the strip's own
    // emoji button is the entry point. It sits to the right of the space bar and borrows its
    // width from it, so the bottom row keeps the same total and the other keys don't shift.
    // Only the letter layouts get it; from the numeric and symbolic layouts the way to emoji is
    // ABC first.
    private fun spaceAndEmoji(showEmojiKey: Boolean): List<KeyData> = if (showEmojiKey) {
        listOf(KeyData.space(widthRatio = 3f), KeyData.emoji())
    } else {
        listOf(KeyData.space())
    }

    // Latin keyboard layout (QWERTY-based with Karakalpak modifications)
    fun getLatinLayout(
        topRowMode: TopRowMode = TopRowMode.EXTRA_LETTERS,
        imeAction: Int? = null,
        showEmojiKey: Boolean = false,
    ): List<List<KeyData>> {
        val topRow = if (topRowMode == TopRowMode.NUMBERS) {
            listOf(
                KeyData.character("1"), KeyData.character("2"), KeyData.character("3"),
                KeyData.character("4"), KeyData.character("5"), KeyData.character("6"),
                KeyData.character("7"), KeyData.character("8"), KeyData.character("9"),
                KeyData.character("0"),
            )
        } else {
            listOf(
                KeyData.character("á"), KeyData.character("ǵ"), KeyData.character("ú"),
                KeyData.character("ń"), KeyData.character("ı"), KeyData.character("ó"),
            )
        }
        return listOf(
            topRow,
            listOf(
                KeyData.character("q"),
                KeyData.character("w"),
                KeyData.character("e"),
                KeyData.character("r"),
                KeyData.character("t"),
                KeyData.character("y"),
                latinSecondary("u", "ú", topRowMode),
                latinSecondary("i", "ı", topRowMode),
                latinSecondary("o", "ó", topRowMode),
                KeyData.character("p"),
            ),
            // Second row
            listOf(
                latinSecondary("a", "á", topRowMode),
                KeyData.character("s"),
                KeyData.character("d"),
                KeyData.character("f"),
                latinSecondary("g", "ǵ", topRowMode),
                KeyData.character("h"),
                KeyData.character("j"),
                KeyData.character("k"),
                KeyData.character("l"),
            ),
            // Third row with shift and backspace
            listOf(
                KeyData.shift(),
                KeyData.character("z"),
                KeyData.character("x"),
                KeyData.character("c"),
                KeyData.character("v"),
                KeyData.character("b"),
                latinSecondary("n", "ń", topRowMode),
                KeyData.character("m"),
                KeyData.backspace(fillRight = true)
            ),
            // Fourth row with special keys
            listOf(
                KeyData.modeSwitch("123"),
                KeyData.character(",", ","),
                KeyData.layoutSwitch(R.drawable.layout_switch_to_cyr),
            ) + spaceAndEmoji(showEmojiKey) + listOf(
                KeyData.character(".", "."),
                KeyData.enterDynamic(imeAction)
            )
        )
    }

    private fun latinSecondary(base: String, extra: String, mode: TopRowMode): KeyData =
        latinSecondary(base, listOf(extra), mode)

    private fun latinSecondary(base: String, extras: List<String>, mode: TopRowMode): KeyData {
        return if (mode == TopRowMode.NUMBERS) {
            KeyData.character(base).copy(alternativeChars = extras)
        } else {
            KeyData.character(base)
        }
    }

    private fun cyrillicSecondary(base: String, extra: String, mode: TopRowMode): KeyData =
        cyrillicSecondary(base, listOf(extra), mode)

    private fun cyrillicSecondary(base: String, extras: List<String>, mode: TopRowMode): KeyData {
        return if (mode == TopRowMode.NUMBERS) {
            KeyData.character(base).copy(alternativeChars = extras)
        } else {
            KeyData.character(base)
        }
    }

    // Cyrillic keyboard layout
    fun getCyrillicLayout(
        topRowMode: TopRowMode = TopRowMode.EXTRA_LETTERS,
        imeAction: Int? = null,
        showEmojiKey: Boolean = false,
    ): List<List<KeyData>> {
        val topRow = if (topRowMode == TopRowMode.NUMBERS) {
            listOf(
                KeyData.character("1"), KeyData.character("2"), KeyData.character("3"),
                KeyData.character("4"), KeyData.character("5"), KeyData.character("6"),
                KeyData.character("7"), KeyData.character("8"), KeyData.character("9"),
                KeyData.character("0"),
            )
        } else {
            listOf(
                KeyData.character("ә"), KeyData.character("ў"), KeyData.character("ү"),
                KeyData.character("қ"), KeyData.character("ё"), KeyData.character("ң"),
                KeyData.character("ғ"), KeyData.character("ө"), KeyData.character("ъ"),
                KeyData.character("ҳ"),
            )
        }
        return listOf(
            topRow,
            listOf(
                KeyData.character("й"),
                KeyData.character("ц"),
                // ў and ү are both у-variants, so they share the у key rather than ў being
                // parked on й for want of a phonetic base of its own.
                cyrillicSecondary("у", listOf("ў", "ү"), topRowMode),
                cyrillicSecondary("к", "қ", topRowMode),
                cyrillicSecondary("е", "ё", topRowMode),
                cyrillicSecondary("н", "ң", topRowMode),
                cyrillicSecondary("г", "ғ", topRowMode),
                KeyData.character("ш"),
                KeyData.character("щ"),
                KeyData.character("з"),
                cyrillicSecondary("х", "ҳ", topRowMode),
            ),
            listOf(
                KeyData.character("ф"),
                KeyData.character("ы"),
                KeyData.character("в"),
                cyrillicSecondary("а", "ә", topRowMode),
                KeyData.character("п"),
                KeyData.character("р"),
                cyrillicSecondary("о", "ө", topRowMode),
                KeyData.character("л"),
                KeyData.character("д"),
                KeyData.character("ж"),
                KeyData.character("э"),
            ),
            listOf(
                KeyData.shift(widthRatio = 1f),
                KeyData.character("я"),
                KeyData.character("ч"),
                KeyData.character("с"),
                KeyData.character("м"),
                KeyData.character("и"),
                KeyData.character("т"),
                cyrillicSecondary("ь", "ъ", topRowMode),
                KeyData.character("б"),
                KeyData.character("ю"),
                KeyData.backspace(fillRight = true),
            ),
            listOf(
                KeyData.modeSwitch("123"),
                KeyData.character(",", ","),
                KeyData.layoutSwitch(R.drawable.layout_switch_to_lat),
            ) + spaceAndEmoji(showEmojiKey) + listOf(
                KeyData.character(".", "."),
                KeyData.enterDynamic(imeAction),
            )
        )
    }

    // Numbers and symbols layout
    fun getNumericLayout(imeAction: Int? = null): List<List<KeyData>> {
        return listOf(
            // Numbers row
            listOf(
                KeyData.character("1"),
                KeyData.character("2"),
                KeyData.character("3"),
                KeyData.character("4"),
                KeyData.character("5"),
                KeyData.character("6"),
                KeyData.character("7"),
                KeyData.character("8"),
                KeyData.character("9"),
                KeyData.character("0")
            ),
            // Symbols row
            listOf(
                KeyData.character("!"),
                KeyData.character("@"),
                KeyData.character("#"),
                KeyData.character("$"),
                KeyData.character("%"),
                KeyData.character("^"),
                KeyData.character("&"),
                KeyData.character("*"),
                KeyData.character("("),
                KeyData.character(")")
            ),
            // More symbols
            listOf(
                KeyData.character("-"),
                KeyData.character("_"),
                KeyData.character("="),
                KeyData.character("+"),
                KeyData.character("["),
                KeyData.character("]"),
                KeyData.character("{"),
                KeyData.character("}"),
                KeyData.character("<"),
                KeyData.character(">")
            ),
            // Punctuation and special
            listOf(
                KeyData.modeSwitch("€~\\", fillLeft = true),
                KeyData.character("'"),
                KeyData.character("\""),
                KeyData.character(":"),
                KeyData.character(";"),
                KeyData.character("/"),
                KeyData.character("?"),
                KeyData.backspace(fillRight = true)
            ),
            // Control row
            listOf(
                KeyData.modeSwitch("ABC"),
                KeyData.character(",", ","),
                KeyData.space(),
                KeyData.character(".", "."),
                KeyData.enterDynamic(imeAction)
            )
        )
    }

    fun getNumberPadLayout(imeAction: Int? = null): List<List<KeyData>> {
        return listOf(
            listOf(
                KeyData.character("1"),
                KeyData.character("2"),
                KeyData.character("3"),
                KeyData.character("-").copy(fillSpace = true)
            ),
            listOf(
                KeyData.character("4"),
                KeyData.character("5"),
                KeyData.character("6"),
                KeyData.numpadSpace().copy(fillSpace = true)
            ),
            listOf(
                KeyData.character("7"),
                KeyData.character("8"),
                KeyData.character("9"),
                KeyData.backspace(fillRight = true)
            ),
            listOf(
                KeyData.character(","),
                KeyData.character("0"),
                KeyData.character("."),
                KeyData.enterDynamic(imeAction).copy(fillSpace = true)
            )
        )
    }

    fun getNumberPasswordLayout(imeAction: Int? = null): List<List<KeyData>> {
        return listOf(
            listOf(
                KeyData.character("1"),
                KeyData.character("2"),
                KeyData.character("3")
            ),
            listOf(
                KeyData.character("4"),
                KeyData.character("5"),
                KeyData.character("6")
            ),
            listOf(
                KeyData.character("7"),
                KeyData.character("8"),
                KeyData.character("9")
            ),
            listOf(
                KeyData.backspace(widthRatio = 1f),
                KeyData.character("0"),
                KeyData.enterDynamic(imeAction, widthRatio = 1f)
            )
        )
    }

    fun getPhoneLayout(imeAction: Int? = null): List<List<KeyData>> {
        return listOf(
            listOf(
                KeyData.character("1"),
                KeyData.phoneDigit("2", "ABC"),
                KeyData.phoneDigit("3", "DEF"),
                KeyData.character("-").copy(fillSpace = true)
            ),
            listOf(
                KeyData.phoneDigit("4", "GHI"),
                KeyData.phoneDigit("5", "JKL"),
                KeyData.phoneDigit("6", "MNO"),
                KeyData.numpadSpace().copy(fillSpace = true)
            ),
            listOf(
                KeyData.phoneDigit("7", "PQRS"),
                KeyData.phoneDigit("8", "TUV"),
                KeyData.phoneDigit("9", "WXYZ"),
                KeyData.backspace(fillRight = true)
            ),
            listOf(
                KeyData.character("*"),
                KeyData.character("0"),
                KeyData.character("."),
                KeyData.enterDynamic(imeAction).copy(fillSpace = true)
            )
        )
    }

    fun getSymbolicLayout(imeAction: Int? = null): List<List<KeyData>> {
        return listOf(
            listOf(
                KeyData.character("£"),
                KeyData.character("¢"),
                KeyData.character("€"),
                KeyData.character("¥"),
                KeyData.character("₽"),
                KeyData.character("©"),
                KeyData.character("®"),
                KeyData.character("™"),
                KeyData.character("✓"),
            ),
            listOf(
                KeyData.character("`"),
                KeyData.character("|"),
                KeyData.character("•"),
                KeyData.character("√"),
                KeyData.character("π"),
                KeyData.character("÷"),
                KeyData.character("×"),
                KeyData.character("§"),
                KeyData.character("∆"),
            ),
            listOf(
                KeyData.character("’"),
                KeyData.character("°"),
                KeyData.character("№"),
                KeyData.character("„"),
                KeyData.character("“"),
                KeyData.character("”"),
                KeyData.character("«"),
                KeyData.character("»"),
                KeyData.character("±"),
            ),
            listOf(
                KeyData.modeSwitch("123", fillLeft = true),
                KeyData.character("~"),
                KeyData.character("\\"),
                KeyData.character("¦"),
                KeyData.character("—"),
                KeyData.character("–"),
                KeyData.character("¯"),
                KeyData.backspace(fillRight = true)
            ),
            // Control row
            listOf(
                KeyData.modeSwitch("ABC"),
                KeyData.space(),
                KeyData.enterDynamic(imeAction)
            )
        )
    }
}
