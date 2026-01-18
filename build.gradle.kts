plugins {
    java
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.github.aquerr"
version = "1.0-SNAPSHOT"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://m2.dv8tion.net/releases")
    }
    maven {
        url = uri("https://maven.lavalink.dev/releases")
    }
}

val jsonVersion = findProperty("jsonVersion") as String
val typeSafeConfigVersion = findProperty("typeSafeConfigVersion") as String
val jsonPathVersion = findProperty("jsonPathVersion") as String
val jdaVersion = findProperty("jdaVersion") as String
val lavaPlayerVersion = findProperty("lavaPlayerVersion") as String
val guavaVersion = findProperty("guavaVersion") as String
val youtubeSourceVersion = findProperty("youtubeSourceVersion") as String

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // DSC Audio START
    implementation("club.minnced:jdave-api:0.1.5")

    implementation("club.minnced:jdave-native-linux-x86-64:0.1.5")
    implementation("club.minnced:jdave-native-linux-aarch64:0.1.5")
    implementation("club.minnced:jdave-native-win-x86-64:0.1.5")
    implementation("club.minnced:jdave-native-darwin:0.1.5")
    // DSC Audio END

    implementation("dev.arbjerg:lavaplayer:${lavaPlayerVersion}")
    implementation("net.dv8tion:JDA:${jdaVersion}") {
        exclude("opus-java")
    }
    implementation("dev.lavalink.youtube:common:${youtubeSourceVersion}")

    implementation("org.json:json:${jsonVersion}")
    implementation("com.typesafe:config:${typeSafeConfigVersion}")
    implementation("com.jayway.jsonpath:json-path:${jsonPathVersion}")
    implementation("com.google.guava:guava:${guavaVersion}")

    // Logging (Log4j2)
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    modules {
        module("org.springframework.boot:spring-boot-starter-logging") {
            replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
        }
    }

    // Database (H2)
    runtimeOnly("com.h2database:h2")

    compileOnly("org.projectlombok:lombok")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-quartz-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
