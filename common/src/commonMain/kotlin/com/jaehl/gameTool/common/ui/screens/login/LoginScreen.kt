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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.TestTags
import com.jaehl.gameTool.common.ui.componets.CustomLinearProgressIndicator
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

        LoginPage(
            loading = screenModel.pageLoading.value,
            pageState = screenModel.pageState.value,
            loginViewModel = screenModel.loginViewModel.value,
            registerViewModel = screenModel.registerViewModel.value,
            onHomeStateChange = screenModel::onHomeStateChange,
            loginInterface = screenModel,
            registerInterface = screenModel,
        )

        val dialogViewModel = screenModel.dialogViewModel.value
        ErrorDialog(dialogViewModel, screenModel::closeDialog)
    }
}

@Composable
fun LoginPage(
    loading : Boolean,
    pageState : LoginScreenModel.PageState,
    loginViewModel : LoginViewModel,
    registerViewModel : RegisterViewModel,
    onHomeStateChange : (pageState : LoginScreenModel.PageState) -> Unit,
    loginInterface : LoginInterface,
    registerInterface: RegisterInterface
) {
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
                            .background(if (pageState == LoginScreenModel.PageState.Loign) MaterialTheme.colors.primary else MaterialTheme.colors.surface)
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight()
                            .clickable (
                                onClick = {
                                    onHomeStateChange(LoginScreenModel.PageState.Loign)
                                }
                            )
                            .testTag(TestTags.Login.login_tab)
                    ) {
                        Text(
                            text = "Login",
                            color = if (pageState == LoginScreenModel.PageState.Loign) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(if (pageState == LoginScreenModel.PageState.Register) MaterialTheme.colors.primary else MaterialTheme.colors.surface)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clickable (
                                onClick = {
                                    onHomeStateChange(LoginScreenModel.PageState.Register)
                                }
                            )
                            .testTag(TestTags.Login.register_tab)
                    ) {
                        Text(
                            text = "Register",
                            color = if (pageState == LoginScreenModel.PageState.Register) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
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
                CustomLinearProgressIndicator(loading)

                when(pageState){
                    LoginScreenModel.PageState.Loign -> {
                        LoginBox(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            //screenModel = screenModel,
                            loading = loading,
                            loginViewModel = loginViewModel,
                            loginInterface = loginInterface,
                        )
                    }
                    LoginScreenModel.PageState.Register -> {
                        RegisterBox(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            //screenModel = screenModel,
                            loading = loading,
                            registerViewModel = registerViewModel,
                            registerInterface = registerInterface
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun LoginBox(modifier: Modifier,
             loading : Boolean,
             loginViewModel : LoginViewModel,
             loginInterface : LoginInterface
){

    Column(
        modifier = modifier
            .padding(10.dp),
    ) {

        StyledOutlinedTextField(
            loginViewModel.userName,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("UserName") },
            singleLine = true,
            enabled = !loading,
            onValueChange = { value ->
                loginInterface.onLoginUserNameChange(value)
            },
            testTag = TestTags.Login.user_name,
        )
        StyledPasswordOutlinedTextField(
            loginViewModel.password,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("Password") },
            singleLine = true,
            enabled = !loading,
            onValueChange = { value ->
                loginInterface.onLoginPasswordChange(value)
            },
            testTag = TestTags.Login.password,
        )

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
                .testTag(TestTags.Login.login_button),
            enabled = !loading,
            onClick = {
                loginInterface.onLoginClick()
            },
        ) {
            Text("Login")
        }
    }
}

@Composable
fun RegisterBox(modifier: Modifier,
                loading : Boolean,
                registerViewModel : RegisterViewModel,
                registerInterface: RegisterInterface
){

    Column(
        modifier = modifier
            .padding(10.dp)
            .testTag("registerColumn")
    ) {
        StyledOutlinedTextField(
            registerViewModel.userName,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("UserName") },
            singleLine = true,
            enabled = !loading,
            onValueChange = { value ->
                registerInterface.onRegisterUserNameChange(value)
            },
            testTag = TestTags.Login.user_name
        )
        StyledOutlinedTextField(
            registerViewModel.email,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("Email") },
            singleLine = true,
            enabled = !loading,
            onValueChange = { value ->
                registerInterface.onRegisterEmailChange(value)
            },
            testTag = TestTags.Login.email
        )
        StyledPasswordOutlinedTextField(
            registerViewModel.password,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("Password") },
            singleLine = true,
            enabled = !loading,
            onValueChange = { value ->
                registerInterface.onRegisterPasswordChange(value)
            },
            testTag = TestTags.Login.password
        )

        StyledPasswordOutlinedTextField(
            registerViewModel.reEnterPassword,
            modifier = Modifier
                .padding(top = 5.dp),
            label = { Text("re-enter Password") },
            singleLine = true,
            enabled = !loading,
            onValueChange = { value ->
                registerInterface.onRegisterReEnterPasswordChange(value)
            },
            testTag = TestTags.Login.re_enter_password
        )

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
                .testTag(TestTags.Login.register_button),
            enabled = !loading,
            onClick = {
                registerInterface.onRegisterClick()
            },
        ) {
            Text("Register")
        }
    }
}