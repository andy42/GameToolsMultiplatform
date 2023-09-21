package com.jaehl.gameTool.common.data.model.request

import com.jaehl.gameTool.common.data.model.Collection

data class UpdateCollectionRequest(
    val name : String,
    val groups : List<GroupUpdate>?
) {
    data class GroupUpdate(
        val id : Int?,
        val name : String,
        val itemAmounts : List<Collection.ItemAmount>
    )
}
