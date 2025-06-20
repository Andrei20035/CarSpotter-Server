package com.carspotter.data.service.auth_credential

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory

class GoogleTokenVerifierImpl : GoogleTokenVerifier {
    override fun verifyAndExtractSub(googleIdToken: String): String? {
        val clientId = System.getenv("GOOGLE_CLIENT_ID")
        println("ClientId: " + clientId)
        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(listOf(System.getenv("GOOGLE_CLIENT_ID")))
            .build()
        println("Google id token: " + googleIdToken)
        val idToken: GoogleIdToken? = verifier.verify(googleIdToken)
        println("Id token: " + idToken)
        return idToken?.payload?.subject
    }
}