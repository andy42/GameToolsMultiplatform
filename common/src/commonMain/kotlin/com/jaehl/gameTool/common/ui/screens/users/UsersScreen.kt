package com.jaehl.gameTool.common.ui.screens.users

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.componets.CustomLinearProgressIndicator
import com.jaehl.gameTool.common.ui.componets.ErrorDialog
import com.jaehl.gameTool.common.ui.screens.userDetails.UserDetailsScreen

class UsersScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<UsersScreenModel>()

        LifecycleEffect(
            onStarted = {
                screenModel.setup()
            }
        )

        UsersPage(
            loading = screenModel.pageLoading.value,
            users = screenModel.users,
            onBackClick = {
                navigator.pop()
            },
            onUserClick = { userId ->
                navigator.push(UserDetailsScreen(userId))
            }
        )

        val dialogConfig = screenModel.dialogConfig.value
        if(dialogConfig is UsersScreenModel.DialogConfig.ErrorDialog){
            ErrorDialog(
                title = dialogConfig.title,
                message = dialogConfig.message,
                buttonText = "Ok",
                onClick = {
                    screenModel.closeDialog()
                }
            )
        }
    }
}

@Composable
fun UsersPage(
    loading : Boolean,
    users : List<UserModel>,
    onBackClick : () -> Unit,
    onUserClick : (userId : Int) -> Unit
){
    val state : ScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        AppBar(
            title = "Users",
            showBackButton = true,
            onBackClick = {
                onBackClick()
            }
        )
        CustomLinearProgressIndicator(loading)
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxSize()
                .verticalScroll(state)

        ) {
            Card(
                modifier = Modifier
                    .width(400.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    UserList(
                        users,
                        onUserClick = { userId ->
                            onUserClick(userId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserList(
    users : List<UserModel>,
    onUserClick : (userId : Int) -> Unit
){
    users.forEach {
        UserRow(
            user = it,
            onUserClick = onUserClick,
        )
    }
}

@Composable
fun UserRow(
    user : UserModel,
    onUserClick : (userId : Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onUserClick(user.id)
            }
            .padding(15.dp)
    ) {
        Column {
            Text(user.name)
            Text(user.email)
        }
        Text(user.role)
    }
}