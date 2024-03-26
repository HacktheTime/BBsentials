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
    implementation(libs.discordJDA)
    implementation("me.nullicorn:Nedit:2.2.0")
    implementation("com.github.JnCrMx:discord-game-sdk4j:v0.5.5")
}
java.withSourcesJar()
java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))