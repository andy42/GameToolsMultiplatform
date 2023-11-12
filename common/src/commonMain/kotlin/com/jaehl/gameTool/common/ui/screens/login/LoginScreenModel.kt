package com.jaehl.gameTool.common.ui.screens.login

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.screens.login.usecases.*
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.ClosedDialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.ErrorDialogViewModel
import kotlinx.coroutines.launch

data class LoginViewModel(
    val userName : TextFieldValue = TextFieldValue(value = ""),
    val password : TextFieldValue = TextFieldValue(value = "")
)

data class RegisterViewModel(
    val userName : TextFieldValue = TextFieldValue(),
    val email : TextFieldValue = TextFieldValue(),
    val password : TextFieldValue = TextFieldValue(),
    val reEnterPassword : TextFieldValue = TextFieldValue()
)

class LoginScreenModel(
    private val jobDispatcher : JobDispatcher,
    private val tokenProvider : TokenProvider,
    private val validateLoginUserName: ValidateLoginUserName,
    private val validateLoginPassword: ValidateLoginPassword,
    private val validateRegisterUserName: ValidateRegisterUserName,
    private val validateRegisterEmail: ValidateRegisterEmail,
    private val validateRegisterPassword: ValidateRegisterPassword,
    private val validateRegisterReEnterPassword: ValidateRegisterReEnterPassword,
    private val loginUseCase : LoginUseCase,
    private val registerUseCase : RegisterUseCase
) : ScreenModel {
    var loginViewModel by mutableStateOf(LoginViewModel())
        private set

    var registerViewModel by mutableStateOf(RegisterViewModel())
        private set

    var pageState by mutableStateOf(PageState.Loign)
        private set

    var pageLoading by mutableStateOf(false)
        private set

    var navigateToHome by mutableStateOf(false)
        private set

    var dialogViewModel by mutableStateOf<DialogViewModel>(ClosedDialogViewModel)
        private set

    fun setup() = launchIo(jobDispatcher, ::onException) {
        if(tokenProvider.isRefreshTokenValid()) {
            navigateToHome = true
        }
    }

    override fun onDispose() {
        super.onDispose()
        navigateToHome  = false
    }

    fun clearEvents() {
        navigateToHome = false
    }

    fun onHomeStateChange(state : PageState) {
        loginViewModel = LoginViewModel()
        registerViewModel = RegisterViewModel()
        pageState = state
    }

    private fun showDialog(dialogViewModel : DialogViewModel) {
        this.dialogViewModel = dialogViewModel
    }

    private fun handelUiException(e : UiException) {
        when (e) {
            is UiException.ForbiddenError -> {
                dialogViewModel = ErrorDialogViewModel(
                    title = "Login Error",
                    message = "Login credentials incorrect"
                )
            }
            is UiException.ServerConnectionError -> {
                dialogViewModel = ErrorDialogViewModel(
                    title = "Connection Error",
                    message = "Oops, seems like you can not connect to the server"
                )
            }
            else -> {
                dialogViewModel = ErrorDialogViewModel(
                    title = "Error",
                    message = "Oops something went wrong"
                )
            }
        }
    }

    private fun onException(t: Throwable){
        if (t is UiException){
            handelUiException(t)
        }
        System.err.println(t.message)
        pageLoading = false
    }

    fun onNavigatedToHome(){
        navigateToHome = false
    }

    fun closeDialog() {
        dialogViewModel = ClosedDialogViewModel
    }

    sealed class PageEvent {
        data class LoginUserNameChange(val userName : String) : PageEvent()
        data class LoginPasswordChange(val password : String) : PageEvent()
        data object LoginButtonClick : PageEvent()
        data class RegisterUserNameChange(val userName : String) : PageEvent()
        data class RegisterEmailChange(val email : String) : PageEvent()
        data class RegisterPasswordChange(val password : String) : PageEvent()
        data class RegisterReEnterPasswordChange(val reEnterPassword : String) : PageEvent()
        data object RegisterButtonClick : PageEvent()
    }

    private fun loginClicked() {
        val userName = loginViewModel.userName.value
        val password = loginViewModel.password.value

        val userNameResult = validateLoginUserName(userName)
        userNameResult.errorMessage?.let { errorMessage ->
            loginViewModel = loginViewModel.copy(
                userName = TextFieldValue(userName, errorMessage)
            )
        }

        val passwordResult = validateLoginPassword(password)
        passwordResult.errorMessage?.let { errorMessage ->
            loginViewModel = loginViewModel.copy(
                password = TextFieldValue(password, errorMessage)
            )
        }

        //exit if validation failed
        if(listOf(userNameResult, passwordResult).any { !it.success }) return

        coroutineScope.launch(jobDispatcher.io()) {
            if(loginUseCase(
                    userName = userName,
                    password = password,
                    showDialog = ::showDialog,
                    showLoading = { loading ->
                        pageLoading = loading
                    }
                )){
                navigateToHome = true
            }
        }
    }

    fun registerClicked() {
        val userName = registerViewModel.userName.value
        val email = registerViewModel.email.value
        val password = registerViewModel.password.value
        val reEnterPassword = registerViewModel.reEnterPassword.value

        val userNameResult = validateRegisterUserName(userName)
        userNameResult.errorMessage?.let { errorMessage ->
            registerViewModel = registerViewModel.copy(
                userName = TextFieldValue(userName, errorMessage)
            )
        }

        val emailResult = validateRegisterEmail(userName)
        emailResult.errorMessage?.let { errorMessage ->
            registerViewModel = registerViewModel.copy(
                email = TextFieldValue(email, errorMessage)
            )
        }

        val passwordResult = validateRegisterPassword(password)
        passwordResult.errorMessage?.let { errorMessage ->
            registerViewModel = registerViewModel.copy(
                password = TextFieldValue(password, errorMessage)
            )
        }

        val reEnterPasswordResult = validateRegisterReEnterPassword(
            password = password,
            reEnterPassword = reEnterPassword
        )
        reEnterPasswordResult.errorMessage?.let { errorMessage ->
            registerViewModel = registerViewModel.copy(
                reEnterPassword = TextFieldValue(reEnterPassword, errorMessage)
            )
        }

        //exit if validation failed
        if(listOf(userNameResult, emailResult, passwordResult, reEnterPasswordResult).any { !it.success }) return

        coroutineScope.launch(jobDispatcher.io()) {
            if(registerUseCase(
                    userName = userName,
                    email = email,
                    password = password,
                    showDialog = ::showDialog,
                    showLoading = { loading ->
                        pageLoading = loading
                    }
                )){
                navigateToHome = true
            }
        }
    }

    fun onEvent(pageEvent : PageEvent) {
        when(pageEvent){
            is PageEvent.LoginUserNameChange -> {
                loginViewModel = loginViewModel.copy(
                    userName = TextFieldValue(pageEvent.userName)
                )
            }
            is PageEvent.LoginPasswordChange -> {
                loginViewModel = loginViewModel.copy(
                    password = TextFieldValue(pageEvent.password)
                )
            }
            is PageEvent.RegisterUserNameChange -> {
                registerViewModel = registerViewModel.copy(
                    userName = TextFieldValue(pageEvent.userName)
                )
            }
            is PageEvent.RegisterEmailChange -> {
                registerViewModel = registerViewModel.copy(
                    email = TextFieldValue(pageEvent.email)
                )
            }
            is PageEvent.RegisterPasswordChange -> {
                registerViewModel = registerViewModel.copy(
                    password = TextFieldValue(pageEvent.password)
                )
            }
            is PageEvent.RegisterReEnterPasswordChange -> {
                registerViewModel = registerViewModel.copy(
                    reEnterPassword = TextFieldValue(pageEvent.reEnterPassword)
                )
            }
            is PageEvent.LoginButtonClick -> {
                loginClicked()
            }
            is PageEvent.RegisterButtonClick -> {
                registerClicked()
            }
        }
    }

    enum class PageState {
        Loign,
        Register
    }
}