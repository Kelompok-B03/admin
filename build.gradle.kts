plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "6.0.1.5171"
    id("com.github.ben-manes.versions") version "0.51.0"
}


group = "id.ac.ui.cs.gatherlove"
version = "0.0.1-SNAPSHOT"

val versions = mapOf(
    "seleniumJava" to "4.14.1",
    "seleniumJupiter" to "5.0.1",
    "webdrivermanager" to "5.6.3",
    "junitJupiter" to "5.9.1",
    "postgresql" to "42.7.2",
    "dotenv" to "3.0.0"
)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Database
    implementation("org.postgresql:postgresql:${versions["postgresql"]}")

    // Utilities
    implementation("io.github.cdimascio:dotenv-java:${versions["dotenv"]}")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.github.bonigarcia:webdrivermanager:${versions["webdrivermanager"]}")
    testImplementation("org.seleniumhq.selenium:selenium-java:${versions["seleniumJava"]}")
    testImplementation("io.github.bonigarcia:selenium-jupiter:${versions["seleniumJupiter"]}")
    testImplementation("com.h2database:h2:2.2.224")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.h2database:h2:2.2.224") //test database
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")

    // JJwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    filter {
        excludeTestsMatching("*FunctionalTest")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.register<Test>("functionalTest") {
    group = "verification"
    description = "Runs functional tests"
    useJUnitPlatform()
    filter {
        includeTestsMatching("*FunctionalTest")
    }
    shouldRunAfter(tasks.test)
}

tasks.test {
    systemProperty("spring.profiles.active", "test")
}

springBoot {
    mainClass.set("id.ac.ui.cs.gatherlove.admin.AdminApplication")
}

tasks {
    bootJar {
        archiveFileName.set("gatherlove-admin.jar")
    }
    jar {
        enabled = false
    }
}