package com.jaehl.gameTool.common.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class NewAdminCollectionRequest(
    val userId : Int,
    val gameId : Int,
    val name : String,
    val groups : List<NewCollectionRequest.NewGroup>
)
