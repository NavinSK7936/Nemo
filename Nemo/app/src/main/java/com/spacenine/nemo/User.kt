package com.spacenine.nemo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

private val auth: FirebaseAuth = FirebaseAuth.getInstance()

internal val user: FirebaseUser?
    get() = auth.currentUser
