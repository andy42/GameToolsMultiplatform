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
import com.jaehl.gameTool.common.ui.componets.ListItem
import com.jaehl.gameTool.common.ui.componets.ListPickerDialog

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
                        .width(300.dp)
                        .height(400.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        UserList(
                            screenModel.users,
                            onRoleChangeClick = {
                                screenModel.onUserRoleClick(it)
                            }
                        )
                    }
                }
            }
        }

        val selectedRoleIndex = remember { mutableStateOf(-1) }

        if(screenModel.dialogConfig.value is UsersScreenModel.DialogConfig.RolePickerConfig) {
            ListPickerDialog(
                title = "Change User Role",
                list = User.Role.values().map { ListItem(it.name, it) },
                onClose = {
                    selectedRoleIndex.value = -1
                    screenModel.closeRolePicker()
                },
                positiveText = "Change Role",
                selectedIndex = selectedRoleIndex.value,
                onItemClick = {
                    selectedRoleIndex.value = it
                },
                onItemPicked = {
                    val userId = (screenModel.dialogConfig.value as UsersScreenModel.DialogConfig.RolePickerConfig).userId
                    selectedRoleIndex.value = -1
                    screenModel.closeRolePicker()
                    screenModel.changeUserRole(userId = userId, role = it)
                }
            )
        }
    }
}

@Composable
fun UserList(
    users : List<UserModel>,
    onRoleChangeClick : (userId : Int) -> Unit
){
    users.forEach {
        UserRow(
            user = it,
            onRoleChangeClick = onRoleChangeClick,
        )
    }
}

@Composable
fun UserRow(
    user : UserModel,
    onRoleChangeClick : (userId : Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onRoleChangeClick(user.id)
            }
    ) {
        Text(user.name)
        Text(user.role)
    }
}