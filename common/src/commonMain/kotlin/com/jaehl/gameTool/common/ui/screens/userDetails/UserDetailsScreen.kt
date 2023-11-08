package com.jaehl.gameTool.common.ui.screens.userDetails

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.ui.componets.*
import com.jaehl.gameTool.common.ui.screens.login.LoginScreen

class UserDetailsScreen(
    val userId : Int? = null
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<UserDetailsScreenModel>()

        LaunchedEffect(screenModel.logoutEvent.value){
            if(screenModel.logoutEvent.value){
                navigator.popAll()
                navigator.push(LoginScreen())
                screenModel.logoutEvent.value = false
            }
        }

        LifecycleEffect(
            onStarted = {
                screenModel.setup(userId)
            }
        )

        AccountDetailsPage(
            loading = screenModel.pageLoading.value,
            viewModel = screenModel.viewModel.value,
            onBackClick = {
                navigator.pop()
            },
            onLogoutClick = {
                screenModel.logoutClick()
            },
            onChangeUserRoleClick = {
                screenModel.onChangeUserRoleClick()
            },
            onChangePasswordClick = {
                screenModel.onChangePasswordClick()
            }
        )

        val dialogConfig = screenModel.dialogConfig.value
        if(dialogConfig is UserDetailsScreenModel.DialogConfig.ErrorDialog){
            ErrorDialog(
                title = dialogConfig.title,
                message = dialogConfig.message,
                buttonText = "Ok",
                onClick = {
                    screenModel.closeDialog()
                }
            )
        }

        val selectedRoleIndex = remember { mutableStateOf(-1) }

        if(dialogConfig is UserDetailsScreenModel.DialogConfig.RolePickerConfig) {
            ListPickerDialog(
                title = "Change User Role",
                list = User.Role.entries.map { ListItem(it.name, it) },
                onClose = {
                    selectedRoleIndex.value = -1
                    screenModel.closeDialog()
                },
                positiveText = "Change Role",
                selectedIndex = selectedRoleIndex.value,
                onItemClick = {
                    selectedRoleIndex.value = it
                },
                onItemPicked = {
                    val userId = dialogConfig.userId
                    selectedRoleIndex.value = -1
                    screenModel.closeDialog()
                    screenModel.changeUserRole(userId = userId, role = it)
                }
            )
        }

        if(dialogConfig is UserDetailsScreenModel.DialogConfig.ChangePasswordDialog) {
            ChangePasswordDialog(
                title = "Change Password",
                onCloseClick = {
                    screenModel.closeDialog()
                },
                password = dialogConfig.password,
                reEnterPassword = dialogConfig.reEnterPassword,
                onPasswordChange = { value ->
                    screenModel.onDialogPasswordChange(value)
                },
                onReEnterPasswordChange = { value ->
                    screenModel.onDialogReEnterPasswordChange(value)
                },
                onChangePasswordClick = {
                    screenModel.onDialogPasswordChangeClick()
                },
            )
        }
    }
}

@Composable
fun AccountDetailsPage(
    loading : Boolean,
    viewModel : UserDetailsScreenModel.ViewModel,
    onBackClick : () -> Unit,
    onLogoutClick : () -> Unit,
    onChangeUserRoleClick : () -> Unit,
    onChangePasswordClick : () -> Unit
) {
    val state : ScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Gray)
    ) {
        AppBar(
            title = "User",
            showBackButton = true,
            onBackClick = {
                onBackClick()
            }
        )
        CustomLinearProgressIndicator(loading)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(state)

        ) {
            if(viewModel.showAdminTools){
                AdminTools(
                    modifier = Modifier
                        .width(400.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp),
                    onChangePasswordClick = {
                        onChangePasswordClick()
                    },
                    onChangeRoleClick = {
                        onChangeUserRoleClick()
                    }
                )
            }
            UserCard(
                modifier = Modifier
                    .width(400.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp),
                viewModel = viewModel.userModel,
                onChangePasswordClick = onChangePasswordClick
            )
            if (viewModel.showLogout){
                Row(
                    modifier = Modifier
                        .width(400.dp)
                        .align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .testTag("logout"),
                        onClick = {
                            onLogoutClick()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                    ) {
                        Text(
                            text = "Logout",
                            color = MaterialTheme.colors.onError
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminTools(
    modifier: Modifier,
    onChangePasswordClick : () -> Unit,
    onChangeRoleClick : () -> Unit,
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
                        onChangeRoleClick()
                    }
                ){
                    Text("Change Role")
                }
                Button(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    onClick = {
                        onChangePasswordClick()
                    }
                ){
                    Text("Change Password")
                }
            }

        }
    }
}

@Composable
fun UserCard(
    modifier: Modifier,
    viewModel : UserDetailsScreenModel.UserViewModel,
    onChangePasswordClick : () -> Unit,
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
                    text = "User"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "UserName : "
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    text = viewModel.userName
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Email : "
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    text = viewModel.email
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Role : "
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    text = viewModel.role
                )
            }

            if(viewModel.showPasswordChange) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp, top = 12.dp, bottom = 12.dp),
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = "Password : "
                    )
                    Button(
                        modifier = Modifier
                            .padding(start = 10.dp),
                        onClick = {
                            onChangePasswordClick()
                        }
                    ) {
                        Text("Change Password")
                    }
                }
            }
        }
    }
}