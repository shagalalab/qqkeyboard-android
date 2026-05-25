package com.shagalalab.qqkeyboard.keyboard.utils

fun String.kaaUppercase(): String = this.map { it.kaaUppercaseChar() }.joinToString("")

fun Char.kaaUppercaseChar(): Char = if (this == 'ı') 'Í' else this.uppercaseChar()

fun String.kaaLowercase(): String = this.map { it.kaaLowercaseChar() }.joinToString("")

fun Char.kaaLowercaseChar(): Char = if (this == 'Í') 'ı' else this.lowercaseChar()
