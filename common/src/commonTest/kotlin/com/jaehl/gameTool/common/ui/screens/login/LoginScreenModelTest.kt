package com.jaehl.gameTool.common.ui.screens.login

import com.jaehl.gameTool.common.JobDispatcherTest
import com.jaehl.gameTool.common.data.repo.TokenProviderMock
import com.jaehl.gameTool.common.data.repo.UserRepoMock
import com.jaehl.gameTool.common.ui.Strings
import com.jaehl.gameTool.common.ui.UiExceptionHandlerImp
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.screens.login.usecases.*
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.ErrorDialogViewModel
import com.jaehl.gameTool.common.ui.screens.login.LoginScreenModel.PageEvent
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

    private val loginUseCase = LoginUseCaseImp(
        JobDispatcherTest(dispatcher),
        userRepoMock,
        uiExceptionHandler = UiExceptionHandlerImp()
    )

    private val registerUseCase = RegisterUseCaseImp(
        JobDispatcherTest(dispatcher),
        userRepoMock,
        uiExceptionHandler = UiExceptionHandlerImp()
    )

    private fun buildLoginScreenModel() : LoginScreenModel{
        return LoginScreenModel(
            JobDispatcherTest(dispatcher),
            tokenProviderMock,
            ValidateLoginUserName(),
            ValidateLoginPassword(),
            ValidateRegisterUserName(),
            ValidateRegisterEmail(),
            ValidateRegisterPassword(),
            ValidateRegisterReEnterPassword(),
            loginUseCase,
            registerUseCase
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
        assertFalse(screenModel.navigateToHome)
        screenModel.setup()
        advanceUntilIdle()
        assertTrue(screenModel.navigateToHome)
    }

    @Test
    fun `login server error test`() = runTest(dispatcher) {
        userRepoMock.loginError = UiException.ServerError(cause = null)
        val screenModel = buildLoginScreenModel()
        screenModel.onEvent(PageEvent.LoginUserNameChange("userName"))
        screenModel.onEvent(PageEvent.LoginPasswordChange("password"))
        screenModel.onEvent(PageEvent.LoginButtonClick)
        advanceUntilIdle()

        assertEquals(screenModel.dialogViewModel, ErrorDialogViewModel(Strings.General.dialogErrorTitle, Strings.General.dialogErrorGeneralMessage))
    }

    @Test
    fun `onLogin TextFiled changed with valid values`() = runTest(dispatcher) {
        val screenModel = buildLoginScreenModel()
        val userName = "userName"
        val password = "password"
        screenModel.onEvent(PageEvent.LoginUserNameChange(userName))
        screenModel.onEvent(PageEvent.LoginPasswordChange(password))
        screenModel.onEvent(PageEvent.LoginButtonClick)
        advanceUntilIdle()
        assertEquals(
            screenModel.loginViewModel,
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
        screenModel.onEvent(PageEvent.LoginUserNameChange(userName))
        screenModel.onEvent(PageEvent.LoginPasswordChange(password))
        screenModel.onEvent(PageEvent.LoginButtonClick)
        advanceUntilIdle()
        assertEquals(
            LoginViewModel(
                userName = TextFieldValue(value = userName, error = Strings.Login.validateLoginUserNameEmpty),
                password = TextFieldValue(value = password, error = Strings.Login.validateLoginPasswordEmpty)
            ),
            screenModel.loginViewModel
        )
    }

    @Test
    fun `onRegister TextFiled changed with valid values`() = runTest(dispatcher) {
        val screenModel = buildLoginScreenModel()
        val userName = "userName"
        val email = "test@test.com"
        val password = "password"
        val reEnterPassword = "password"

        screenModel.onEvent(PageEvent.RegisterUserNameChange(userName))
        screenModel.onEvent(PageEvent.RegisterEmailChange(email))
        screenModel.onEvent(PageEvent.RegisterPasswordChange(password))
        screenModel.onEvent(PageEvent.RegisterReEnterPasswordChange(reEnterPassword))
        screenModel.onEvent(PageEvent.RegisterButtonClick)
        advanceUntilIdle()
        assertEquals(
            RegisterViewModel(
                userName = TextFieldValue(value = userName),
                email = TextFieldValue(value = email),
                password = TextFieldValue(value = password),
                reEnterPassword = TextFieldValue(value = reEnterPassword),
            ),
            screenModel.registerViewModel
        )
    }

    @Test
    fun `onRegister TextFiled changed with invalid values`() = runTest(dispatcher) {
        val screenModel = buildLoginScreenModel()
        val userName = ""
        val email = ""
        val password = ""
        val reEnterPassword = "password"
        screenModel.onEvent(PageEvent.RegisterUserNameChange(userName))
        screenModel.onEvent(PageEvent.RegisterEmailChange(email))
        screenModel.onEvent(PageEvent.RegisterPasswordChange(password))
        screenModel.onEvent(PageEvent.RegisterReEnterPasswordChange(reEnterPassword))
        screenModel.onEvent(PageEvent.RegisterButtonClick)
        advanceUntilIdle()

        val registerViewModel = screenModel.registerViewModel
        assertEquals(
            TextFieldValue(value = userName, error = Strings.Login.validateRegisterUserNameEmpty),
            registerViewModel.userName
        )
        assertEquals(
            TextFieldValue(value = email, error = Strings.Login.validateRegisterEmailEmpty),
            registerViewModel.email
        )
        assertEquals(
            TextFieldValue(value = password, error = Strings.Login.validateRegisterPasswordEmpty),
            registerViewModel.password
        )
        assertEquals(
            TextFieldValue(value = reEnterPassword, error = Strings.Login.validateRegisterReEnterPasswordMismatch),
            registerViewModel.reEnterPassword
        )
    }
}