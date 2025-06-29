package com.carspotter.data.service.aws_S3

import java.net.URL

interface IStorageService {
    fun getPresignedUploadUrl(imageName: String): URL
    fun getPublicImageUrl(imageName: String): String
}