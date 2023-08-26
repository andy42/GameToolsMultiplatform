package com.jaehl.gameTool.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class JobDispatcher {
    open fun io() : CoroutineDispatcher = Dispatchers.IO
    open fun computation() : CoroutineDispatcher = Dispatchers.Default
    open fun ui() : CoroutineDispatcher = Dispatchers.Main
}