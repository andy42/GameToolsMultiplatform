package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.ui.TestTags

@Composable
fun CustomLinearProgressIndicator(loading : Boolean, testTag : String = TestTags.General.loading_indicator){
    if(loading){
        androidx.compose.material.LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .testTag(testTag),
            color = MaterialTheme.colors.secondary
        )
    } else {
        Box(Modifier.fillMaxWidth().height(4.dp))
    }
}