package com.jaehl.gameTool.common

interface Logger {
    fun info(tag : String, message : String)
    fun error(tag : String, message : String, t : Throwable)
}

expect class LoggerImp() : Logger