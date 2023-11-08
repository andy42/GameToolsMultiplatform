package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.common.data.model.Backup
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.BackupService

class BackupServiceRetroFit(
    private val serverApi : ServerApi,
    private val tokenProvider : TokenProvider
) : BackupService {
    override suspend fun getBackups(): List<Backup> {
        return serverApi.getBackups(
            bearerToken = tokenProvider.getBearerAccessToken()
        ).data
    }

    override suspend fun createBackups(): Backup {
        return serverApi.createBackup(
            bearerToken = tokenProvider.getBearerAccessToken()
        ).data
    }

    override suspend fun applyBackup(backupId: String) {
        return serverApi.applyBackup(
            bearerToken = tokenProvider.getBearerAccessToken(),
            backupId = backupId
        )
    }
}