package com.jaehl.gameTool.common.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import com.jaehl.gameTool.common.ui.screens.accountDetails.AccountDetailsScreen
import com.jaehl.gameTool.common.ui.screens.backupList.BackupListScreen
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
            showAdminTools = screenModel.showAdminTools.value,
            showEditGames = screenModel.showEditGames.value,
            onAccountClick = {
                navigator.push(AccountDetailsScreen())
            },
            onUsersClick = {
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
            onBackupClick = {
                navigator.push(
                    BackupListScreen()
                )
            }
        )
    }
}

@Composable
fun HomePage(
    games: List<GameModel>,
    showAdminTools : Boolean,
    showEditGames : Boolean,
    onAccountClick : () -> Unit,
    onUsersClick : () -> Unit,
    onCreateGameClick : () -> Unit,
    onGameClick : (gameId: Int) -> Unit,
    onGameEditClick : (gameId: Int) -> Unit,
    onBackupClick : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Gray)
    ) {
        AppBar(
            title = "Home",
            actions = {
                IconButton(content = {
                    Icon(Icons.Outlined.AccountBox, "Settings", tint = Color.White)
                }, onClick = {
                    onAccountClick()
                })
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()

        ) {
            if(showAdminTools) {
                AdminTools(
                    modifier = Modifier
                        .width(400.dp)
                        .padding(top = 20.dp)
                        .align(Alignment.CenterHorizontally),
                    onUsersClick = {
                        onUsersClick()
                    },
                    onBackupClick = onBackupClick
                )
            }
            GamesCard(
                modifier = Modifier
                    .width(400.dp)
                    .padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally),
                games = games,
                showEditGames = showEditGames,
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
    onBackupClick : () -> Unit
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
                        onBackupClick()
                    }
                ){
                    Text("Backups")
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
    showEditGames : Boolean,
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
                        .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                    color = MaterialTheme.colors.onSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Games"
                )
                if(showEditGames) {
                    IconButton(content = {
                        Icon(Icons.Outlined.Add, "Add Game", tint = MaterialTheme.colors.onSecondary)
                    }, onClick = {
                        onCreateGameClick()
                    })
                }
            }
            games.forEachIndexed{ index, gameModel ->
                GameRow(index, gameModel, showEditGames, onGameClick, onGameEditClick)
            }
        }
    }
}

@Composable
fun GameRow(
    index : Int,
    game : GameModel,
    showEditGames : Boolean,
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
        if(showEditGames) {
            IconButton(content = {
                Icon(Icons.Outlined.Edit, "Edit", tint = Color.Black)
            }, onClick = {
                onGameEditClick(game.id)
            })
        }
    }
}