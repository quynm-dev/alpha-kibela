import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask

val kotlinVersion: String by project
val logbackVersion: String by project
val hikariCPVersion: String by project
val mysqlConnectorJVersion: String by project
val exposedVersion: String by project
val liquibaseVersion: String by project
val koinVersion: String by project
val koinKspVersion: String by project
val resultVersion: String by project
val ktorVersion: String by project
val microLoggingVersion: String by project
val h2Version: String by project
val jUnitVersion: String by project
val mockkVersion: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.12"
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
    kotlin("plugin.serialization") version "1.9.10"
    id("com.ncorti.ktfmt.gradle") version "0.19.0"
}

group = "alpha"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-apache-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    // DI
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    implementation("io.insert-koin:koin-annotations:$koinKspVersion")
    ksp("io.insert-koin:koin-ksp-compiler:$koinKspVersion")

    // Railway
    implementation("com.michael-bull.kotlin-result:kotlin-result:$resultVersion")

    // DB
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariCPVersion")
    implementation("com.mysql:mysql-connector-j:$mysqlConnectorJVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$microLoggingVersion")

    // UT
    testImplementation("com.h2database:h2:$h2Version")
    testImplementation("org.junit.jupiter:junit-jupiter:$jUnitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

ktfmt {
    kotlinLangStyle()
}

tasks.register<KtfmtFormatTask>("lint") {
    source = project.fileTree(rootDir)
    include("**/*.kt")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
