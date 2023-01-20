import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.freefair.lombok") version "6.4.1"
}

group = "io.github.aquerr"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://m2.dv8tion.net/releases")
    }
}

dependencies {
    implementation("com.sedmelluq:lavaplayer:1.3.78")
    implementation("net.dv8tion:JDA:4.4.0_350") {
        exclude("opus-java")
    }
    implementation("com.vdurmont:emoji-java:5.1.1")

    implementation("org.json:json:20220320")
    implementation("com.typesafe:config:1.4.1")
    implementation("com.jayway.jsonpath:json-path:2.7.0")

    // Database (H2)
    implementation("com.h2database:h2:2.1.214")

    // ORM
    implementation("org.hibernate:hibernate-core:6.1.6.Final")
//    implementation("org.hibernate:hibernate-core-jakarta:6.1.6.Final")
//    implementation("org.glassfish.jaxb:jaxb-runtime:3.0.0")

    // Logging (Log4j2)
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
    implementation("org.slf4j:slf4j-api:1.7.25")

    // Test (JUnit 5)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.mockito:mockito-core:4.4.0")
    testImplementation("org.mockito:mockito-inline:4.4.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.4.0")
    testImplementation("org.assertj:assertj-core:3.22.0")
}


tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "io.github.aquerr.futrzakbot.FutrzakBot"
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
}

tasks.withType<Test> {
    useJUnitPlatform()
}