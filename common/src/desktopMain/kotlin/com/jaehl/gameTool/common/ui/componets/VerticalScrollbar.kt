package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun CustomVerticalScrollbar(scrollState : ScrollState, modifier: Modifier) {
    androidx.compose.foundation.VerticalScrollbar(
        modifier = modifier,
        adapter = rememberScrollbarAdapter(
            scrollState = scrollState
        )
    )
}
@Composable
actual fun CustomVerticalScrollbar(scrollState : LazyListState, modifier: Modifier) {
    androidx.compose.foundation.VerticalScrollbar(
        modifier = modifier,
        adapter = rememberScrollbarAdapter(
            scrollState = scrollState
        )
    )
}
