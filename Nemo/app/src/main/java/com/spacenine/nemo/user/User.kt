package com.spacenine.nemo.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

private val auth: FirebaseAuth = FirebaseAuth.getInstance()

internal val currentFirebaseUser: FirebaseUser?
    get() = auth.currentUser

internal var currentUser: User? = null

data class User(var name: String? = null, var phone: String? = null)