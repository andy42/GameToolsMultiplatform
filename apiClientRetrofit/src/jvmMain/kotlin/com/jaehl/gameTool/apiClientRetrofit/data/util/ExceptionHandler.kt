package com.jaehl.gameTool.apiClientRetrofit.data.util

import com.jaehl.gameTool.common.ui.util.ServerErrorResponse
import com.jaehl.gameTool.common.ui.util.UiException
import retrofit2.HttpException
import kotlinx.serialization.json.Json
import java.net.ConnectException

class ExceptionHandler {

    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = " "
    }

    private fun parseErrorResponse(response : String) : ServerErrorResponse {

        try {
            return json.decodeFromString<ServerErrorResponse>(response)
        }
        catch (t : Throwable) {
            System.err.println(t.message)
            return ServerErrorResponse(
                code = 500,
                message = "server error, invalid error response"
            )
        }
    }

    private fun toUiException(e : HttpException, serverErrorResponse : ServerErrorResponse) : UiException {
        return when (serverErrorResponse.code) {
            401 -> UiException.UnauthorizedError(cause = e)
            403 -> UiException.ForbiddenError(cause = e)
            else -> UiException.ServerError(cause = e)
        }
    }
    fun parse(e: Throwable) : UiException {
        if(e is HttpException) {
            return toUiException(
                e,
                parseErrorResponse(e.response()?.errorBody()?.string() ?: "")
            )
        }
        else if (e is ConnectException){
            return UiException.ServerConnectionError(cause = e)
        }
        else {
            return UiException.GeneralError(cause = e)
        }
    }

    suspend fun <T>tryBlock(block : suspend () -> T) : T{
        try {
            return block()
        }
        catch (t : Throwable) {
            throw parse(t)
        }
    }
}