package com.jaehl.gameTool.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.screens.home.HomeRobot
import com.jaehl.gameTool.common.ui.screens.login.LoginScreenRobot
import org.hamcrest.CoreMatchers
import org.junit.Assert

class DeviceRobot {
    private val device : UiDevice
    private val BASIC_SAMPLE_PACKAGE = "com.jaehl.gameTool.GameTool"
    private val LAUNCH_TIMEOUT = 5000L

    init {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    fun setup() = apply {
        device.pressHome()

        val launcherPackage: String = getLauncherPackageName()
        Assert.assertThat(launcherPackage, CoreMatchers.notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT)

        // Launch the blueprint app
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = context.packageManager
            .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear out any previous instances
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT)
    }

    private fun getLauncherPackageName(): String {
        // Create launcher Intent
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        // Use PackageManager to get the launcher package name
        val pm = ApplicationProvider.getApplicationContext<Context>().packageManager
        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo!!.activityInfo.packageName
    }

    fun logoutIfNeeded() : LoginScreenRobot {
        val homeRobot = asHomeScreen()
        if(homeRobot.isHomePage()){
            return homeRobot
                .clickNavAccountDetailsAndTransition()
                .clickLogoutAndTransition()
        }
        else {
            return asLoginScreen()
        }
    }

    fun asHomeScreen() : HomeRobot {
        return HomeRobot(device)
    }

    fun asLoginScreen() : LoginScreenRobot {
        return LoginScreenRobot(device)
    }
}