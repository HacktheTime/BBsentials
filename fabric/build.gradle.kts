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
    maven("https://repo.hypixel.net/repository/Hypixel/")
}


val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    shadowImpl(project(":common"))

    minecraft(libs.modern.minecraft)
    mappings("net.fabricmc:yarn:${libs.versions.modern.yarn.get()}:v2")
    modRuntimeOnly(libs.modern.devauth)
    modImplementation(libs.modern.fabric.loader)
    modImplementation(libs.modern.fabric.api)
    modImplementation(libs.modmenu)
    modImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
    modImplementation("dev.xpple:clientarguments:1.7")?.let { include(it) }
    implementation("com.github.JnCrMx:discord-game-sdk4j:latest.release")
    modImplementation("net.hypixel:mod-api:latest.release")
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
    dependsOn(tasks.shadowJar)
    mustRunAfter(tasks.shadowJar)
    archiveClassifier.set("")
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    relocate("net.hypixel", "de.hype.bbsentials.deps")
    configurations = listOf(shadowImpl)
}

loom {
    log4jConfigs.from(project.rootProject.file("log4j2.xml"))
}

tasks.assemble.get().dependsOn(tasks.remapJar)