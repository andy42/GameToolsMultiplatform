package com.jaehl.gameTool.apiClientKtor.data.service

import com.jaehl.gameTool.common.data.model.Backup
import com.jaehl.gameTool.common.data.service.BackupService
import io.ktor.http.*

class BackupServiceKtor(
    private val requestUtil : RequestUtil
) : BackupService {

    override suspend fun getBackups(): List<Backup> {
        return requestUtil.createRequest(
            url = "admin/backups",
            HttpMethod.Get
        )
    }

    override suspend fun createBackups(): Backup {
        return requestUtil.createRequest(
            url = "admin/backups/create",
            HttpMethod.Post
        )
    }

    override suspend fun applyBackup(backupId: String) {
        return requestUtil.createRequest(
            url = "admin/backups/apply/$backupId",
            HttpMethod.Post
        )
    }
}