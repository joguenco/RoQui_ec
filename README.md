# RoQui_ec
E-Invoicing for Ecuador developed in Kotlin with Spring Boot 3

## Dependencies
- Java 21
- Gradle 8.13
- Spring Boot 3.5
- MariaDB 11.4

## Libraries
- https://github.com/joguenco/RoQuiSigner
- https://github.com/joguenco/RoQuiPrinter
- https://github.com/joguenco/RoQuiClientSri

## Hot Reload
In one terminal run:
```
gradle compileKotlin --continuous --parallel --build-cache --configuration-cache
```
In second terminal run:
```
gradle bootRun
```

## Format source code
```
gradle ktfmtFormat
```

# RoQui Web Client
[Frontend for RoQui_ec](roqui-client/README.md)

# Email Server for Development
```
docker run --name mailhog -d -p 1025:1025 -p 8025:8025 mailhog/mailhog
```