package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.common.data.model.Image
import com.jaehl.gameTool.common.data.model.ImageMetaData
import com.jaehl.gameTool.common.data.model.ImageType
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.ImageService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File

class ImageServiceRetroFit(
    val serverApi : ServerApi,
    val tokenProvider : TokenProvider
) : ImageService {
    override suspend fun addImage(imageFile: File, imageType : ImageType, description : String): ImageMetaData {

        val requestFile: RequestBody = imageFile
            .asRequestBody("image/png".toMediaType())

        val multipartImage =
            MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile)

        val map: MutableMap<String, RequestBody> = mutableMapOf()
        map["description"] = description.toRequestBody("text/plain".toMediaType())
        map["imageType"] = imageType.value.toString().toRequestBody("text/plain".toMediaType())

        return serverApi.addImage(
            bearerToken = tokenProvider.getBearerAccessToken(),
            image = multipartImage,
            partMap = map
        ).data
    }

    override suspend fun getImages(): List<ImageMetaData> {
        return serverApi.getImages(
            bearerToken = tokenProvider.getBearerAccessToken(),
        ).data
    }

    override suspend fun getImage(id: Int): ByteArray {
        val image : ResponseBody? = serverApi.getImage(
            bearerToken = tokenProvider.getBearerRefreshToken(),
            id = id
        )

        return image?.bytes() ?: throw Exception("Image error")
    }
}