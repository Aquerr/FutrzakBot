plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
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

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://m2.dv8tion.net/releases")
    }
}

dependencies {
    implementation("dev.arbjerg:lavaplayer:2.0.4")
    implementation("net.dv8tion:JDA:5.0.0-beta.18") {
        exclude("opus-java")
    }
    implementation("com.vdurmont:emoji-java:5.1.1")

    implementation("org.json:json:20230227")
    implementation("com.typesafe:config:1.4.3")
    implementation("com.jayway.jsonpath:json-path:2.8.0")
    implementation("com.google.guava:guava:32.1.3-jre")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-quartz") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Logging (Log4j2)
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    // Database (H2)
    implementation("com.h2database:h2")

    // Test (JUnit 5)
    testImplementation("org.mockito:mockito-bom:5.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.assertj:assertj-core")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
