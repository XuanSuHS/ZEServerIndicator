plugins {
    val kotlinVersion = "1.8.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"
}

group = "top.xuansu.mirai.zeServerIndicator"
version = "0.1.9-B7"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://mvnrepository.com/repos/central")
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("com.squareup.okhttp3:okhttp:4.10.0")
}