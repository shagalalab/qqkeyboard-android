package com.shagalalab.qqkeyboard.keyboard.utils

import java.text.BreakIterator

object EmojiUtils {

    /**
     * Calculates the number of characters to delete to remove one complete grapheme cluster
     * (which includes emojis, combining characters, etc.) from the end of the text.
     *
     * @param textBeforeCursor The text before the cursor position
     * @return The number of Unicode code units to delete, or 0 if text is empty
     */
    fun getGraphemeClusterLength(textBeforeCursor: String?): Int {
        if (textBeforeCursor.isNullOrEmpty()) {
            return 0
        }

        // Use BreakIterator to find grapheme cluster boundaries
        val breakIterator = BreakIterator.getCharacterInstance()
        breakIterator.setText(textBeforeCursor)

        // Find the last grapheme cluster boundary
        val lastBoundary = breakIterator.last()
        val previousBoundary = breakIterator.previous()

        // If there's no previous boundary, the entire text is one grapheme cluster
        if (previousBoundary == BreakIterator.DONE) {
            return textBeforeCursor.length
        }

        // Return the length of the last grapheme cluster
        return lastBoundary - previousBoundary
    }

    /**
     * Alternative method using Unicode properties for emoji detection
     * This is more lightweight but might not catch all complex emoji sequences
     */
    fun getEmojiOrCharacterLength(textBeforeCursor: String?): Int {
        if (textBeforeCursor.isNullOrEmpty()) {
            return 0
        }

        val length = textBeforeCursor.length
        if (length == 0) return 0

        // Check if the last character is a low surrogate (part of emoji)
        val lastChar = textBeforeCursor[length - 1]

        // If it's a low surrogate, we need to delete both high and low surrogates
        if (Character.isLowSurrogate(lastChar) && length >= 2) {
            val prevChar = textBeforeCursor[length - 2]
            if (Character.isHighSurrogate(prevChar)) {
                // Check if this might be part of a more complex emoji sequence
                return getComplexEmojiLength(textBeforeCursor, length - 2)
            }
        }

        // Check for variation selectors and combining characters
        if (isVariationSelector(lastChar) || isCombiningCharacter(lastChar)) {
            return findCompleteSequenceLength(textBeforeCursor)
        }

        // For flag emojis and other multi-part sequences
        if (isRegionalIndicator(lastChar) && length >= 2) {
            val prevChar = textBeforeCursor[length - 2]
            if (isRegionalIndicator(prevChar)) {
                return 2 // Flag emoji (2 regional indicators)
            }
        }

        // Default case: single character
        return if (Character.isLowSurrogate(lastChar) && length >= 2 &&
                  Character.isHighSurrogate(textBeforeCursor[length - 2])) {
            2 // Surrogate pair
        } else {
            1 // Single character
        }
    }

    private fun getComplexEmojiLength(text: String, startIndex: Int): Int {
        val index = startIndex
        var length = 2 // Start with the surrogate pair

        // Look ahead for zero-width joiners, variation selectors, etc.
        while (index + length < text.length) {
            val nextChar = text[index + length]

            when {
                nextChar == '\u200D' -> { // Zero-width joiner
                    length++
                    // ZWJ is usually followed by another emoji
                    if (index + length + 1 < text.length) {
                        val afterZWJ = text[index + length]
                        length += if (Character.isHighSurrogate(afterZWJ)) {
                            2 // Add the emoji after ZWJ
                        } else {
                            1
                        }
                    }
                }
                isVariationSelector(nextChar) -> length++
                isSkinToneModifier(nextChar) -> {
                    if (Character.isHighSurrogate(nextChar) && index + length + 1 < text.length) {
                        length += 2 // Skin tone modifier is a surrogate pair
                    } else {
                        length++
                    }
                }
                else -> break
            }
        }

        return length
    }

    private fun findCompleteSequenceLength(text: String): Int {
        var length = 1
        var index = text.length - 2

        while (index >= 0) {
            val char = text[index]
            when {
                Character.isHighSurrogate(char) -> {
                    length++
                    index--
                }
                char == '\u200D' -> { // Zero-width joiner
                    length++
                    index--
                    // Skip the character/emoji before ZWJ
                    if (index >= 0 && Character.isLowSurrogate(text[index])) {
                        length++
                        index--
                        if (index >= 0 && Character.isHighSurrogate(text[index])) {
                            length++
                            index--
                        }
                    }
                }
                isVariationSelector(char) || isCombiningCharacter(char) -> {
                    length++
                    index--
                }
                else -> break
            }
        }

        return length
    }

    private fun isVariationSelector(char: Char): Boolean {
        val codePoint = char.code
        return (codePoint >= 0xFE00 && codePoint <= 0xFE0F) ||
               (codePoint >= 0xE0100 && codePoint <= 0xE01EF)
    }

    private fun isCombiningCharacter(char: Char): Boolean {
        val type = Character.getType(char).toByte()
        return type == Character.NON_SPACING_MARK ||
                type == Character.COMBINING_SPACING_MARK ||
                type == Character.ENCLOSING_MARK
    }

    private fun isRegionalIndicator(char: Char): Boolean {
        val codePoint = char.code
        return codePoint >= 0x1F1E6 && codePoint <= 0x1F1FF
    }

    private fun isSkinToneModifier(char: Char): Boolean {
        val codePoint = char.code
        return codePoint >= 0x1F3FB && codePoint <= 0x1F3FF
    }
}
