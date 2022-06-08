import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("com.github.oshi:oshi-core:6.1.6")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.kodein.db:kodein-db:0.9.0-beta")
    implementation("org.kodein.db:kodein-leveldb-jni-jvm-windows:0.9.0-beta")
    implementation ("org.kodein.db:kodein-db-serializer-kotlinx:0.9.0-beta")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.json:json:20220320")
    implementation(fileTree("lib/Definity-Webhooks-1.0.jar"))

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
