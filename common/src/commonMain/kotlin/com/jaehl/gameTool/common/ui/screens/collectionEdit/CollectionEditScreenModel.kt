package com.jaehl.gameTool.common.ui.screens.collectionEdit

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.model.request.UpdateCollectionRequest
import com.jaehl.gameTool.common.data.repo.CollectionRepo
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.extensions.swap
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.UiExceptionHandler
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.screens.launchWithCatch
import com.jaehl.gameTool.common.ui.screens.runWithCatch
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import com.jaehl.gameTool.common.ui.viewModel.ItemModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

class CollectionEditScreenModel (
    private val jobDispatcher: JobDispatcher,
    private val collectionRepo: CollectionRepo,
    private val itemRepo : ItemRepo,
    private val gameRepo: GameRepo,
    private val appConfig : AppConfig,
    private val tokenProvider: TokenProvider,
    private val uiExceptionHandler : UiExceptionHandler
) : ScreenModel {

    private lateinit var config : Config

    val title = mutableStateOf("")
    val collectionName = mutableStateOf(TextFieldValue())
    val groupList = mutableStateListOf<GroupViewModel>()
    val itemModels = mutableStateListOf<ItemModel>()

    //val showExitSaveDialog = mutableStateOf(false)
    val closePageEvent = mutableStateOf(false)

    val dialogConfig = mutableStateOf<DialogConfig>(DialogConfig.Closed)

    var pageLoading = mutableStateOf(false)

    private var itemCategories = mutableStateListOf<ItemCategory>()

    private var groupAddIndex = 0
    private var groupMap = LinkedHashMap<Int, GroupViewModel>()
    private var unsavedChanges = false

    private suspend fun updateUi(collectionResource : Resource<Collection>?, itemsResource : Resource<List<Item>>, itemCategoryResource : Resource<List<ItemCategory>>){
        pageLoading.value = (collectionResource is Resource.Loading
                || itemsResource is Resource.Loading
                || itemCategoryResource is Resource.Loading)

        //if the Resource are empty might not have loaded wait for server response
        if(itemsResource is Resource.Loading && itemsResource.data.isEmpty()) return
        if(itemCategoryResource is Resource.Loading && itemCategoryResource.data.isEmpty()) return

        listOf<Resource<*>?>(collectionResource, itemsResource, itemCategoryResource).forEach {
            if(it is Resource.Error){
                onException(it.exception)
                return
            }
        }

        itemModels.postSwap(
            itemsResource.getDataOrThrow().map { item ->
                item.toItemModel(appConfig, tokenProvider)
            }
        )

        val list = mutableListOf(ItemCategory.Item_Category_ALL)
        list.addAll(itemCategoryResource.getDataOrThrow())
        itemCategories.postSwap(list)

        collectionResource?.getDataOrThrow()?.let { collection ->
            groupAddIndex = 0
            groupMap.clear()

            collectionName.value = collectionName.value.copy(
                value = collection.name
            )

            val groups = collection.groups.map { group ->

                val itemAmountMap = linkedMapOf<Int, ItemAmountViewModel>()
                group.itemAmounts.forEach { itemAmount ->
                    itemAmountMap[itemAmount.itemId] = ItemAmountViewModel(
                        itemModel = itemRepo.getItemCached(
                            itemAmount.itemId)?.toItemModel(appConfig, tokenProvider) ?: throw Exception("Item Not Found : ${itemAmount.itemId}"
                        ),
                        amount = itemAmount.amount
                    )
                }

                GroupViewModel(
                    id = groupAddIndex++,
                    severId = group.id,
                    name = TextFieldValue(value = group.name),
                    itemList = itemAmountMap
                )
            }

            groups.forEach {
                groupMap[it.id] = it
            }
            groupList.postSwap(groupMap.values)
        }
    }

    fun setup(config : Config) {
        this.config = config

        unsavedChanges = false

        launchIo(
            jobDispatcher,
            onException = {}
        ) {
            if(config.collectionId != null) {
                combine(
                    collectionRepo.getCollectionFlow(config.collectionId),
                    itemRepo.getItems(config.gameId),
                    gameRepo.getGameItemCategories(config.gameId)
                ) { collectionResource, items, itemCategories ->
                    updateUi(collectionResource, items, itemCategories)
                }.collect()
            }
            else {
                combine(
                    itemRepo.getItems(config.gameId),
                    gameRepo.getGameItemCategories(config.gameId)
                ) { items, itemCategories ->
                    updateUi(null, items, itemCategories)
                }.collect()
            }
        }
    }

    private fun onException(t : Throwable) {
        System.err.println(t.message)
    }

    fun onCollectionTextChange(value : String) {
        collectionName.value = collectionName.value.copy(
            value = value
        )
        unsavedChanges = true
    }

    fun onAddNewGroupClick() {
        val newGroup = GroupViewModel(
            id = groupAddIndex++,
            severId = null,
            name = TextFieldValue(),
            itemList = linkedMapOf()
        )
        groupMap[newGroup.id] = newGroup
        groupList.swap(groupMap.values)
        unsavedChanges = true
    }

    fun onRemoveGroupClick(groupId : Int) {
        groupMap.remove(groupId)
        groupList.swap(groupMap.values)
        unsavedChanges = true
    }

    fun onGroupNameChange(groupId : Int, value : String) = runWithCatch(onException = ::onException) {
        val group = groupMap[groupId] ?: throw Exception("updateGroupName groupId not found : $groupId")
        groupMap[groupId] = group.copy(
            name = group.name.copy(
                value = value
            )
        )
        groupList.swap(groupMap.values)
        unsavedChanges = true
    }

    fun onItemAmountChange(groupId : Int, itemId : Int, value : String) = runWithCatch(onException = ::onException) {
        val group = groupMap[groupId] ?: throw Exception("onItemAmountChange groupId not found : $groupId")
        val item = group.itemList[itemId] ?: throw Exception("onItemAmountChange itemId not found : $itemId")
        val amount = if(value.isEmpty()) 0 else value.toIntOrNull() ?: throw Exception("onItemAmountChange value not Int : $value")

        groupMap[groupId] = group.copy(
            itemList = group.itemList.let { itemList ->
                itemList[itemId] = item.copy(
                    amount = amount
                )
                return@let itemList
            }
        )
        groupList.swap(groupMap.values)
        unsavedChanges = true
    }

    fun onRemoveItemClick(groupId : Int, itemId : Int) = runWithCatch(onException = ::onException) {
        val group = groupMap[groupId] ?: throw Exception("updateGroupName groupId not found : $groupId")
        group.itemList.remove(itemId)
        groupList.swap(groupMap.values)
        unsavedChanges = true
    }

    fun onAddItemClick(groupId : Int, itemId : Int) = launchWithCatch(onException = ::onException) {
        val group = groupMap[groupId] ?: throw Exception("onAddItemClick groupId not found : $groupId")
        val item = itemRepo.getItemCached(itemId) ?: throw Exception("onAddItemClick itemId not found : $itemId")
        group.itemList[itemId] = ItemAmountViewModel(
            itemModel = item.toItemModel(appConfig, tokenProvider),
            amount = 1
        )
        groupList.swap(groupMap.values)
        unsavedChanges = true
    }

    private suspend fun newCollection() {
        val body = NewCollectionRequest(
            gameId = config.gameId,
            name = collectionName.value.value,
            groups = groupList.map { groupViewModel ->
                NewCollectionRequest.NewGroup(
                    name = groupViewModel.name.value,
                    itemAmounts = groupViewModel.itemList.values.map {
                        Collection.ItemAmount(
                            itemId = it.itemModel.id,
                            amount = it.amount
                        )
                    }
                )
            }
        )

        collectionRepo.addCollection(
            data = body
        )
        unsavedChanges = false
    }

    private suspend fun updateCollection(){
        val body = UpdateCollectionRequest(
            name = collectionName.value.value,
            groups = groupList.map { groupViewModel ->
                UpdateCollectionRequest.GroupUpdate(
                    id = groupViewModel.severId,
                    name = groupViewModel.name.value,
                    itemAmounts = groupViewModel.itemList.values.map {
                        Collection.ItemAmount(
                            itemId = it.itemModel.id,
                            amount = it.amount
                        )
                    }
                )
            }
        )

        val collectionId = config.collectionId ?: throw Exception("save config.collectionId is null")
        collectionRepo.updateCollection(
            collectionId = collectionId,
            body = body
        )
        unsavedChanges = false
    }

    fun save(closeAfter : Boolean = false) = launchIo(
        jobDispatcher = jobDispatcher,
        onException = ::onException
    ) {
        pageLoading.value = true
        if(config.collectionId == null){
            newCollection()
        } else {
            updateCollection()
        }
        if(closeAfter){
            closePageEvent.value = true
        }
        pageLoading.value = false
    }

    fun onBackClick() {
        if(unsavedChanges){
            dialogConfig.value = DialogConfig.DialogSaveWarning
            //showExitSaveDialog.value = true
        } else {
            closePageEvent.value = true
        }
    }

    fun openDialogItemPicker(groupId : Int){
        dialogConfig.value = DialogConfig.DialogItemPicker(
            groupId = groupId,
            itemCategoryFilter = ItemCategory.Item_Category_ALL,
            itemCategories = itemCategories,
            searchText = "",
            addError = ""
        )
    }

    fun dialogItemPickerSearchTextChange(value : String) {
        val dialogItemPicker = dialogConfig.value
        if(dialogItemPicker !is DialogConfig.DialogItemPicker) return

        dialogConfig.value = dialogItemPicker.copy(
            searchText = value
        )
    }

    fun dialogItemPickerFilterCategoryPickerOpen() {
        val dialogItemPicker = dialogConfig.value
        if(dialogItemPicker !is DialogConfig.DialogItemPicker) return

        dialogConfig.value = dialogItemPicker.copy(
            showItemCategoryPicker = true
        )
    }

    fun dialogItemPickerFilterCategoryPickerClose() {
        val dialogItemPicker = dialogConfig.value
        if(dialogItemPicker !is DialogConfig.DialogItemPicker) return

        dialogConfig.value = dialogItemPicker.copy(
            showItemCategoryPicker = false
        )
    }

    fun dialogItemPickerFilterCategoryChange(value : ItemCategory) {
        val dialogItemPicker = dialogConfig.value
        if(dialogItemPicker !is DialogConfig.DialogItemPicker) return

        dialogConfig.value = dialogItemPicker.copy(
            itemCategoryFilter = value,
            showItemCategoryPicker = false
        )
    }

    fun closeDialog() {
        dialogConfig.value = DialogConfig.Closed
    }

    sealed class DialogConfig {
        data object Closed : DialogConfig()
        data class DialogItemPicker(
            val groupId : Int,
            val showItemCategoryPicker : Boolean = false,
            val itemCategoryFilter : ItemCategory,
            val itemCategories : List<ItemCategory>,
            val searchText : String = "",
            val addError : String = ""
        ) : DialogConfig()
        data object DialogSaveWarning : DialogConfig()
    }

    data class Config(
        val gameId : Int,
        val collectionId : Int?
    )

    data class GroupViewModel(
        val id : Int,
        val severId : Int?,
        val name : TextFieldValue,
        val itemList : LinkedHashMap<Int, ItemAmountViewModel>
    )
}