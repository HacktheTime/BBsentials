plugins {
    java
}

dependencies {
    implementation(libs.apache.httpclient)
    implementation(libs.legacyGson)
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))
