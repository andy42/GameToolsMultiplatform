package com.jaehl.gameTool.common.ui.screens.login

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.screens.itemEdit.ItemEditScreenModel
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.ItemModel

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
    val jobDispatcher : JobDispatcher,
    val userRepo : UserRepo,
    val tokenProvider : TokenProvider,
    val loginValidator : LoginValidator,
    val registerValidator: RegisterValidator
) : ScreenModel, LoginValidator.LoginValidatorListener, RegisterValidator.RegisterValidatorListener {
    var loginViewModel = mutableStateOf(LoginViewModel())
        private set

    var registerViewModel = mutableStateOf(RegisterViewModel())
        private set

    var homeState = mutableStateOf<PageState>(PageState.Loign)
        private set

    var pageLoading = mutableStateOf<Boolean>(false)

    var navigateToHome = mutableStateOf<Boolean>(false)

    val dialogConfig = mutableStateOf<DialogConfig>(DialogConfig.Closed)

    init {
        loginValidator.listener = this
        registerValidator.listener = this
    }

    fun setup() = launchIo(jobDispatcher, ::onException) {
        launchIo(jobDispatcher, ::onException) {
            if(tokenProvider.isRefreshTokenValid()) {
                navigateToHome.value = true
            }
        }
    }

    override fun onDispose() {
        super.onDispose()
        navigateToHome.value  = false
    }

    fun onLoginUserNameChange(email : String) {
        loginViewModel.value = loginViewModel.value.copy(
            userName = loginViewModel.value.userName.copy(
                value = email,
                error = ""
            )
        )

    }

    fun onLoginPasswordChange(password : String) {
        loginViewModel.value = loginViewModel.value.copy(
            password = loginViewModel.value.password.copy(
                value = password,
                error = ""
            )
        )
    }

    fun onRegisterUserNameChange(userName : String) {
        registerViewModel.value = registerViewModel.value.copy(
            userName = registerViewModel.value.userName.copy(
                value = userName,
                error = ""
            )
        )
    }

    fun onRegisterEmailChange(email : String) {
        registerViewModel.value = registerViewModel.value.copy(
            email = registerViewModel.value.email.copy(
                value = email,
                error = ""
            )
        )
    }

    fun onRegisterPasswordChange(password : String) {
        registerViewModel.value = registerViewModel.value.copy(
            password = registerViewModel.value.password.copy(
                value = password,
                error = ""
            )
        )
    }

    fun onRegisterReEnterPasswordChange(reEnterPassword : String) {
        registerViewModel.value = registerViewModel.value.copy(
            reEnterPassword = registerViewModel.value.reEnterPassword.copy(
                value = reEnterPassword,
                error = ""
            )
        )
    }

    fun onHomeStateChange(state : PageState) {
        loginViewModel.value = LoginViewModel()
        registerViewModel.value = RegisterViewModel()
        homeState.value = state
    }

    fun onLoginClick() {

        val email = loginViewModel.value.userName.value
        val password = loginViewModel.value.password.value

        if(!loginValidator.onValidate(email, password)){
            return
        }

        pageLoading.value = true

        launchIo(
            jobDispatcher,
            onException = ::onException
        ){
            userRepo.login(email, password)
            pageLoading.value = false
            navigateToHome.value = true
        }
    }

    private fun handelUiException(e : UiException) {
        when (e) {
            is UiException.ForbiddenError -> {
                dialogConfig.value = DialogConfig.ErrorDialog(
                    title = "Login Error",
                    message = "Login credentials incorrect"
                )
            }
            is UiException.ServerConnectionError -> {
                dialogConfig.value = DialogConfig.ErrorDialog(
                    title = "Connection Error",
                    message = "Oops, seems like you can not connect to the server"
                )
            }
            else -> {
                dialogConfig.value = DialogConfig.ErrorDialog(
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
        pageLoading.value = false
    }

    fun onRegisterClick() {
        val userName = registerViewModel.value.userName.value
        val email = registerViewModel.value.email.value
        val password = registerViewModel.value.password.value
        val reEnterPassword = registerViewModel.value.reEnterPassword.value

        if(!registerValidator.onValidate(
                userName= userName,
                email = email,
                password = password,
                reEnterPassword = reEnterPassword
        )){
            return
        }

        pageLoading.value = true

        launchIo(
            jobDispatcher,
            onException = ::onException
        ){
            userRepo.register(
                userName = userName,
                email = email,
                password = password
            )
            pageLoading.value = false
            navigateToHome.value = true
        }
    }

    fun onNavigatedToHome(){
        navigateToHome.value = false
    }

    fun closeDialog() {
        dialogConfig.value = DialogConfig.Closed
    }

    override fun onLoginEmailError(error: String) {
        loginViewModel.value = loginViewModel.value.copy(
            userName = loginViewModel.value.userName.copySetError(error)
        )
    }

    override fun onLoginPasswordError(error: String) {
        loginViewModel.value = loginViewModel.value.copy(
            password = loginViewModel.value.password.copySetError(error)
        )
    }

    override fun onRegisterUserNameError(error: String) {
        registerViewModel.value = registerViewModel.value.copy(
            userName = registerViewModel.value.userName.copySetError(error)
        )
    }

    override fun onRegisterEmailError(error: String) {
        registerViewModel.value = registerViewModel.value.copy(
            email = registerViewModel.value.email.copySetError(error)
        )
    }

    override fun onRegisterPasswordError(error: String) {
        registerViewModel.value = registerViewModel.value.copy(
            password = registerViewModel.value.password.copySetError(error)
        )
    }

    override fun onRegisterReEnterPasswordError(error: String) {
        registerViewModel.value = registerViewModel.value.copy(
            reEnterPassword = registerViewModel.value.reEnterPassword.copySetError(error)
        )
    }

    sealed class DialogConfig {
        data object Closed : DialogConfig()
        data class ErrorDialog(
            val title : String,
            val message : String
        ) : DialogConfig()
    }

    enum class PageState {
        Loign,
        Register
    }
}