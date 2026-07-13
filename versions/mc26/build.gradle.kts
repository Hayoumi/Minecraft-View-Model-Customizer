plugins {
    id("net.fabricmc.fabric-loom") version "1.15.5"
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set("${project.property("archives_base_name")}-mc26.1.2")
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

sourceSets.main {
    java.srcDir("../../shared/src/main/java")
    resources.srcDir("../../shared/src/main/resources")
}

dependencies {
    minecraft("com.mojang:minecraft:26.1.2")
    implementation("net.fabricmc:fabric-loader:0.19.3")
    implementation("net.fabricmc.fabric-api:fabric-api:0.154.2+26.1.2")

    val lwjglVersion = "3.3.3"
    implementation("org.lwjgl:lwjgl-nanovg:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-macos")
    runtimeOnly("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-macos-arm64")
    include("org.lwjgl:lwjgl-nanovg:$lwjglVersion")
    include("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-windows")
    include("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-linux")
    include("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-macos")
    include("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-macos-arm64")
}

tasks.processResources {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"
    filesMatching("fabric.mod.json") { expand("version" to project.version) }
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<JavaCompile>().configureEach { options.release.set(25) }
