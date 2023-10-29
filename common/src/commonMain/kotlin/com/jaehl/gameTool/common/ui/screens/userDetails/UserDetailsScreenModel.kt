package com.jaehl.gameTool.common.ui.screens.userDetails

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.screens.launchIo

class UserDetailsScreenModel(
    private val jobDispatcher: JobDispatcher,
    private val tokenProvider: TokenProvider,
    private val userRepo: UserRepo
) : ScreenModel {
    val viewModel = mutableStateOf(ViewModel())
    val logoutEvent = mutableStateOf(false)
    var userId = -1

    val dialogConfig = mutableStateOf<DialogConfig>(DialogConfig.Closed)

    private fun onException(t : Throwable) {
        System.err.println(t.message)
    }

    fun setup(userId : Int?) = launchIo(jobDispatcher, ::onException) {
        dataRefresh(userId)
    }

    private suspend fun dataRefresh(userId : Int?) {
        val userSelf = userRepo.getUserSelf()

        val user = if(userId == null){
            userSelf
        } else {
            userRepo.getUser(userId)
        }

        var viewModel = ViewModel(
            userModel = user.toUserViewModel(
                showPasswordChange= (userSelf == user)
            ),
            showLogout = (userSelf == user),
            showAdminTools = listOf(
                User.Role.Admin
            ).contains(userSelf.role)
        )

        this.userId = user.id
        this.viewModel.value =  viewModel
    }

    fun logoutClick() = launchIo(jobDispatcher, ::onException) {
        tokenProvider.clearTokens()
        logoutEvent.value = true
    }

    fun closeDialog() {
        dialogConfig.value = DialogConfig.Closed
    }

    fun onChangeUserRoleClick(){
        dialogConfig.value = DialogConfig.RolePickerConfig(userId)
    }

    fun changeUserRole(userId : Int, role : User.Role) = launchIo(jobDispatcher, ::onException) {
        userRepo.changeUserRole(userId = userId, role = role)
        dataRefresh(userId)
    }

    fun onChangePasswordClick() {
        dialogConfig.value = DialogConfig.ChangePasswordDialog()
    }

    fun onDialogPasswordChange(value : String){
        var changePasswordDialog = dialogConfig.value
        if(changePasswordDialog !is DialogConfig.ChangePasswordDialog) return
        changePasswordDialog = changePasswordDialog.copy(
            password = TextFieldValue(value = value)
        )
        dialogConfig.value = changePasswordDialog
    }

    fun onDialogReEnterPasswordChange(value : String){
        var changePasswordDialog = dialogConfig.value
        if(changePasswordDialog !is DialogConfig.ChangePasswordDialog) return
        changePasswordDialog = changePasswordDialog.copy(
            reEnterPassword = TextFieldValue(value = value)
        )
        dialogConfig.value = changePasswordDialog
    }

    fun onDialogPasswordChangeClick(){
        var changePasswordDialog = dialogConfig.value
        if(changePasswordDialog !is DialogConfig.ChangePasswordDialog) return

        if(changePasswordDialog.password.value != changePasswordDialog.reEnterPassword.value){
            changePasswordDialog = changePasswordDialog.copy(
                reEnterPassword = changePasswordDialog.reEnterPassword.copy(
                    error = "Passwords do not Match"
                )
            )
            dialogConfig.value = changePasswordDialog
            return
        }
        launchIo(jobDispatcher, ::onException){
            userRepo.changePassword(userId, changePasswordDialog.password.value)
            dialogConfig.value = DialogConfig.Closed
        }
    }

    fun User.toUserViewModel(showPasswordChange : Boolean) : UserViewModel{
        return UserViewModel(
            userName = this.userName,
            email = this.email,
            role = this.role.name,
            showPasswordChange = showPasswordChange
        )
    }

    sealed class DialogConfig {
        data object Closed : DialogConfig()
        data class ErrorDialog(
            val title : String,
            val message : String
        ) : DialogConfig()
        data class RolePickerConfig(val userId : Int) : DialogConfig()
        data class ChangePasswordDialog(
            val password : TextFieldValue = TextFieldValue(),
            val reEnterPassword : TextFieldValue = TextFieldValue(),
        ) : DialogConfig()
    }

    data class UserViewModel(
        val userName : String = "",
        val email : String = "",
        val role : String = "",
        val showPasswordChange : Boolean = false
    )

    data class ViewModel(
        val userModel : UserViewModel = UserViewModel(),
        val showLogout : Boolean = false,
        val showAdminTools : Boolean = false
    )
}