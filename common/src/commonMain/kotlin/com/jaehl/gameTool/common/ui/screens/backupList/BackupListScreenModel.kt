package com.jaehl.gameTool.common.ui.screens.backupList

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.Backup
import com.jaehl.gameTool.common.data.repo.BackupRepo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.screens.launchIo
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class BackupListScreenModel(
    private val jobDispatcher: JobDispatcher,
    private val backupRepo: BackupRepo
) : ScreenModel {

    val backupList = mutableStateListOf<BackupViewModel>()
    val dialogData = mutableStateOf<DialogData>(DialogData.ClosedDialog)

    private fun dateToName(date : LocalDateTime) : String{
        return nameDateFormatter.format(date)
    }

    fun Backup.toBackupViewModel() : BackupViewModel {
        val localDateTime = LocalDateTime.parse(this.date, dateFormatter)
        return BackupViewModel(
            id = this.id,
            date = localDateTime.toEpochSecond(ZoneOffset.UTC),
            name = dateToName(localDateTime)
        )
    }

    fun setup(){
        launchIo(jobDispatcher, ::onException){
            backupRepo.getBackups().collect { backups ->
                backupList.postSwap(backups.map {
                    it.toBackupViewModel()
                })
            }
        }
    }

    fun createBackup() = launchIo(jobDispatcher, ::onException) {
        backupRepo.createBackups()
        backupRepo.getBackups().collect { backups ->
            backupList.postSwap(backups.map {
                it.toBackupViewModel()
            })
        }
    }

    fun applyBackup(backupId : String) = launchIo(jobDispatcher, ::onException) {
        backupRepo.applyBackup(backupId)
    }

    fun openDialog(dialogData : DialogData) {
        this.dialogData.value = dialogData
    }

    fun closeDialog(){
        this.dialogData.value = DialogData.ClosedDialog
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
    }

    data class BackupViewModel(
        val id : String,
        val date : Long,
        val name : String
    )

    sealed class DialogData {
        data class ApplyDialog(val backupId : String) : DialogData()
        data object ClosedDialog : DialogData()
    }

    companion object {
        private val nameDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    }
}