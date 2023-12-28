plugins {
    application
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
}

application {
    mainClass.set("dev.lythium.sharkbackend.MainKt")
}

repositories {
    mavenCentral()
}

val ktorVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val postgresqlVersion: String by project

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-money:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "dev.lythium.sharkbackend.MainKt"
    }
}