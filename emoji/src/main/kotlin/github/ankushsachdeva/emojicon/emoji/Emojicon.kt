package github.ankushsachdeva.emojicon.emoji

import java.io.Serializable

data class Emojicon(val emoji: String) : Serializable {
    
    companion object {
        private const val serialVersionUID = 1L
        
        fun fromCodePoint(codePoint: Int): Emojicon {
            return Emojicon(newString(codePoint))
        }
        
        fun fromChar(ch: Char): Emojicon {
            return Emojicon(ch.toString())
        }
        
        fun fromChars(chars: String): Emojicon {
            return Emojicon(chars)
        }
        
        private fun newString(codePoint: Int): String {
            return if (Character.charCount(codePoint) == 1) {
                String(intArrayOf(codePoint), 0, 1)
            } else {
                String(Character.toChars(codePoint))
            }
        }
    }
}
