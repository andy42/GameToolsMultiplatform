package com.jaehl.gameTool.common.ui.screens.login

import com.jaehl.gameTool.common.JobDispatcherTest
import com.jaehl.gameTool.common.data.repo.TokenProviderMock
import com.jaehl.gameTool.common.data.repo.UserRepoMock
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.ErrorDialogViewModel
import kotlinx.coroutines.test.*
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoginScreenModelTest {

    private val dispatcher = StandardTestDispatcher ()
    private val userRepoMock = UserRepoMock()
    private val tokenProviderMock = TokenProviderMock()

    private fun buildLoginScreenModel() : LoginScreenModel{
        return LoginScreenModel(
            JobDispatcherTest(dispatcher),
            userRepoMock,
            tokenProviderMock,
            LoginValidator(),
            RegisterValidator()
        )
    }

    @Before
    fun before(){
        userRepoMock.clear()
    }

    @Test
    fun `valid refresh proceed to HomeScreen`() = runTest(dispatcher) {
        tokenProviderMock.isRefreshTokenValid = true
        val screenModel = buildLoginScreenModel()
        assertFalse(screenModel.navigateToHome.value)
        screenModel.setup()
        advanceUntilIdle()
        assertTrue(screenModel.navigateToHome.value)
    }

    @Test
    fun `login server error test`() = runTest(dispatcher) {
        userRepoMock.loginError = UiException.ServerError(cause = null)
        val screenModel = buildLoginScreenModel()
        screenModel.onLoginUserNameChange("userName")
        screenModel.onLoginPasswordChange("password")
        screenModel.onLoginClick()
        advanceUntilIdle()
        assertEquals(screenModel.dialogViewModel.value, ErrorDialogViewModel("Error", "Oops something went wrong"))
    }

    @Test
    fun `onLogin TextFiled changed with valid values`() = runTest(dispatcher) {
        val screenModel = buildLoginScreenModel()
        val userName = "userName"
        val password = "password"
        screenModel.onLoginUserNameChange(userName)
        screenModel.onLoginPasswordChange(password)
        assertEquals(
            screenModel.loginViewModel.value,
            LoginViewModel(
                userName = TextFieldValue(value = userName),
                password = TextFieldValue(value = password)
            )
        )
    }

    @Test
    fun `onLogin TextFiled changed with invalid values`() = runTest(dispatcher) {
        val screenModel = buildLoginScreenModel()
        val userName = ""
        val password = ""
        screenModel.onLoginUserNameChange(userName)
        screenModel.onLoginPasswordChange(password)
        screenModel.onLoginClick()
        advanceUntilIdle()
        assertEquals(
            LoginViewModel(
                userName = TextFieldValue(value = userName, error = "you most enter an user name"),
                password = TextFieldValue(value = password, error = "you most enter a password")
            ),
            screenModel.loginViewModel.value
        )
    }

    @Test
    fun `onRegister TextFiled changed with valid values`() = runTest(dispatcher) {
        val screenModel = buildLoginScreenModel()
        val userName = "userName"
        val email = "test@test.com"
        val password = "password"
        val reEnterPassword = "password"
        screenModel.onRegisterUserNameChange(userName)
        screenModel.onRegisterEmailChange(email)
        screenModel.onRegisterPasswordChange(password)
        screenModel.onRegisterReEnterPasswordChange(reEnterPassword)
        assertEquals(
            RegisterViewModel(
                userName = TextFieldValue(value = userName),
                email = TextFieldValue(value = email),
                password = TextFieldValue(value = password),
                reEnterPassword = TextFieldValue(value = reEnterPassword),
            ),
            screenModel.registerViewModel.value
        )
    }

    @Test
    fun `onRegister TextFiled changed with invalid values`() = runTest(dispatcher) {
        val screenModel = buildLoginScreenModel()
        val userName = ""
        val email = ""
        val password = ""
        val reEnterPassword = "password"
        screenModel.onRegisterUserNameChange(userName)
        screenModel.onRegisterEmailChange(email)
        screenModel.onRegisterPasswordChange(password)
        screenModel.onRegisterReEnterPasswordChange(reEnterPassword)
        screenModel.onRegisterClick()
        assertEquals(
            RegisterViewModel(
                userName = TextFieldValue(value = userName, error = "you most enter a userName"),
                email = TextFieldValue(value = email, error = "you most enter an email"),
                password = TextFieldValue(value = password, error = "you most enter a password"),
                reEnterPassword = TextFieldValue(value = reEnterPassword, error = "you password does not match"),
            ),
            screenModel.registerViewModel.value
        )
    }
}