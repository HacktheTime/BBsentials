import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    kotlin("jvm") version (libs.versions.kotlinVersion)
    id("com.github.johnrengelman.shadow")
    id("fabric-loom") version "1.4.5"
}
repositories {
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://maven.xpple.dev/maven2")
}


val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
    isTransitive = false
}

dependencies {
    shadowImpl(project(":common"))

    minecraft(libs.modern.minecraft)
    mappings("net.fabricmc:yarn:${libs.versions.modern.yarn.get()}:v2")
    modRuntimeOnly(libs.modern.devauth)
    modImplementation(libs.modern.fabric.loader)
    modImplementation(libs.modern.fabric.api)
    modImplementation(libs.modmenu)
    modImplementation("dev.xpple:clientarguments:1.7")?.let { include(it) }

//    modImplementation(libs.discordJDA)?.let { include(it) }
    implementation(libs.discordJDA)?.let { include(it) }
    modApi(libs.clothConfig) {
        exclude(group = "net.fabricmc.fabric-api")
    }
}
tasks.processResources {
    from(project(":common").sourceSets["main"].resources.srcDirs)
    inputs.property("version", project.version)
    inputs.property("minecraft_versionSupported", libs.versions.modern.minecraft.get())
    inputs.property("loader_version", libs.versions.modern.fabric.loader.get())
    inputs.property("fabric_api_version", libs.versions.modern.fabric.api.get())
    filesMatching("fabric.mod.json") {
        expand(inputs.properties)
    }
    from(project(":common").sourceSets["main"].resources.srcDirs)
}
tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "17" }

java {
    java {

    withSourcesJar()
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}

val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)
}

tasks.assemble.get().dependsOn(tasks.remapJar)