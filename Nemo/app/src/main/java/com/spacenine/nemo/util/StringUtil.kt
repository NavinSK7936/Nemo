package com.spacenine.nemo.util

import com.google.gson.Gson

val gson = Gson()

fun toJson(any: Any): String =
    gson.toJson(any)

inline fun<reified T> String.fromJson(): T =
    gson.fromJson(this, T::class.java)
