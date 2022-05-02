package com.spacenine.nemo.util

import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences


const val userSharedPreferencesFileName = "User"

const val userPhoneKey = "UserPhoneNumber"

fun Context.updatePhoneNumberInSharedPreferences(phone: String) {

    val sharedPreferences = getSharedPreferences(userSharedPreferencesFileName, MODE_PRIVATE)
    val sharedPreferencesEditor = sharedPreferences.edit()

    sharedPreferencesEditor.putString(userPhoneKey, phone)
    sharedPreferencesEditor.apply()

}

fun Context.getPhoneNumberInSharedPreferences(): String? {

    val sharedPreferences = getSharedPreferences(userSharedPreferencesFileName, MODE_PRIVATE)

    return sharedPreferences.getString(userPhoneKey, null)

}
