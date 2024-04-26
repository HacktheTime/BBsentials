plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    idea
    java
}


allprojects {

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.architectury.dev/")
        maven("https://maven.notenoughupdates.org/releases/")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net/")
        maven("https://repo.spongepowered.org/maven/")
        maven("https://repo.sk1er.club/repository/maven-releases/")
        maven("https://maven.wagyourtail.xyz/releases")
        maven("https://maven.wagyourtail.xyz/snapshots")
        maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
        maven("https://maven.xpple.dev/maven2")
        maven("https://maven.terraformersmc.com/releases")
        maven("https://maven.shedaniel.me/")
        maven("https://jitpack.io")
        maven("https://repo.hypixel.net/repository/Hypixel/")
    }

    afterEvaluate {
        tasks.withType(AbstractArchiveTask::class) {
            archiveBaseName.set("BBsentials-${project.name}")
            archiveVersion.set(libs.versions.modVersion)
        }
        tasks.withType(JavaCompile::class) {
            options.encoding = "UTF-8"
        }
        tasks.findByName("processResources")?.run {
            this as Copy
            from(rootProject.file("LICENSE")) {
                rename { "BBsentials-LICENSE" }
            }
        }
    }
}

/*
task copyJars(type: Copy) {
    from project(":forge").fileTree("build/libs") // Copy JARs from the :forge subproject
    into file("$rootDir/build/libs") // Copy them to the global output directory
    from project(":fabric").fileTree("build/libs") // Copy JARs from the :fabric subproject
    into file("$rootDir/build/libs") // Copy them to the global output directory
}*/

