package com.kewlspy.url_shortner.util

object Base62 {
    private const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val BASE = ALPHABET.length

    fun encode(num: Long): String {
        if (num == 0L) return ALPHABET[0].toString()
        var n = num
        val sb = StringBuilder()
        while (n > 0) {
            val rem = (n % BASE).toInt()
            sb.append(ALPHABET[rem])
            n /= BASE
        }
        return sb.reverse().toString()
    }

    fun decode(str: String): Long {
        var num = 0L
        for (ch in str) {
            num = num * BASE + ALPHABET.indexOf(ch)
        }
        return num
    }
}
