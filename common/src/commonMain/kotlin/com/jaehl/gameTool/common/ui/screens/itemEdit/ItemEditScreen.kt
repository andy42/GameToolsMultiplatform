package com.jaehl.gameTool.common.ui.screens.itemEdit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.componets.ItemIcon
import com.jaehl.gameTool.common.ui.screens.itemDetails.ItemDetailsScreenModel

class ItemEditScreen(
    private val gameId : Int,
    private val itemId : Int?
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<ItemEditScreenModel.Config, ItemEditScreenModel>(
            arg = ItemEditScreenModel.Config(gameId = gameId, itemId = itemId)
        )
        ItemEditPage(
            onBackClick = {
                navigator.pop()
            }
        )

    }
}

@Composable
fun ItemEditPage(
    onBackClick : ()-> Unit,
) {
    val state : ScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        AppBar(
            title = "ItemDetails",
            backButtonEnabled = true,
            onBackClick = {
                onBackClick()
            }
        )
        Box(
            Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state)
                    .padding(20.dp)
            ) {

            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
    }
}