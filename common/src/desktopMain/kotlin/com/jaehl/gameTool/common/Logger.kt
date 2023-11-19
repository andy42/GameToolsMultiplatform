package com.jaehl.gameTool.common

actual class LoggerImp actual constructor() : Logger {
    override fun info(tag: String, message: String) {
        println("$tag : $message")
    }

    override fun error(tag: String, message: String, t: Throwable) {
        println("$tag : $message")
    }
}