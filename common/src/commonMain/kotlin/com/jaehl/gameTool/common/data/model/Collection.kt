package com.jaehl.gameTool.common.data.model


data class Collection(
    val id : Int,
    val userId : Int,
    val gameId : Int,
    val name : String,
    val groups : List<Group>
) {

    data class Group(
        val id : Int,
        val name : String,
        val itemAmounts : List<ItemAmount>
    )

    data class ItemAmount(
        val itemId : Int,
        val amount : Int
    )
}
