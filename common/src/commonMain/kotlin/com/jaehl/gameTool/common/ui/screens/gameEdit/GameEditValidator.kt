package com.jaehl.gameTool.common.ui.screens.gameEdit

import com.jaehl.gameTool.common.data.model.ImageType
import com.jaehl.gameTool.common.ui.componets.ImageResource

class GameEditValidator(

) {
    var validatorListener : ValidatorListener? = null

    fun validate(name : String, iconImage : ImageResource, bannerImage : ImageResource) : Boolean{
        var valid = validateName(name)
        valid = validateIconImage(iconImage) && valid
        valid = validateBannerImage(bannerImage) && valid
        return valid
    }

    private fun validateName(name : String) : Boolean {
        if(name.isEmpty()){
            validatorListener?.onNameError("you most enter a name")
            return false
        }
        return true
    }

    private fun validateIconImage(iconImage : ImageResource) : Boolean {
        if(iconImage is ImageResource.ImageLocalResource) {
            val imageType = ImageType.fromFileExtension( iconImage.getFileExtension() )
            if(iconImage.url.isEmpty()){
                validatorListener?.onIconError("you must add an image")
                return false
            }
            else if(imageType == ImageType.NotSupported){
                validatorListener?.onIconError("file most be a png/webp/jpg")
                return false
            }
        }
        return true
    }

    private fun validateBannerImage(bannerImage : ImageResource) : Boolean {
        if(bannerImage is ImageResource.ImageLocalResource) {
            val imageType = ImageType.fromFileExtension( bannerImage.getFileExtension() )
            if(bannerImage.url.isEmpty()){
                validatorListener?.onBannerError("you must add an image")
                return false
            }
            else if(imageType == ImageType.NotSupported){
                validatorListener?.onBannerError("file most be a png/webp/jpg")
                return false
            }
        }
        return true
    }

    interface ValidatorListener {
        fun onNameError(error : String)
        fun onIconError(error : String)
        fun onBannerError(error : String)
    }
}