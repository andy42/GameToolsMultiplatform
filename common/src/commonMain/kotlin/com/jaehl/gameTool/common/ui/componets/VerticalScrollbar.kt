package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CustomVerticalScrollbar(scrollState : ScrollState, modifier: Modifier)

@Composable
expect fun CustomVerticalScrollbar(scrollState : LazyListState, modifier: Modifier)
