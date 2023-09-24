package com.jaehl.gameTool.common.ui.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ScreenModel.launchIo(
    jobDispatcher: JobDispatcher,
    onException: (t: Throwable) -> Unit,
    block: suspend () -> Unit
) = coroutineScope.launch {

    withContext(jobDispatcher.io()){
        try {
            block()
        }
        catch (t: Throwable) {
            onException(t)
        }
    }
}

fun ScreenModel.runWithCatch(
    onException: (t: Throwable) -> Unit,
    block: () -> Unit
) {
    try {
        block()
    }
    catch (t: Throwable) {
        onException(t)
    }
}

fun ScreenModel.launchWithCatch(
    onException: (t: Throwable) -> Unit,
    block: suspend () -> Unit
) = coroutineScope.launch {
    try {
        block()
    }
    catch (t: Throwable) {
        onException(t)
    }
}