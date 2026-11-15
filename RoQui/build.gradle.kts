plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    war
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.3.21"
    kotlin("kapt") version "2.4.21"
    id("com.ncorti.ktfmt.gradle") version "0.27.0"
}

group = "dev.joguenco"

version = "3.0.0"

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts/") }
}

dependencies {
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat-runtime")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    // Database
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:4.0.0")
    // AutoMapper Entity to DTO
    implementation("org.mapstruct:mapstruct:1.6.3")
    kapt("org.mapstruct:mapstruct-processor:1.6.3")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Signer
    implementation("dev.joguenco.signer:RoQuiSigner:2.0.0")
    implementation("eu.europa.ec.joinup.sd-dss:dss-xades:6.5.1")
    implementation("eu.europa.ec.joinup.sd-dss:dss-utils-apache-commons:6.5.1")
    // Printer
    implementation("dev.joguenco.printer:RoQuiPrinter:3.0.0")
    implementation("com.sun.xml.ws:jaxws-rt:4.0.5")
    implementation("net.sf.jasperreports:jasperreports:7.1.1")
    implementation("net.sf.jasperreports:jasperreports-pdf:7.1.1")
    implementation("net.sf.jasperreports:jasperreports-barcode4j:7.1.1")
    // Client SRI
    implementation("dev.joguenco.client:RoQuiClientSri:1.3.0")
    implementation("com.thoughtworks.xstream:xstream:1.4.21")
    implementation("commons-io:commons-io:2.22.0")
    // Utils
    implementation("joda-time:joda-time:2.15.1")
    // Email
    implementation("org.apache.commons:commons-email2-jakarta:2.0.0-M1")
    implementation("com.sun.mail:jakarta.mail:2.0.2")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

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
    keepJavacAnnotationProcessors = true
}

ktfmt { kotlinLangStyle() }

// ktfmt requires Kotlin 2.x
configurations
    .matching { it.name == "ktfmt" }
    .configureEach {
        resolutionStrategy.eachDependency {
            if (
                requested.group == "org.jetbrains.kotlin" &&
                    requested.name == "kotlin-compiler-embeddable"
            ) {
                useVersion("2.3.21")
                because("ktfmt requires Kotlin 2.x compiler")
            }
        }
    }

tasks.withType<Test> { useJUnitPlatform() }
