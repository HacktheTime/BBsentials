plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    idea
    java
}


allprojects {

    repositories {
        mavenCentral()
        maven("https://repo.spongepowered.org/maven/")
        maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
        maven("https://maven.notenoughupdates.org/releases/")
        maven("https://maven.terraformersmc.com/releases")
        maven("https://maven.shedaniel.me/")
        jcenter()
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

