package com.jaehl.gameTool.common.data.local

import com.jaehl.gameTool.common.data.model.Item

interface ItemLocalDataSource {
    fun getItems(gameId : Int) : List<Item>
    fun getItem(itemId : Int) : Item

    fun update(items : List<Item>)
    fun update(item : Item)
}