plugins {
    java
    idea
    kotlin("jvm") version (libs.versions.kotlinVersion)
    id("com.github.johnrengelman.shadow")
}


dependencies {
    implementation(libs.apache.httpclient)
    implementation(libs.legacyGson)
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("javazoom:jlayer:1.0.1")
    implementation("net.dv8tion:JDA:5.0.0-beta.23")
//    implementation(libs.discordJDA)
    implementation("me.nullicorn:Nedit:latest.release")
    implementation("net.hypixel:mod-api:latest.release")
    implementation("com.github.JnCrMx:discord-game-sdk4j:latest.release")
}
java.withSourcesJar()
java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))