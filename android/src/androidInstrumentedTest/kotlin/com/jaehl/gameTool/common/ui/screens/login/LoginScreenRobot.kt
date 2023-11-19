package com.jaehl.gameTool.common.ui.screens.login

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.TestTags
import com.jaehl.gameTool.common.ui.screens.home.HomeRobot
import org.hamcrest.CoreMatchers
import org.junit.Assert

class LoginScreenRobot(
    private val device: UiDevice,
    private val timeout : Long
) {

    enum class TextFieldId(val value : String) {
        UserName(TestTags.Login.user_name),
        Email(TestTags.Login.email),
        Password(TestTags.Login.password),
        ReEnterPassword(TestTags.Login.re_enter_password)
    }

    enum class ButtonId(val value : String) {
        LoginButton(TestTags.Login.login_button),
        RegisterButton(TestTags.Login.register_button)
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
        device.wait(Until.findObject(By.res( errorTextTag)), timeout)
        val errorText = device.findObject(By.res(errorTextTag))
        Assert.assertThat(errorText.getText(), CoreMatchers.`is`(CoreMatchers.equalTo(value)))
    }

    fun clickButton(buttonId : ButtonId) = apply {
        device.findObject(By.res( buttonId.value))
            .click()
    }

    fun loginClickAndTransition() : HomeRobot {
        device.findObject(By.res( ButtonId.LoginButton.value))
            .clickAndWait(Until.newWindow(), timeout)

        return HomeRobot(device, timeout)
    }

    fun loginUser(userName : String, password : String) : HomeRobot {
        textFieldEnterValue(TextFieldId.UserName, userName)
        textFieldEnterValue(TextFieldId.Password, password)
        return loginClickAndTransition()
    }

    fun assertErrorDialog(title : String, message: String) = apply {
        device.wait(Until.findObject(By.res( TestTags.General.error_dialog_title)), timeout)
        val errorDialogTitle = device.findObject(By.res( TestTags.General.error_dialog_title))
        Assert.assertThat(errorDialogTitle.getText(), CoreMatchers.`is`(CoreMatchers.equalTo(title)))

        val errorDialogMessage = device.findObject(By.res( TestTags.General.error_dialog_message))
        Assert.assertThat(errorDialogMessage.getText(), CoreMatchers.`is`(CoreMatchers.equalTo(message)))
    }

    fun waitUnitLoadingFinished() = apply {
        device.wait(Until.gone(By.res( TestTags.General.loading_indicator)), timeout)
    }
}