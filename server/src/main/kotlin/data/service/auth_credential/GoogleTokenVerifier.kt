package com.carspotter.data.service.auth_credential

interface GoogleTokenVerifier {
    fun verifyAndExtractSub(googleIdToken: String): String?
}