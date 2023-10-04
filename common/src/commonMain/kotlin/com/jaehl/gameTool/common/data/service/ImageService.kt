package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.Image
import com.jaehl.gameTool.common.data.model.ImageMetaData
import com.jaehl.gameTool.common.data.model.ImageType
import java.io.File

interface ImageService {
    suspend fun addImage(imageFile : File, imageType : ImageType, description : String) : Image
    suspend fun getImages() : List<ImageMetaData>
    suspend fun getImage(id : Int) : ByteArray
}