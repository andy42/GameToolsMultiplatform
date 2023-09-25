package com.jaehl.gameTool.common.ui.screens.gameEdit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.componets.StyledOutlinedTextField

class GameEditScreen(
    val gameId : Int?
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<GameEditScreenModel>()

        LaunchedEffect(gameId){
            screenModel.setup(
                GameEditScreenModel.Config(
                    gameId = gameId
                )
            )
        }

        LaunchedEffect(screenModel.closePageEvent.value){
            if(screenModel.closePageEvent.value){
                navigator.pop()
            }
        }

        GameEditPage(
            title = "GameEdit",
            viewModel = screenModel.viewModel.value,
            onBackClick = {
                navigator.pop()
            },
            onSaveClick = screenModel::onSaveClick,
            onDelete = screenModel::onDelete,
            onNameChange = screenModel::onNameChange
        )
    }
}

@Composable
fun GameEditPage(
    title : String,
    viewModel: GameEditViewModel,
    onBackClick : () -> Unit,
    onSaveClick : () -> Unit,
    onDelete : () -> Unit,
    onNameChange : (value : String) -> Unit
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
            },
            actions = {
                Button(
                    onClick = {
                        onSaveClick()
                    },
                    modifier = Modifier
                ) {
                    Text("Save")
                }
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
//                    .height(400.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(MaterialTheme.colors.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StyledOutlinedTextField(
                        viewModel.name,
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 10.dp),
                        label = { Text("Name") },
                        enabled = !viewModel.pageLoading,
                        onValueChange = { value ->
                            onNameChange(value)
                        }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        if(viewModel.showDelete) {
                            Button(
                                modifier = Modifier
                                    .padding(bottom = 10.dp, end = 10.dp)
                                    .align(alignment = Alignment.CenterEnd),
                                onClick = {
                                    onDelete()
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                            ) {
                                Text("Delete")
                            }
                        }
                    }

                }
            }
        }
    }
}