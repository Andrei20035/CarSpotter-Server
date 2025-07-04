package com.carspotter.data.service.aws_S3

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URL
import java.time.Duration

class S3Service(
    private val bucketName: String,
    private val region: String,
): IStorageService {
    private val presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build()

    override fun getPresignedUploadUrl(imageName: String): URL {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(imageName)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .putObjectRequest(putObjectRequest)
            .build()

        return presigner.presignPutObject(presignRequest).url()
    }

    override fun getPublicImageUrl(imageName: String): String {
        return "https://$bucketName.s3.$region.amazonaws.com/$imageName"
    }
}