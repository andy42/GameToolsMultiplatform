package com.jaehl.gameTool.common.ui

object TestTags {
    object General {

        const val nav_title = "navTitle"
        const val nav_back_button = "navBackButton"

        const val error_dialog = "errorDialog"
        const val error_dialog_title = "errorDialogTitle"
        const val error_dialog_message = "errorDialogMessage"

        const val loading_indicator = "loadingIndicator"

        const val error_suffix = "_error"
        fun textFieldError(textFieldId : String) : String {
            return textFieldId + error_suffix
        }
        const val hide_password_suffix = "_hidePassword"
        fun textFieldHidePassword(textFieldId : String) : String {
            return textFieldId + hide_password_suffix
        }
    }

    object Login {
        const val login_tab = "loginTab"
        const val register_tab = "registerTab"
        const val user_name = "userName"
        const val email = "email"
        const val password = "password"
        const val re_enter_password = "reEnterPassword"
        const val login_button = "loginButton"
        const val register_button = "registerButton"
    }

    object Home {
        const val game_row = "gameRow"
        const val nav_account_details = "navAccountDetails"
    }

    object GameDetails {
        const val items_button = "itemsButton"
        const val collections_button = "collectionsButton"
    }

    object ItemList {
        const val item_row = "itemRow"
    }
}

