package com.jaehl.gameTool.common.ui.screens.login

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.TestTags
import com.jaehl.gameTool.common.ui.screens.home.HomeRobot
import org.hamcrest.CoreMatchers
import org.junit.Assert

class LoginScreenRobot(private val device: UiDevice) {

    enum class TextFieldId(val value : String) {
        userName(TestTags.Login.user_name),
        email(TestTags.Login.email),
        password(TestTags.Login.password),
        reEnterPassword(TestTags.Login.re_enter_password)
    }

    enum class ButtonId(val value : String) {
        loginButton(TestTags.Login.login_button),
        registerButton(TestTags.Login.register_button)
    }

    fun textFieldEnterValue(textFieldId : TextFieldId, value : String) = apply {
        device.findObject(By.res( textFieldId.value))
            .setText(value)
    }

    fun clickTextFieldShowPassword(textFieldId : TextFieldId) = apply {
        device.findObject(By.res( TestTags.General.textFieldHidePassword(textFieldId.value)))
            .click()
    }

    fun assertTextFieldValue(textFieldId : TextFieldId, value : String) = apply {
        val textField = device.findObject(By.res( textFieldId.value))
        Assert.assertThat(textField.getText(), CoreMatchers.`is`(CoreMatchers.equalTo(value)))
    }
    fun assertTextFieldErrorText(textFieldId : TextFieldId, value : String) = apply {
        val errorTextTag = TestTags.General.textFieldError(textFieldId.value)
        device.wait(Until.findObject(By.res( errorTextTag)), 3000L)
        val errorText = device.findObject(By.res(errorTextTag))
        Assert.assertThat(errorText.getText(), CoreMatchers.`is`(CoreMatchers.equalTo(value)))
    }

    fun clickButton(buttonId : ButtonId) = apply {
        device.findObject(By.res( buttonId.value))
            .click()
    }

    fun loginClickAndTransition() : HomeRobot {
        device.findObject(By.res( ButtonId.loginButton.value))
            .clickAndWait(Until.newWindow(), 5000L)

        return HomeRobot(device)
    }

    fun loginUser(userName : String, password : String) : HomeRobot {
        textFieldEnterValue(TextFieldId.userName, userName)
        textFieldEnterValue(TextFieldId.password, password)
        return loginClickAndTransition()
    }

    fun assertErrorDialog(title : String, message: String) = apply {
        device.wait(Until.findObject(By.res( TestTags.General.error_dialog_title)), 3000L)
        val errorDialogTitle = device.findObject(By.res( TestTags.General.error_dialog_title))
        Assert.assertThat(errorDialogTitle.getText(), CoreMatchers.`is`(CoreMatchers.equalTo(title)))

        val errorDialogMessage = device.findObject(By.res( TestTags.General.error_dialog_message))
        Assert.assertThat(errorDialogMessage.getText(), CoreMatchers.`is`(CoreMatchers.equalTo(message)))
    }

    fun waitUnitLoadingFinished() = apply {
        device.wait(Until.gone(By.res( TestTags.General.loading_indicator)), 3000L)
    }
}