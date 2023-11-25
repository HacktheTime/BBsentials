pluginManagement {
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
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "gg.essential.loom" -> useModule("gg.essential:architectury-loom:${requested.version}")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.6.0")
}


rootProject.name = "BBsentialsAll"

include("common", "fabric", "forge")
