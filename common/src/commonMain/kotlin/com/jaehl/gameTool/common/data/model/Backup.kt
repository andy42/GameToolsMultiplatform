package com.jaehl.gameTool.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Backup(
    val id : String,
    val date : String,
    val version : Int
)
