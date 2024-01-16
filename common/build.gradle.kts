plugins {
    java
    idea
    kotlin("jvm") version (libs.versions.kotlinVersion)
}

dependencies {
    implementation(libs.apache.httpclient)
    implementation(libs.legacyGson)
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation(libs.discordJDA)

}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))
