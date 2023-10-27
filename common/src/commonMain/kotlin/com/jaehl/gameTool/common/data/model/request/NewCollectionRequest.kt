package com.jaehl.gameTool.common.data.model.request

import com.jaehl.gameTool.common.data.model.Collection
import kotlinx.serialization.Serializable

@Serializable
data class NewCollectionRequest(
    val gameId : Int,
    val name : String,
    val groups : List<NewGroup>
) {
    @Serializable
    data class NewGroup(
        val name : String,
        val itemAmounts : List<Collection.ItemAmount>
    )
}
