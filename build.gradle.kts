import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    war
    id("org.springframework.boot") version "3.4.9"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt") version "1.9.25"
    id("com.ncorti.ktfmt.gradle") version "0.22.0"
}

group = "dev.joguenco"

version = "0.1.0"

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Security
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    // SystemUtils
    implementation("org.apache.commons:commons-lang3:3.13.0")
    // AutoMapper Entity to DTO
    implementation("org.mapstruct:mapstruct:1.6.3")
    kapt("org.mapstruct:mapstruct-processor:1.6.3")
    // Signer
    implementation("dev.joguenco.signer:RoQuiSigner:1.0.0")
    implementation("com.googlecode.xades4j:xades4j:1.7.0")
    implementation("com.sun.xml.bind:jaxb-impl:2.3.9")
    // Printer
    implementation("dev.joguenco.printer:RoQuiPrinter:1.1.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")
    implementation("com.sun.xml.ws:jaxws-rt:4.0.1")
    implementation("net.sf.jasperreports:jasperreports:6.21.2")
    implementation("net.sf.barcode4j:barcode4j:2.1")
    implementation("org.apache.xmlgraphics:batik-all:1.17")
    implementation("com.github.librepdf:openpdf:1.3.30")
    // Client SRI
    implementation("dev.joguenco.client:RoQuiClientSri:1.1.0")
    implementation("com.sun.xml.ws:jaxws-rt:4.0.0")
    implementation("com.thoughtworks.xstream:xstream:1.4.20")
    implementation("commons-io:commons-io:2.12.0")
    // Utils
    implementation("joda-time:joda-time:2.14.0")
}

kotlin { compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") } }

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

kapt {
    arguments {
        // Set Mapstruct Configuration options here
        // https://kotlinlang.org/docs/reference/kapt.html#annotation-processor-arguments
        // https://mapstruct.org/documentation/stable/reference/html/#configuration-options
        arg("mapstruct.defaultComponentModel", "spring")
    }
}

ktfmt { kotlinLangStyle() }

tasks.withType<Test> { useJUnitPlatform() }

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}
