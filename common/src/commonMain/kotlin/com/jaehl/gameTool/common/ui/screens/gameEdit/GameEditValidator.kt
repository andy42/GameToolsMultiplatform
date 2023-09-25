package com.jaehl.gameTool.common.ui.screens.gameEdit

class GameEditValidator(

) {
    var validatorListener : ValidatorListener? = null

    fun validate(name : String) : Boolean{
        if(name.isEmpty()){
            validatorListener?.onNameError("you most enter a name")
            return false
        }
        return true
    }

    interface ValidatorListener {
        fun onNameError(error : String)
    }
}