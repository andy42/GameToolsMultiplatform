package com.jaehl.gameTool.common

import android.util.Log

actual class LoggerImp actual constructor() : Logger {
    override fun info(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun error(tag: String, message: String, t: Throwable) {
        Log.e(tag, message, t)
    }
}