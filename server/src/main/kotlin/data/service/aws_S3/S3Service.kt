package com.carspotter.data.service.aws_S3

import org.gradle.internal.impldep.com.amazonaws.HttpMethod
import org.gradle.internal.impldep.com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import org.gradle.internal.impldep.com.amazonaws.services.s3.AmazonS3
import org.gradle.internal.impldep.com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.gradle.internal.impldep.com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import java.net.URL
import java.util.Date

class S3Service(
    private val bucketName: String,
    private val region: String,
): IStorageService {
    private val s3Client: AmazonS3 = AmazonS3ClientBuilder.standard()
        .withRegion(region)
        .withCredentials(DefaultAWSCredentialsProviderChain())
        .build()

    override fun getPresignedUploadUrl(imageName: String): URL {
        val expiration = Date(System.currentTimeMillis() + 15 * 60 * 1000) // 15 minutes
        val request = GeneratePresignedUrlRequest(bucketName, imageName)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration)
        return s3Client.generatePresignedUrl(request)
    }

    override fun getPublicImageUrl(imageName: String): String {
        return "https://$bucketName.s3.$region.amazonaws.com/$imageName"
    }

}