plugins {
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version("5.2.0")
}

group = "eu.beegames"
version = "1.1.2"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.16-R0.5-SNAPSHOT")

    shadow(kotlin("stdlib"))
}

tasks {
    named("build") {
        dependsOn("shadowJar")
    }
    processResources {
        // unfortunately, doesn't work very well...
        // expand(Pair("version", project.version))
    }
}

