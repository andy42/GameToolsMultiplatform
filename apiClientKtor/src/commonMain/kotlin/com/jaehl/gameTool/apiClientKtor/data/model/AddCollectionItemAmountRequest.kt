package com.jaehl.gameTool.apiClientKtor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddCollectionItemAmountRequest(
    val amount : Int
)
