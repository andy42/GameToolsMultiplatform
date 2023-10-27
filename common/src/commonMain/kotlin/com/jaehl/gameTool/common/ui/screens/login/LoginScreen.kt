package com.jaehl.gameTool.common.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.componets.ErrorDialog
import com.jaehl.gameTool.common.ui.componets.StyledOutlinedTextField
import com.jaehl.gameTool.common.ui.componets.StyledPasswordOutlinedTextField
import com.jaehl.gameTool.common.ui.screens.home.HomeScreen

class LoginScreen : Screen{

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<LoginScreenModel>()

        LifecycleEffect(
            onStarted = {
                screenModel.setup()
            }
        )

        LaunchedEffect(screenModel.navigateToHome.value){
            if(screenModel.navigateToHome.value) {
                navigator.push(HomeScreen())
                screenModel.navigateToHome.value = false
            }
        }

        val focusManager = LocalFocusManager.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Gray)
        ) {
            Card(
                modifier = Modifier
                    .width(400.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .onKeyEvent {
                            if((it.key == Key.Enter || (it.key == Key.Tab && it.type == KeyEventType.KeyDown )) ){
                                focusManager.moveFocus(FocusDirection.Down)
                                true
                            }
                            else false
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .height(50.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(if (screenModel.homeState.value == LoginScreenModel.PageState.Loign) MaterialTheme.colors.primary else MaterialTheme.colors.surface)
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight()
                                .clickable (
                                    onClick = {
                                        screenModel.onHomeStateChange(LoginScreenModel.PageState.Loign)
                                    }
                                )
                        ) {
                            Text(
                                text = "Login",
                                color = if (screenModel.homeState.value == LoginScreenModel.PageState.Loign) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(if (screenModel.homeState.value == LoginScreenModel.PageState.Register) MaterialTheme.colors.primary else MaterialTheme.colors.surface)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clickable (
                                    onClick = {
                                        screenModel.onHomeStateChange(LoginScreenModel.PageState.Register)
                                    }
                                )
                        ) {
                            Text(
                                text = "Register",
                                color = if (screenModel.homeState.value == LoginScreenModel.PageState.Register) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colors.primary)
                            .fillMaxWidth()
                            .height(2.dp)
                    )
                    when(screenModel.homeState.value){
                        LoginScreenModel.PageState.Loign -> {
                            LoginBox(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                screenModel = screenModel,
                                loginViewModel = screenModel.loginViewModel.value
                            )
                        }
                        LoginScreenModel.PageState.Register -> {
                            RegisterBox(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                screenModel = screenModel,
                                registerViewModel = screenModel.registerViewModel.value
                            )
                        }
                    }
                }
            }
        }
        val dialogConfig = screenModel.dialogConfig.value
        if(dialogConfig is LoginScreenModel.DialogConfig.ErrorDialog){
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
fun LoginBox(modifier: Modifier,
             screenModel : LoginScreenModel,
             loginViewModel : LoginViewModel){

    Column(
        modifier = modifier
            .padding(10.dp)
    ) {

        StyledOutlinedTextField(
            loginViewModel.userName,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("UserName") },
            singleLine = true,
            enabled = !screenModel.pageLoading.value,
            onValueChange = { value ->
                screenModel.onLoginUserNameChange(value)
            },
        )
        StyledPasswordOutlinedTextField(
            loginViewModel.password,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("Password") },
            singleLine = true,
            enabled = !screenModel.pageLoading.value,
            onValueChange = { value ->
                screenModel.onLoginPasswordChange(value)
            },
        )

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp),
            enabled = !screenModel.pageLoading.value,
            onClick = {
                screenModel.onLoginClick()
            },
        ) {
            Text("Login")
        }
    }
}

@Composable
fun RegisterBox(modifier: Modifier,
                screenModel : LoginScreenModel,
                registerViewModel : RegisterViewModel){

    Column(
        modifier = modifier
            .padding(10.dp)
    ) {
        StyledOutlinedTextField(
            registerViewModel.userName,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("UserName") },
            singleLine = true,
            enabled = !screenModel.pageLoading.value,
            onValueChange = { value ->
                screenModel.onRegisterUserNameChange(value)
            }
        )
        StyledOutlinedTextField(
            registerViewModel.email,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("Email") },
            singleLine = true,
            enabled = !screenModel.pageLoading.value,
            onValueChange = { value ->
                screenModel.onRegisterEmailChange(value)
            }
        )
        StyledPasswordOutlinedTextField(
            registerViewModel.password,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("Password") },
            singleLine = true,
            enabled = !screenModel.pageLoading.value,
            onValueChange = { value ->
                screenModel.onRegisterPasswordChange(value)
            }
        )

        StyledPasswordOutlinedTextField(
            registerViewModel.reEnterPassword,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("re-enter Password") },
            singleLine = true,
            enabled = !screenModel.pageLoading.value,
            onValueChange = { value ->
                screenModel.onRegisterReEnterPasswordChange(value)
            }
        )

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp),
            onClick = {
                screenModel.onRegisterClick()
            },
        ) {
            Text("Register")
        }
    }
}