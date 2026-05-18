package com.shagalalab.qqkeyboard.keyboard.data

import com.shagalalab.qqkeyboard.keyboard.model.KeyData

object KeyboardMappings {

    // Latin keyboard layout (QWERTY-based with Karakalpak modifications)
    fun getLatinLayout(imeAction: Int? = null): List<List<KeyData>> {
        return listOf(
            listOf(
                KeyData.spacer(),
                KeyData.character("á"),
                KeyData.character("ǵ"),
                KeyData.character("ú"),
                KeyData.character("ń"),
                KeyData.character("ı"),
                KeyData.character("ó"),
                KeyData.spacer(),
            ),
            listOf(
                KeyData.character("q"),
                KeyData.character("w"),
                KeyData.character("e"),
                KeyData.character("r"),
                KeyData.character("t"),
                KeyData.character("y"),
                KeyData.character("u"),
                KeyData.character("i"),
                KeyData.character("o"),
                KeyData.character("p"),
            ),
            // Second row
            listOf(
                KeyData.character("a"),
                KeyData.character("s"),
                KeyData.character("d"),
                KeyData.character("f"),
                KeyData.character("g"),
                KeyData.character("h"),
                KeyData.character("j"),
                KeyData.character("k"),
                KeyData.character("l")
            ),
            // Third row with shift and backspace
            listOf(
                KeyData.shift(),
                KeyData.character("z"),
                KeyData.character("x"),
                KeyData.character("c"),
                KeyData.character("v"),
                KeyData.character("b"),
                KeyData.character("n"),
                KeyData.character("m"),
                KeyData.backspace()
            ),
            // Fourth row with special keys
            listOf(
                KeyData.modeSwitch("123"),
                KeyData.character(",", ","),
                KeyData.layoutSwitch("ҚҚ"),
                KeyData.space(),
                KeyData.emojiSwitch(),
                KeyData.character(".", "."),
                KeyData.enterDynamic(imeAction)
            )
        )
    }

    // Cyrillic keyboard layout
    fun getCyrillicLayout(imeAction: Int? = null): List<List<KeyData>> {
        return listOf(
            listOf(
                KeyData.character("ә"),
                KeyData.character("ў"),
                KeyData.character("ү"),
                KeyData.character("қ"),
                KeyData.character("ё"),
                KeyData.character("ң"),
                KeyData.character("ғ"),
                KeyData.character("ө"),
                KeyData.character("ъ"),
                KeyData.character("ҳ"),
            ),
            listOf(
                KeyData.character("й"),
                KeyData.character("ц"),
                KeyData.character("у"),
                KeyData.character("к"),
                KeyData.character("е"),
                KeyData.character("н"),
                KeyData.character("г"),
                KeyData.character("ш"),
                KeyData.character("щ"),
                KeyData.character("з"),
                KeyData.character("х"),
            ),
            listOf(
                KeyData.character("ф"),
                KeyData.character("ы"),
                KeyData.character("в"),
                KeyData.character("а"),
                KeyData.character("п"),
                KeyData.character("р"),
                KeyData.character("о"),
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
                KeyData.character("ь"),
                KeyData.character("б"),
                KeyData.character("ю"),
                KeyData.backspace(widthRatio = 1f),
            ),
            listOf(
                KeyData.modeSwitch("123"),
                KeyData.character(",", ","),
                KeyData.layoutSwitch("QQ"),
                KeyData.space(),
                KeyData.emojiSwitch(),
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
                KeyData.modeSwitch("€~\\"),
                KeyData.character("'"),
                KeyData.character("\""),
                KeyData.character(":"),
                KeyData.character(";"),
                KeyData.character("/"),
                KeyData.character("?"),
                KeyData.backspace()
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
                KeyData.modeSwitch("123"),
                KeyData.character("~"),
                KeyData.character("\\"),
                KeyData.character("¦"),
                KeyData.character("—"),
                KeyData.character("–"),
                KeyData.character("¯"),
                KeyData.backspace()
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
