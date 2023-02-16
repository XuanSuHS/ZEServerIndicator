plugins {
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"
}

group = "top.xuansu.mirai.zeServerIndicator"
version = "0.1.2"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://mvnrepository.com/repos/central")
    //mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("com.squareup.okhttp3:okhttp:4.10.0")
    //implementation("org.eclipse.jetty.websocket:websocket-client:9.4.50.v20221201")
    //implementation("org.springframework.boot:spring-boot-starter-websocket:3.0.2")
    //implementation("org.java-websocket:Java-WebSocket:1.5.3")
}