plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "net.hectus.invade"
version = "0.0.9"
description = "Plugin for Hectus' game mode called \"Invade\", where you search items while trying not to die, kill other players and survive."

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()

    maven("https://marcpg.com/repo/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("com.marcpg:libpg:0.1.1")
    implementation("org.postgresql:postgresql:42.7.3")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

tasks {
    build { dependsOn(shadowJar) }
    processResources {
        filter {
            it.replace("\${version}", version.toString())
        }
    }
    shadowJar {
        relocate("com.marcpg.libpg", "net.hectus.invade.libpg")
    }
    runServer {
        minecraftVersion("1.20.4")
    }
}
