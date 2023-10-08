package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.model.Backup
import com.jaehl.gameTool.common.data.service.BackupService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface BackupRepo {
    suspend fun getBackups() : Flow<List<Backup>>
    suspend fun createBackups() : Backup
    suspend fun applyBackup(backupId : String)
}

class BackupRepoImp(
    private val backupService: BackupService
) : BackupRepo {

    private val backups = arrayListOf<Backup>()

    override suspend fun getBackups(): Flow<List<Backup>>  = flow {
        emit(backups)
        emit(backupService.getBackups())
    }

    override suspend fun createBackups(): Backup {
        val backup = backupService.createBackups()
        backups.add(backup)
        return backup
    }

    override suspend fun applyBackup(backupId: String) {
        backupService.applyBackup(backupId)
    }
}