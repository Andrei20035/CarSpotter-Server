
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.carspotter"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform() // This enables JUnit 5
}


dependencies {
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.java.time)
    implementation(libs.h2)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.config.yaml)
    implementation("io.ktor:ktor-server-host-common:3.1.1")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.apache.commons:commons-compress:1.27.1")

    testImplementation ("org.jetbrains.kotlin:kotlin-test-junit5:2.1.20")
    testImplementation("org.testcontainers:testcontainers:1.20.6")
    testImplementation("org.testcontainers:junit-jupiter:1.20.6")
    testImplementation("org.testcontainers:postgresql:1.20.6")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.0")
    testImplementation(libs.kotlinTestJunit5)
    testImplementation(libs.ktor.server.test.host)

}
