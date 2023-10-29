package com.jaehl.gameTool.common.ui.screens.users

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.componets.ErrorDialog
import com.jaehl.gameTool.common.ui.componets.ListItem
import com.jaehl.gameTool.common.ui.componets.ListPickerDialog
import com.jaehl.gameTool.common.ui.screens.home.HomeScreenModel
import com.jaehl.gameTool.common.ui.screens.userDetails.UserDetailsScreen

class UsersScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<UsersScreenModel>()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            AppBar(
                title = "Users",
                backButtonEnabled = true,
                onBackClick = {
                    navigator.pop()
                }
            )
            Column(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxSize()

            ) {
                Card(
                    modifier = Modifier
                        .width(400.dp)
                        .height(400.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        UserList(
                            screenModel.users,
                            onUserClick = { userId ->
                                navigator.push(UserDetailsScreen(userId))
                            }
                        )
                    }
                }
            }
        }

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