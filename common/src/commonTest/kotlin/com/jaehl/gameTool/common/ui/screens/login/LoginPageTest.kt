package com.jaehl.gameTool.common.ui.screens.login


import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.jaehl.gameTool.common.ui.TestTags
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.viewModel.ClosedDialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

import com.jaehl.gameTool.common.ui.screens.login.LoginScreenModel.PageEvent

class LoginPageTest {

    @get:Rule
    val compose = createComposeRule()

    private val loginPageInterface = LoginPageInterface()
    
    private fun findUserName() = compose.onNodeWithTag(TestTags.Login.user_name)
    private fun findUserNameError() = compose.onNodeWithTag(TestTags.General.textFieldError(TestTags.Login.user_name))

    private fun findEmail() = compose.onNodeWithTag(TestTags.Login.email)
    private fun findEmailError() = compose.onNodeWithTag(TestTags.General.textFieldError(TestTags.Login.email))

    private fun findPassword() = compose.onNodeWithTag(TestTags.Login.password)
    private fun findPasswordError() = compose.onNodeWithTag(TestTags.General.textFieldError(TestTags.Login.password))
    private fun findPasswordHidePassword() = compose.onNodeWithTag(TestTags.General.textFieldHidePassword(TestTags.Login.password))

    private fun findReEnterPassword() = compose.onNodeWithTag(TestTags.Login.re_enter_password)
    private fun findReEnterPasswordError() = compose.onNodeWithTag(TestTags.General.textFieldError(TestTags.Login.re_enter_password))
    private fun findReEnterPasswordHidePassword() = compose.onNodeWithTag(TestTags.General.textFieldHidePassword(TestTags.Login.re_enter_password))
    
    private fun findLoginButton() = compose.onNodeWithTag(TestTags.Login.login_button)
    private fun findRegisterButton() = compose.onNodeWithTag(TestTags.Login.register_button)

    @Test
    fun `login state actions tests`() {
        compose.setContent {
            LoginBuilder()
                .setPageState(LoginScreenModel.PageState.Loign)
                .build()
        }
        loginPageInterface.clear()

        val userNameInput = "user name test"
        findUserName().performTextInput(userNameInput)
        assertEquals(userNameInput, loginPageInterface.loginUserName, "")

        val passwordInput = "passwordtest"
        findPassword().performTextInput(passwordInput)
        assertEquals(passwordInput, loginPageInterface.loginPassword, "")

        findLoginButton().performClick()
        assertEquals(true, loginPageInterface.isLoginButtonCLicked, "")
    }

    private fun createPasswordMask(value : String) : String {
        return "•".repeat(value.length)
    }

    @Test
    fun `login state TextFieldValues tests`() {
        val userName = TextFieldValue(
            value = "user name test",
            error = "user name Error"
        )
        val password = TextFieldValue(
            value = "password test",
            error = "password Error"
        )
        compose.setContent {
            LoginBuilder()
                .setPageState(LoginScreenModel.PageState.Loign)
                .setLoginViewModel(
                    userName = userName,
                    password = password,
                )
                .build()
        }
        loginPageInterface.clear()

        findUserName().assert(hasText(userName.value))
        findUserNameError().assert(hasText(userName.error))
        findPassword().assert(hasText(
            createPasswordMask(password.value)
        ))
        findPasswordHidePassword().performClick()
        findPassword().assert(hasText(password.value))
        findPasswordError().assert(hasText(password.error))
    }

    @Test
    fun `login loading tests`() {
        compose.setContent {
            LoginBuilder()
                .setLoading(true)
                .setPageState(LoginScreenModel.PageState.Loign)
                .build()
        }
        loginPageInterface.clear()

        findUserName().assert(isNotEnabled())
        findPassword().assert(isNotEnabled())
        findLoginButton().assert(isNotEnabled())
    }

    @Test
    fun `Register loading tests`() {
        compose.setContent {
            LoginBuilder()
                .setLoading(true)
                .setPageState(LoginScreenModel.PageState.Register)
                .build()
        }
        loginPageInterface.clear()

        findUserName().assert(isNotEnabled())
        findEmail().assert(isNotEnabled())
        findPassword().assert(isNotEnabled())
        findReEnterPassword().assert(isNotEnabled())
        findRegisterButton().assert(isNotEnabled())
    }

    @Test
    fun `Register state actions tests`() {
        compose.setContent {
            LoginBuilder()
                .setPageState(LoginScreenModel.PageState.Register)
                .build()
        }
        loginPageInterface.clear()

        val userNameInput = "user name test"
        findUserName().performTextInput(userNameInput)
        assertEquals(userNameInput, loginPageInterface.registerUserName, "")

        val emailInput = "test@test.com"
        findEmail().performTextInput(emailInput)
        assertEquals(emailInput, loginPageInterface.registerEmail, "")

        val passwordInput = "password test"
        findPassword().performTextInput(passwordInput)
        assertEquals(passwordInput, loginPageInterface.registerPassword, "")

        val reEnterPasswordInput = "reEnterPassword test"
        findReEnterPassword().performTextInput(reEnterPasswordInput)
        assertEquals(reEnterPasswordInput, loginPageInterface.registerReEnterPassword, "")

        findRegisterButton().performClick()
        assertEquals(true, loginPageInterface.isRegisterButtonCLicked, "")
    }

