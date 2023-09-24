package com.jaehl.gameTool.common.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.screens.gameDetails.GameDetailsScreen
import com.jaehl.gameTool.common.ui.screens.users.UsersScreen

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<HomeScreenModel>()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Gray)
        ) {
            AppBar(
                title = "Home"
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()

            ) {
                Card(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .width(300.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                navigator.push(UsersScreen())
                            }
                        ){
                            Text("Users")
                        }
                    }
                }

                GamesCard(
                    modifier = Modifier
                        .width(300.dp)
                        .padding(top = 20.dp)
                        .align(Alignment.CenterHorizontally),
                    games = screenModel.games,
                    onGameClick = { gameId ->
                        navigator.push(GameDetailsScreen(gameId))
                    },
                    onGameEditClick = { gameId ->

                    }
                )
            }
        }
    }
}

@Composable
fun GamesCard(
    modifier: Modifier,
    games: List<GameModel>,
    onGameClick : (gameId: Int) -> Unit,
    onGameEditClick : (gameId: Int) -> Unit

) {

    Card(
        modifier = modifier
            .width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.secondary)
                    .padding(10.dp),
                color = MaterialTheme.colors.onSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                text = "Games"
            )
            games.forEachIndexed{ index, gameModel ->
                GameRow(index, gameModel, onGameClick, onGameEditClick)
            }
        }
    }
}

@Composable
fun GameRow(
    index : Int,
    game : GameModel,
    onGameClick : (gameId: Int) -> Unit,
    onGameEditClick : (gameId: Int) -> Unit
) {
    Row (
        modifier = Modifier
            .clickable {
                onGameClick(game.id)
            }
            .background(if(index.mod(2) == 0) AppColor.rowBackgroundEven else AppColor.rowBackgroundOdd),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            game.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
        IconButton(content = {
            Icon(Icons.Outlined.Edit, "Edit", tint = Color.Black)
        }, onClick = {
            onGameEditClick(game.id)
        })
    }
}