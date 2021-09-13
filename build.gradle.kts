import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version("5.2.0")
}

group = "eu.beegames"
version = "1.2.0"

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
    //components.all(KotlinAlignment::class.java)
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.17-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.3")

    shadow(kotlin("stdlib-jdk8"))
    shadow(kotlin("reflect"))
    shadow("com.zaxxer:HikariCP:4.0.3")
    shadow("org.ktorm:ktorm-core:3.4.1")
    shadow("com.maxmind.geoip2:geoip2:2.15.0")

    shadow("net.kyori:adventure-api:4.8.1")
    shadow("net.kyori:adventure-platform-bungeecord:4.0.0-SNAPSHOT")
    // shadow("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
}

val makeShadow = tasks.register<ShadowJar>("makeShadow") {
    archiveClassifier.set("shade")
    from(sourceSets.main.orNull?.output)
    configurations = mutableListOf(project.configurations.shadow.get())

    dependsOn("relocateShadows")

    doFirst {
        relocators.removeIf {
            // Relocating kotlin somehow breaks kotlin's reflection
            (it is SimpleRelocator) && (it.pattern == "kotlin" || it.pattern.startsWith("kotlin."))
        }
    }
}

tasks {
    withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }
    named("build") {
        dependsOn("makeShadow")
    }

    // To implementors: you must relocate kotlin references to eu.beegames.core.lib.kotlin etc.
    register<ConfigureShadowRelocation>("relocateShadows") {
        target = makeShadow.get()
        prefix = "eu.beegames.core.lib"
    }

    processResources {
        doFirst {
            expand("version" to version)
        }
    }
}