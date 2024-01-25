package com.jaehl.gameTool.common.ui.util

import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import java.lang.ref.WeakReference

class ItemRecipeFlattener {
    private fun walk(map : HashMap<Int, ItemRecipeNode>, itemRecipeNodes : List<ItemRecipeNode>){
        itemRecipeNodes.forEach {
            it.itemAmount.amount

            val itemRecipeNode = map[it.itemAmount.itemModel.id]
            if(itemRecipeNode == null){
                map[it.itemAmount.itemModel.id] = it.copy(
                    inputs = arrayListOf(),
                    byProducts = arrayListOf(),
                    parentNode = WeakReference(null)
                )
            } else {
                map[it.itemAmount.itemModel.id] = itemRecipeNode.copy(
                    itemAmount = itemRecipeNode.itemAmount.copy(
                        amount = itemRecipeNode.itemAmount.amount + it.itemAmount.amount
                    ),
                )
            }
            walk(map, it.inputs)
        }
    }

    fun flattenItemRecipes (itemRecipeNodes : List<ItemRecipeNode>) : List<ItemRecipeNode>{
        val itemMap = hashMapOf<Int, ItemRecipeNode>()
        walk(itemMap, itemRecipeNodes.map { it.inputs }.flatten() )
        return itemMap.values.toList()
    }
}