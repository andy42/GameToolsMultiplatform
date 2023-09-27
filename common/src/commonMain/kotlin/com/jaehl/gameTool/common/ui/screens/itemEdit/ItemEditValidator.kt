package com.jaehl.gameTool.common.ui.screens.itemEdit

import com.jaehl.gameTool.common.data.model.ImageType
import com.jaehl.gameTool.common.ui.componets.ImageResource

class ItemEditValidator {

    var listener : Listener? = null

    fun validate(
        name : String,
        image : ImageResource
    ) : Boolean{

        var valid = validateName(name)
        valid = validateImage(image) && valid
        return valid
    }

    fun validateName(name : String) : Boolean {
        if(name.isEmpty()){
            listener?.onNameError("you must enter a name")
            return false
        }
        return true
    }

    fun validateImage(image : ImageResource) : Boolean {
        if(image is ImageResource.ImageLocalResource) {
            val imageType = ImageType.fromFileExtension( image.getFileExtension() )
            if(image.url.isEmpty()){
                listener?.onImageError("you must add an image")
                return false
            }
            else if(imageType == ImageType.NotSupported){
                listener?.onImageError("file most be a png/webp/jpg")
                return false
            }
        }
        return true
    }

    interface Listener {
        fun onNameError(error : String)
        fun onImageError(error : String)
    }
}