package com.jaehl.gameTool.common.ui.screens.collectionList

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator

import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.componets.CustomVerticalScrollbar
import com.jaehl.gameTool.common.ui.screens.collectionDetails.CollectionDetailsScreen
import com.jaehl.gameTool.common.ui.screens.collectionEdit.CollectionEditScreen

class CollectionListScreen(
    val gameId : Int
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<CollectionListScreenModel>()

        LaunchedEffect(gameId){
            screenModel.setup(
                CollectionListScreenModel.Config(
                    gameId = gameId
                )
            )
        }

        CollectionListPage(
            onBackClick = {
                navigator.pop()
            },
            collections = screenModel.collections,
            onAddCollectionClick = {
                navigator.push(
                    CollectionEditScreen(
                        gameId = gameId,
                        collectionId = null
                    )
                )
            },
            onCollectionClick = { clickedCollectionId ->
                navigator.push(
                    CollectionDetailsScreen(
                        gameId = gameId,
                        collectionId = clickedCollectionId
                    )
                )
            },
            onCollectionDeleteClick = { collectionId ->
                screenModel.onCollectionDeleteClick(collectionId = collectionId)
            }
        )
    }
}

//@Preview
//@Composable
//fun preview(){
//    CollectionListPage(
//        onBackClick = {},
//        collections = listOf(
//            CollectionListScreenModel.CollectionViewModel(
//                id = 1,
//                name = "Collection 1"
//            ),
//            CollectionListScreenModel.CollectionViewModel(
//                id = 2,
//                name = "Collection2"
//            ),
//            CollectionListScreenModel.CollectionViewModel(
//                id = 3,
//                name = "Collection 3"
//            )
//        ),
//        onCollectionClick = {},
//        onCollectionDeleteClick = {}
//    )
//}

@Composable
fun CollectionListPage(
    onBackClick : () -> Unit,
    collections : List<CollectionListScreenModel.CollectionViewModel>,
    onAddCollectionClick : () -> Unit,
    onCollectionClick : (clickedCollectionId : Int) -> Unit,
    onCollectionDeleteClick: (clickedCollectionId : Int) -> Unit,
){
    val state : ScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        AppBar(
            title = "Collections",
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
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onAddCollectionClick()
                        },
                        modifier = Modifier
                            .padding(top = 20.dp)
                    ) {
                        Text("Create New")
                    }
                }

                collections.forEachIndexed { index, collectionViewModel ->
                    CollectionListRow(
                        index = index,
                        collection = collectionViewModel,
                        onCollectionClick = onCollectionClick,
                        onCollectionDeleteClick = onCollectionDeleteClick,

                    )
                }
            }
            CustomVerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                scrollState = state
            )
        }
    }
}

@Composable
fun CollectionListRow(
    index : Int,
    collection : CollectionListScreenModel.CollectionViewModel,
    onCollectionClick : (clickedCollectionId : Int) -> Unit,
    onCollectionDeleteClick: (clickedCollectionId : Int) -> Unit
){
    Row (
        modifier = Modifier
            .clickable {  onCollectionClick(collection.id) }
            .background(if(index.mod(2) == 0) AppColor.rowBackgroundEven else AppColor.rowBackgroundOdd)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            collection.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )

        IconButton(
            content = {
                Icon(Icons.Outlined.Delete, "delete", tint = Color.Black)
            },
            onClick = {
                onCollectionDeleteClick(collection.id)
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )

    }
}
