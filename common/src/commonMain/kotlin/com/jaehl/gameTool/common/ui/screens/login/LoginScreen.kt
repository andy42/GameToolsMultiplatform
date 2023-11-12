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
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel

import com.jaehl.gameTool.common.ui.screens.login.LoginScreenModel.PageEvent

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

        LaunchedEffect(screenModel.navigateToHome){
            if(screenModel.navigateToHome) {
                navigator.push(HomeScreen())
                screenModel.clearEvents()
            }
        }

        LoginPage(
            loading = screenModel.pageLoading,
            pageState = screenModel.pageState,
            loginViewModel = screenModel.loginViewModel,
            registerViewModel = screenModel.registerViewModel,
            dialogViewModel = screenModel.dialogViewModel,
            onHomeStateChange = screenModel::onHomeStateChange,
            onEvent = screenModel::onEvent,
            closeDialog = screenModel::closeDialog
        )
    }
}

@Composable
fun LoginPage(
    loading : Boolean,
    pageState : LoginScreenModel.PageState,
    loginViewModel : LoginViewModel,
    registerViewModel : RegisterViewModel,
    dialogViewModel : DialogViewModel,
    onHomeStateChange : (pageState : LoginScreenModel.PageState) -> Unit,
    onEvent : (event : PageEvent) -> Unit,
    closeDialog : () -> Unit
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
                            loading = loading,
                            loginViewModel = loginViewModel,
                            onEvent = onEvent
                        )
                    }
                    LoginScreenModel.PageState.Register -> {
                        RegisterBox(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            loading = loading,
                            registerViewModel = registerViewModel,
                            onEvent = onEvent
                        )
                    }
                }
            }
        }
    }

    ErrorDialog(dialogViewModel, closeDialog)
}


@Composable
fun LoginBox(modifier: Modifier,
             loading : Boolean,
             loginViewModel : LoginViewModel,
             onEvent : (event : LoginScreenModel.PageEvent) -> Unit
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
                onEvent(PageEvent.LoginUserNameChange(value))
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
                onEvent(PageEvent.LoginPasswordChange(value))
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
                onEvent(PageEvent.LoginButtonClick)
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
                onEvent : (event : LoginScreenModel.PageEvent) -> Unit
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
                onEvent(PageEvent.RegisterUserNameChange(value))
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
                onEvent(PageEvent.RegisterEmailChange(value))
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
                onEvent(PageEvent.RegisterPasswordChange(value))
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
                onEvent(PageEvent.RegisterReEnterPasswordChange(value))
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
                onEvent(PageEvent.RegisterButtonClick)
            },
        ) {
            Text("Register")
        }
    }
}