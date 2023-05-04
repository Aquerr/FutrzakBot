plugins {
    java
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
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
    implementation("com.github.walkyst:lavaplayer-fork:1.4.0")
    implementation("net.dv8tion:JDA:5.0.0-beta.6") {
        exclude("opus-java")
    }
    implementation("com.vdurmont:emoji-java:5.1.1")

    implementation("org.json:json:20220320")
    implementation("com.typesafe:config:1.4.1")
    implementation("com.jayway.jsonpath:json-path:2.7.0")
    implementation("com.google.guava:guava:31.1-jre")
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
    implementation("org.springframework.boot:spring-boot-starter-log4j2:3.0.1")

    // Database (H2)
    implementation("com.h2database:h2:2.1.214")

    // ORM
    implementation("org.hibernate:hibernate-core:6.1.6.Final")
//    implementation("org.hibernate:hibernate-core-jakarta:6.1.6.Final")
//    implementation("org.glassfish.jaxb:jaxb-runtime:3.0.0")

    // Test (JUnit 5)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.mockito:mockito-core:4.4.0")
    testImplementation("org.mockito:mockito-inline:4.4.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.4.0")
    testImplementation("org.assertj:assertj-core:3.22.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
