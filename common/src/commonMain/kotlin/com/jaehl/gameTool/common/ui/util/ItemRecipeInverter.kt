package com.jaehl.gameTool.common.ui.util

import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import java.lang.ref.WeakReference

class ItemRecipeInverter {

    private fun invert(new : ItemRecipeNode, ref :ItemRecipeNode){
        val parentItem = ref.parentNode.get() ?: return

        var first = new.inputs.firstOrNull{ it.itemAmount.itemModel.id == parentItem.itemAmount.itemModel.id}
        if(first == null){
            val newChild = parentItem.copy()
            newChild.inputs = arrayListOf()
            newChild.parentNode = WeakReference(new)
            new.inputs.add(newChild)
            invert(newChild, parentItem)
        } else {
            first = first.copy(
                itemAmount = first.itemAmount.copy(
                    amount = first.itemAmount.amount + parentItem.itemAmount.amount
                )
            )
            invert(first, parentItem)
        }
    }

    private fun walk(map : HashMap<Int,ItemRecipeNode>, itemRecipeNodes : List<ItemRecipeNode>){
        itemRecipeNodes.forEach {
            if(it.inputs.isEmpty()){
                if(map.containsKey(it.itemAmount.itemModel.id)){
                    val t = map[it.itemAmount.itemModel.id]!!
                    val newT = t.copy(
                        itemAmount = t.itemAmount.copy(
                            amount = t.itemAmount.amount + it.itemAmount.amount
                        )
                    )
                    map[it.itemAmount.itemModel.id] = newT
                    invert(newT, it)
                } else {
                    val new = it.copy()
                    new.parentNode = WeakReference(null)
                    new.inputs = arrayListOf()
                    map[it.itemAmount.itemModel.id] = new
                    invert(new, it)
                }
            }
            else {
                walk(map, it.inputs)
            }
        }
    }

    fun invertItemRecipes (itemRecipeNodes : List<ItemRecipeNode>) : List<ItemRecipeNode>{
        var baseMap = hashMapOf<Int, ItemRecipeNode>()
        walk(baseMap, itemRecipeNodes)
        return baseMap.values.toList()
    }
}