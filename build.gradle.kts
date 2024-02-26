plugins {
    java
    id("org.springframework.boot") version "3.2.3"
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
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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

val jsonVersion = findProperty("jsonVersion") as String
val typeSafeConfigVersion = findProperty("typeSafeConfigVersion") as String
val jsonPathVersion = findProperty("jsonPathVersion") as String
val mockitoBomVersion = findProperty("mockitoBomVersion") as String
val mockitoInlineVersion = findProperty("mockitoInlineVersion") as String
val jdaVersion = findProperty("jdaVersion") as String
val lavaPlayerVersion = findProperty("lavaPlayerVersion") as String
val guavaVersion = findProperty("guavaVersion") as String

dependencies {
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

    implementation("dev.arbjerg:lavaplayer:${lavaPlayerVersion}")
    implementation("net.dv8tion:JDA:${jdaVersion}") {
        exclude("opus-java")
    }

    implementation("org.json:json:${jsonVersion}")
    implementation("com.typesafe:config:${typeSafeConfigVersion}")
    implementation("com.jayway.jsonpath:json-path:${jsonPathVersion}")
    implementation("com.google.guava:guava:${guavaVersion}")

    // Logging (Log4j2)
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    // Database (H2)
    implementation("com.h2database:h2")

    compileOnly("org.projectlombok:lombok")

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Test (JUnit 5)
    testImplementation("org.mockito:mockito-bom:${mockitoBomVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-inline:${mockitoInlineVersion}")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
