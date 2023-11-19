package com.jaehl.gameTool.common.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.Strings
import com.jaehl.gameTool.common.ui.TestTags
import com.jaehl.gameTool.common.ui.componets.*
import com.jaehl.gameTool.common.ui.screens.userDetails.UserDetailsScreen
import com.jaehl.gameTool.common.ui.screens.backupList.BackupListScreen
import com.jaehl.gameTool.common.ui.screens.gameDetails.GameDetailsScreen
import com.jaehl.gameTool.common.ui.screens.gameEdit.GameEditScreen
import com.jaehl.gameTool.common.ui.screens.login.LoginScreen
import com.jaehl.gameTool.common.ui.screens.users.UsersScreen
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel

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

        LaunchedEffect(screenModel.logoutEvent){
            if(screenModel.logoutEvent){
                navigator.popAll()
                navigator.push(LoginScreen())
                screenModel.logoutEvent = false
            }
        }

        HomePage(
            loading = screenModel.pageLoading,
            games = screenModel.games,
            dialogViewModel = screenModel.dialogViewModel,
            showAdminTools = screenModel.showAdminTools,
            showEditGames = screenModel.showEditGames,
            userUnverified = screenModel.userUnverified,
            onAccountClick = {
                navigator.push(UserDetailsScreen())
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
            },
            onRefreshClick = {
                screenModel.onRefreshClick()
            },
            onCloseDialog = screenModel::closeDialog,
            onForceLogout = screenModel::forceLogout
        )
    }
}

@Composable
fun HomePage(
    loading : Boolean,
    games: List<GameModel>,
    dialogViewModel : DialogViewModel,
    showAdminTools : Boolean,
    showEditGames : Boolean,
    userUnverified : Boolean,
    onAccountClick : () -> Unit,
    onUsersClick : () -> Unit,
    onCreateGameClick : () -> Unit,
    onGameClick : (gameId: Int) -> Unit,
    onGameEditClick : (gameId: Int) -> Unit,
    onBackupClick : () -> Unit,
    onRefreshClick : () -> Unit,
    onCloseDialog : () -> Unit,
    onForceLogout : () -> Unit
) {
    val state : ScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Gray)
    ) {
        AppBar(
            title = "Home",
            actions = {
                IconButton(
                    content = {
                        Icon(Icons.Outlined.Refresh, "Refresh", tint = Color.White)
                    },
                    onClick = {
                        onRefreshClick()
                    }
                )
                IconButton(
                    modifier = Modifier.testTag("navAccountDetails"),
                    content = {
                        Icon(Icons.Outlined.AccountBox, "accountDetails", tint = Color.White)
                    },
                    onClick = {
                        onAccountClick()
                    }
                )
            }
        )
        CustomLinearProgressIndicator(loading)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(state)

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
            if(userUnverified) {
                UserMessage(
                    modifier = Modifier
                        .width(400.dp)
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally),
                    title = "User warning",
                    message = "Your account is unverified, please contact an admin to verified your account."
                )
            }
            else {
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

    ErrorDialog(dialogViewModel, onClose = onCloseDialog)
    ForceLogoutDialog(dialogViewModel, onClose = onForceLogout)
}

@Composable
fun UserMessage(
    modifier: Modifier,
    title : String,
    message : String
) {
    Card(
        modifier = modifier
            .testTag(TestTags.Home.user_message_card)
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
                        .padding(start = 10.dp, top = 12.dp, bottom = 12.dp)
                        .testTag(TestTags.Home.user_message_title),
                    color = MaterialTheme.colors.onSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = title
                )
            }
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, top = 12.dp, bottom = 12.dp)
                    .testTag(TestTags.Home.user_message_text),
                color = MaterialTheme.colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                text = message
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
            .testTag(TestTags.Home.admin_tools_card)
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
                    text = Strings.Home.adminToolsTitle
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .testTag(TestTags.Home.admin_tools_backup_button),
                    onClick = {
                        onBackupClick()
                    }
                ){
                    Text(Strings.Home.backupTitle)
                }
                Button(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .testTag(TestTags.Home.admin_tools_users_button),
                    onClick = {
                        onUsersClick()
                    }
                ){
                    Text(Strings.Home.usersTitle)
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
            .testTag(TestTags.Home.games_card)
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
                    IconButton(
                        modifier = Modifier
                            .testTag(TestTags.Home.games_add_game),
                        content = {
                            Icon(Icons.Outlined.Add, "Add Game", tint = MaterialTheme.colors.onSecondary)

                        }, onClick = {
                            onCreateGameClick()
                        }
                    )
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
            .testTag(TestTags.Home.game_row)
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
                .testTag(TestTags.Home.game_row_title)
        )
        if(showEditGames) {
            IconButton(
                modifier = Modifier
                    .testTag(TestTags.Home.game_row_edit_button),
                content = {
                    Icon(Icons.Outlined.Edit, "Edit", tint = Color.Black)
                },
                onClick = {
                    onGameEditClick(game.id)
                }
            )
        }
    }
}