package com.jaehl.gameTool.common

import kotlinx.coroutines.CoroutineDispatcher

class JobDispatcherTest(
    private val coroutineDispatcher : CoroutineDispatcher
) : JobDispatcher() {
    override fun io(): CoroutineDispatcher {
        return coroutineDispatcher
    }

    override fun computation(): CoroutineDispatcher {
        return coroutineDispatcher
    }

    override fun ui(): CoroutineDispatcher {
        return coroutineDispatcher
    }
}