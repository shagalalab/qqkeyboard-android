package com.shagalalab.qqkeyboard.keyboard.utils

fun String.kaaUppercase(): String {
    return if (this == "ı") "Í" else this.uppercase()
}
