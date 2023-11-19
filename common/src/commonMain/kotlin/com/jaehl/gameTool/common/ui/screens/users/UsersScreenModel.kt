package com.jaehl.gameTool.common.ui.screens.users

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.extensions.postSwap

class UsersScreenModel(
    private val jobDispatcher : JobDispatcher,
    private val userRepo: UserRepo
) : ScreenModel {
    var users = mutableStateListOf<UserModel>()
    var pageLoading = mutableStateOf(false)
    val dialogConfig = mutableStateOf<DialogConfig>(DialogConfig.ClosedDialog)

    fun setup() {
        launchIo(
            jobDispatcher,
            onException = ::onException
        ){
            userRepo.getUsersFlow().collect{ usersResource ->
                this.pageLoading.value = usersResource is Resource.Loading
                if(usersResource is Resource.Error){
                    this.onException(usersResource.exception)
                    return@collect
                }

                this.users.postSwap(usersResource.getDataOrThrow().map {
                    UserModel(
                        id = it.id,
                        name = it.userName,
                        email = it.email,
                        role = it.role.name
                    )
                })

            }
        }
    }

    fun closeDialog(){
        dialogConfig.value = DialogConfig.ClosedDialog
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
        pageLoading.value = false
    }

    sealed class DialogConfig {
        data object ClosedDialog : DialogConfig()
        data class ErrorDialog(
            val title : String,
            val message : String
        ) : DialogConfig()
    }
}

