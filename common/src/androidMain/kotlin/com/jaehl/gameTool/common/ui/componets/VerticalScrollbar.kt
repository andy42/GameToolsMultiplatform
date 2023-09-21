package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun CustomVerticalScrollbar(scrollState : ScrollState, modifier: Modifier) {
    Box(modifier = modifier)
}

@Composable
actual fun CustomVerticalScrollbar(scrollState : LazyListState, modifier: Modifier) {
    Box(modifier = modifier)
}
