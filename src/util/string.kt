package com.example.util

private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

class StringUtil {
    fun createRandomString(n: Int) {
        (1..n).map { i -> kotlin.random.Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }
}