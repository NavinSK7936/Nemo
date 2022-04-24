package com.spacenine.nemo

import android.os.Handler
import android.os.Looper

internal const val MAX_NET_G_VALUE = 8.0f
internal const val MAX_SHAKE = 3
internal const val ON_SHAKE_MESSAGE =
    "I am in DANGER, I need help. This message was sent from NEMO App recognizing my situation."
internal const val ON_HIT_MESSAGE =
    "I guess my Phone got hit due to some reason. This message was sent from NEMO App recognizing my situation."


fun runOnUIThread(action: Runnable) =
    Handler(Looper.getMainLooper()).post(action)
