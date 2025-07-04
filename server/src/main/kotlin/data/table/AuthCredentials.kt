package com.carspotter.data.table

import com.carspotter.data.model.AuthProvider
import org.jetbrains.exposed.dao.id.UUIDTable

object AuthCredentials: UUIDTable("auth_credentials") {
    val email = varchar("email", 255).uniqueIndex()
    val password = text("password").nullable()
    val provider = varchar("provider", 20).default(AuthProvider.REGULAR.name)
    val googleId = text("google_id").nullable()
}