package com.bytedance.android.douyin.saas.utils

import android.os.Handler
import android.os.Looper

object ThreadUtils {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun runOnUIThread(run: () -> Unit) {
        runOnUIThread(Runnable { run() })
    }

    fun runOnUIThread(run: Runnable) {
        if (isUIThread()) {
            run.run()
        } else {
            mainHandler.post(run)
        }
    }

    fun postOnUIThread(run: ()->Unit) {
        postOnUIThread(Runnable { run() })
    }

    fun postOnUIThread(run: Runnable) {
        mainHandler.post(run)
    }

    fun isUIThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }
}