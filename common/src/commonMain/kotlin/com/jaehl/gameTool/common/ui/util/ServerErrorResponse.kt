package com.jaehl.gameTool.common.ui.util

import kotlinx.serialization.Serializable

@Serializable
data class ServerErrorResponse(
    val code : Int,
    val message : String
)
