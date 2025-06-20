package com.carspotter.data.service.auth_credential

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory

class GoogleTokenVerifierImpl : GoogleTokenVerifier {
    override fun verifyAndExtractSub(googleIdToken: String): String? {
        val clientId = System.getenv("GOOGLE_CLIENT_ID")
        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(listOf(System.getenv("GOOGLE_CLIENT_ID")))
            .build()
        val idToken: GoogleIdToken? = verifier.verify(googleIdToken)
        return idToken?.payload?.subject
    }
}