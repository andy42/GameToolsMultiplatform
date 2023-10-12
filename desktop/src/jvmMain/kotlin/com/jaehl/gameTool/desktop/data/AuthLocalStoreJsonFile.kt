package com.jaehl.gameTool.desktop.data

import com.jaehl.gameTool.common.data.AuthLocalStore
import com.jaehl.gameTool.common.data.model.UserTokens
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class AuthLocalStoreJsonFile(
    private val localFiles : LocalFiles,
    private val localFileSettings : LocalFileSettings
) : AuthLocalStore {

    private var userTokens : UserTokens? = null

    init {
        userTokens = loadUserTokens()
    }

    private fun getAuthFile() : File {
        return localFiles.getFile(localFileSettings.userHomeDirectory, "auth.json")
    }
    private fun loadUserTokens() : UserTokens? {
        val file = getAuthFile()
        if(file.exists()) {
            return Json.decodeFromString(file.readText())
        }
        else return null
    }

    override suspend fun getUserTokens(): UserTokens {
        return userTokens ?: UserTokens("", "")
    }

    override suspend fun saveToken(userTokens: UserTokens) {
        val prettyJson = Json { // this returns the JsonBuilder
            prettyPrint = true
            prettyPrintIndent = " "
        }
        getAuthFile().writeBytes(
            prettyJson.encodeToString(userTokens).toByteArray()
        )
        this.userTokens = userTokens
    }
}