    @Test
    fun `Register state TextFieldValues tests`() {
        val userName = TextFieldValue(
            value = "user name test",
            error = "user name Error"
        )
        val email = TextFieldValue(
            value = "email test",
            error = "email Error"
        )
        val password = TextFieldValue(
            value = "password test",
            error = "password Error"
        )
        val reEnterPassword = TextFieldValue(
            value = "reEnterPassword test",
            error = "reEnterPassword Error"
        )
        compose.setContent {
            LoginBuilder()
                .setPageState(LoginScreenModel.PageState.Register)
                .setRegisterViewModel(
                    userName = userName,
                    email = email,
                    password = password,
                    reEnterPassword = reEnterPassword
                )
                .build()
        }
        loginPageInterface.clear()

        findUserName().assert(hasText(userName.value))
        findUserNameError().assert(hasText(userName.error))

        findEmail().assert(hasText(email.value))
        findEmailError().assert(hasText(email.error))

        findPassword().assert(hasText(
            createPasswordMask(password.value)
        ))
        findPasswordHidePassword().performClick()
        findPassword().assert(hasText(password.value))
        findPasswordError().assert(hasText(password.error))

        findReEnterPassword().assert(hasText(
            createPasswordMask(reEnterPassword.value)
        ))
        findReEnterPasswordHidePassword().performClick()
        findReEnterPassword().assert(hasText(reEnterPassword.value))
        findReEnterPasswordError().assert(hasText(reEnterPassword.error))
    }

    inner class LoginBuilder(){
        private var loading = false
        private var pageState = LoginScreenModel.PageState.Loign
        private var loginViewModel = LoginViewModel()
        private var registerViewModel = RegisterViewModel()
        private var dialogViewModel : DialogViewModel = ClosedDialogViewModel

        fun setLoading(loading : Boolean) = apply {
            this.loading = loading
        }

        fun setPageState(pageState: LoginScreenModel.PageState) = apply {
            this.pageState = pageState
        }

        fun setLoginViewModel(
            userName : TextFieldValue,
            password : TextFieldValue
        ) = apply {
            loginViewModel = LoginViewModel(
                userName = userName,
                password = password
            )
        }

        fun setRegisterViewModel(
            userName : TextFieldValue,
            email : TextFieldValue,
            password : TextFieldValue,
            reEnterPassword : TextFieldValue
        ) = apply {
            registerViewModel = RegisterViewModel(
                userName = userName,
                email = email,
                password = password,
                reEnterPassword = reEnterPassword
            )
        }

        fun setDialogViewModel(dialogViewModel : DialogViewModel) = apply {
            this.dialogViewModel = dialogViewModel
        }

        @Composable
        fun build() {
            LoginPage(
                loading = loading,
                pageState = pageState,
                loginViewModel = loginViewModel,
                registerViewModel = registerViewModel,
                dialogViewModel = dialogViewModel,
                onHomeStateChange = loginPageInterface::onHomeStateChange,
                onEvent = loginPageInterface::onEvent,
                closeDialog = loginPageInterface::closeDialog
            )
        }
    }

    class LoginPageInterface(){

        var pageState : LoginScreenModel.PageState? = null
        fun onHomeStateChange(pageState: LoginScreenModel.PageState) {
            this.pageState = pageState
        }

       var loginUserName : String? = null
       var loginPassword : String? = null
       var isLoginButtonCLicked = false
       var registerUserName : String? = null
       var registerEmail : String? = null
       var registerPassword : String? = null
       var registerReEnterPassword : String? = null
       var isRegisterButtonCLicked = false

        fun onEvent(event : PageEvent) {
            when (event) {
                is PageEvent.LoginUserNameChange -> {
                    loginUserName = event.userName
                }
                is PageEvent.LoginPasswordChange -> {
                    loginPassword = event.password
                }
                is PageEvent.LoginButtonClick -> {
                    isLoginButtonCLicked = true
                }
                is PageEvent.RegisterUserNameChange -> {
                    registerUserName = event.userName
                }
                is PageEvent.RegisterEmailChange -> {
                    registerEmail = event.email
                }
                is PageEvent.RegisterPasswordChange -> {
                    registerPassword = event.password
                }
                is PageEvent.RegisterReEnterPasswordChange -> {
                    registerReEnterPassword = event.reEnterPassword
                }
                is PageEvent.RegisterButtonClick -> {
                    isRegisterButtonCLicked = true
                }
            }
        }

        var onCloseDialogClicked = false
        fun closeDialog() {
            onCloseDialogClicked = true
        }

        fun clear(){
            pageState = null
            loginUserName = null
            loginPassword = null
            isLoginButtonCLicked = false
            registerUserName = null
            registerEmail = null
            registerPassword = null
            registerReEnterPassword = null
            isRegisterButtonCLicked = false
            onCloseDialogClicked = false
        }
    }
}

