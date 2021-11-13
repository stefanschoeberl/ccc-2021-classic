import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    id("antlr")
}

group = "dev.schoeberl.ccc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.8")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-lib", "src/main/antlr", "-visitor")
}

tasks.compileKotlin {
    dependsOn("generateGrammarSource")
}

task<JavaExec>("runLevel1") {
    main = "dev.schoeberl.ccc.lv1.Level1Kt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("runLevel2") {
    main = "dev.schoeberl.ccc.lv2.Level2Kt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("runLevel3") {
    main = "dev.schoeberl.ccc.lv3.Level3Kt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("runLevel4") {
    main = "dev.schoeberl.ccc.lv4.Level4Kt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("runLevel5") {
    main = "dev.schoeberl.ccc.lv5.Level5Kt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("runLevel6") {
    main = "dev.schoeberl.ccc.lv6.Level6Kt"
    classpath = sourceSets["main"].runtimeClasspath
}

task<Task>("runAll") {
    dependsOn("runLevel1")
    dependsOn("runLevel2")
    dependsOn("runLevel3")
    dependsOn("runLevel4")
    dependsOn("runLevel5")
    dependsOn("runLevel6")
}