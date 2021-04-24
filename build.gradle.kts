plugins {
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.serialization") version "1.4.30"
	id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "dev.defvs"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
	maven("https://dl.bintray.com/nwillc/maven")
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation("com.github.nwillc:ksvg:3.0.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
}
