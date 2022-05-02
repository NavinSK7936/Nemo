package com.spacenine.nemo

import android.os.Handler
import android.os.Looper



fun runOnUIThread(action: Runnable) =
    Handler(Looper.getMainLooper()).post(action)
