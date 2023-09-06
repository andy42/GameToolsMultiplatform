package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.model.baseBody
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.Image
import com.jaehl.gameTool.common.data.service.ImageService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ImageServiceRetroFit(
    val serverApi : ServerApi,
    val authProvider: AuthProvider
) : ImageService {
    override fun addImage(imageFile: File, description : String): Image {

        val requestFile: RequestBody = imageFile
            .asRequestBody("image/png".toMediaType())

        val multipartImage =
            MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile)

        val map: MutableMap<String, RequestBody> = mutableMapOf()
        map["description"] = description.toRequestBody("text/plain".toMediaType())

        return serverApi.addImage(
            bearerToken = authProvider.getBearerToken(),
            image = multipartImage,
            partMap = map
        ).baseBody()
    }
}