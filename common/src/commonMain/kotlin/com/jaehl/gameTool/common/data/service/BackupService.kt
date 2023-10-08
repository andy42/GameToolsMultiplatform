package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.Backup

interface BackupService {
    suspend fun getBackups() : List<Backup>
    suspend fun createBackups() : Backup
    suspend fun applyBackup(backupId : String)
}