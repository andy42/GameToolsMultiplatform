package com.jaehl.gameTool.common.ui.screens.gameDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
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
import com.jaehl.gameTool.common.ui.screens.collectionList.CollectionListScreen
import com.jaehl.gameTool.common.ui.screens.itemList.ItemListScreen

class GameDetailsScreen(
    val gameId : Int
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<GameDetailsScreenModel.Config, GameDetailsScreenModel>(tag = null, arg = GameDetailsScreenModel.Config(gameId = gameId))
        GameDetailsPage(
            title = screenModel.gameTitle.value,
            onBackClick = {
                navigator.pop()
            },
            onOpenItemsClick = {
                navigator.push(ItemListScreen(gameId = gameId))
            },
            onOpenCollectionsClick = {
                navigator.push(CollectionListScreen(gameId = gameId))
            },
            testImport = {
                screenModel.testImport()
            }
        )
    }
}

//@Preview
//@Composable
//fun preview(){
//    GameDetailsPage(
//        title = "game 1",
//        onBackClick = {},
//        onOpenItemsClick = {},
//        onOpenCollectionsClick = {},
//        testImport = {}
//    )
//}

@Composable
fun GameDetailsPage(
    title : String,
    onBackClick : () -> Unit,
    onOpenItemsClick : () -> Unit,
    onOpenCollectionsClick : () -> Unit,
    testImport : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        AppBar(
            title = title,
            backButtonEnabled = true,
            onBackClick = {
                onBackClick()
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Card(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .width(300.dp)
                    .height(400.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(MaterialTheme.colors.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            onOpenItemsClick()
                        }
                    ) {
                        Text("Items")
                    }

                    Button(
                        onClick = {
                            onOpenCollectionsClick()
                        }
                    ) {
                        Text("Collections")
                    }

//                    Button(
//                        onClick = {
//                            testImport()
//                        }
//                    ) {
//                        Text("import")
//                    }
                }

            }
        }
    }
}