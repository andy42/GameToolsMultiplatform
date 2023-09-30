package com.jaehl.gameTool.common.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.componets.ItemIcon
import com.jaehl.gameTool.common.ui.screens.gameDetails.GameDetailsScreen
import com.jaehl.gameTool.common.ui.screens.gameEdit.GameEditScreen
import com.jaehl.gameTool.common.ui.screens.users.UsersScreen

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<HomeScreenModel>()

        LifecycleEffect(
            onStarted = {
                screenModel.setup()
            }
        )

        HomePage(
            games = screenModel.games,
            onUserClick = {
                navigator.push(UsersScreen())
            },
            onCreateGameClick = {
                navigator.push(GameEditScreen(
                    gameId = null
                ))
            },
            onGameClick = { gameId ->
                navigator.push(GameDetailsScreen(gameId))
            },
            onGameEditClick = { gameId ->
                navigator.push(GameEditScreen(
                    gameId = gameId
                ))
            },
            onCreateBackupClick = {
                TODO("create backup")
            }
        )
    }
}

@Composable
fun HomePage(
    games: List<GameModel>,
    onUserClick : () -> Unit,
    onCreateGameClick : () -> Unit,
    onGameClick : (gameId: Int) -> Unit,
    onGameEditClick : (gameId: Int) -> Unit,
    onCreateBackupClick : () -> Unit
) {
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
            AdminTools(
                modifier = Modifier
                    .width(400.dp)
                    .padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally),
                onUsersClick = {
                    onUserClick()
                },
                onCreateBackupClick = onCreateBackupClick
            )
            GamesCard(
                modifier = Modifier
                    .width(400.dp)
                    .padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally),
                games = games,
                onCreateGameClick = onCreateGameClick,
                onGameClick = { gameId ->
                    onGameClick(gameId)
                },
                onGameEditClick = { gameId ->
                    onGameEditClick(gameId)
                }
            )
        }
    }
}

@Composable
fun AdminTools(
    modifier: Modifier,
    onUsersClick : () -> Unit,
    onCreateBackupClick : () -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.secondary),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                    color = MaterialTheme.colors.onSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Admin Tools"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    onClick = {
                        onCreateBackupClick()
                    }
                ){
                    Text("create Backup")
                }
                Button(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    onClick = {
                        onUsersClick()
                    }
                ){
                    Text("Users")
                }
            }

        }
    }
}

@Composable
fun GamesCard(
    modifier: Modifier,
    games: List<GameModel>,
    onCreateGameClick : () -> Unit,
    onGameClick : (gameId: Int) -> Unit,
    onGameEditClick : (gameId: Int) -> Unit

) {

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.secondary),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    color = MaterialTheme.colors.onSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Games"
                )
                IconButton(content = {
                    Icon(Icons.Outlined.Add, "Add Game", tint = MaterialTheme.colors.onSecondary)
                }, onClick = {
                    onCreateGameClick()
                })
            }
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
        ItemIcon(
            modifier = Modifier
                .width(70.dp)
                .height(70.dp)
                .padding(5.dp),
            imageResource = game.icon
        )
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