package com.shagalalab.qqkeyboard.keyboard.data

import com.shagalalab.qqkeyboard.keyboard.model.KeyData

object EmojiData {

    // Basic emoji categories
    val smileysAndPeople = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
        "🙂", "🙃", "😉", "😌", "😍", "😘", "😗", "😙", "😚", "😋",
        "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩", "😏",
        "😒", "😞", "😔", "😟", "😕", "🙁", "😣", "😖", "😫", "😩",
        "🤯", "😢", "😭", "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵",
        "🥶", "😱", "😨", "😰", "😥", "😓", "🤗", "🤔", "🤭", "🤫"
    )

    val animalsAndNature = listOf(
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
        "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", "🐔",
        "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇", "🐺",
        "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜", "🦟"
    )

    val foodAndDrink = listOf(
        "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈",
        "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦",
        "🥒", "🥬", "🌶️", "🫑", "🌽", "🥕", "🫒", "🧄", "🧅", "🥔"
    )

    val activities = listOf(
        "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱",
        "🪀", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🪃", "🥅", "⛳"
    )

    val symbols = listOf(
        "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
        "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️",
        "✝️", "☪️", "🕉️", "☸️", "✡️", "🔯", "🕎", "☯️", "☦️", "🛐"
    )

    fun getEmojiLayout(): List<List<KeyData>> {
        // Create a grid layout with emojis
        val emojiRows = mutableListOf<List<KeyData>>()

        // Split emojis into rows of 8
        val allEmojis = smileysAndPeople.take(32) // Show first 32 emojis for now
        val emojiChunks = allEmojis.chunked(8)

        emojiChunks.forEach { row ->
            emojiRows.add(
                row.map { emoji ->
                    KeyData.character(emoji, emoji)
                }
            )
        }

        // Add control row
        emojiRows.add(
            listOf(
                KeyData.modeSwitch("АБВ"),
                KeyData.space(),
                KeyData.backspace(),
                KeyData.enter()
            )
        )

        return emojiRows
    }

    // Category-based emoji layouts
    fun getEmojiLayoutByCategory(category: EmojiCategory): List<List<KeyData>> {
        val emojis = when (category) {
            EmojiCategory.SMILEYS -> smileysAndPeople
            EmojiCategory.ANIMALS -> animalsAndNature
            EmojiCategory.FOOD -> foodAndDrink
            EmojiCategory.ACTIVITIES -> activities
            EmojiCategory.SYMBOLS -> symbols
        }

        val emojiRows = mutableListOf<List<KeyData>>()
        val emojiChunks = emojis.chunked(8)

        emojiChunks.forEach { row ->
            emojiRows.add(
                row.map { emoji ->
                    KeyData.character(emoji, emoji)
                }
            )
        }

        // Add control row
        emojiRows.add(
            listOf(
                KeyData.modeSwitch("АБВ"),
                KeyData.space(),
                KeyData.backspace(),
                KeyData.enter()
            )
        )

        return emojiRows
    }
}

enum class EmojiCategory {
    SMILEYS, ANIMALS, FOOD, ACTIVITIES, SYMBOLS
}
