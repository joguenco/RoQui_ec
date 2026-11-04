# RoQui_ec
E-Invoicing for Ecuador developed in Kotlin with Spring Boot 4
## Dependencies
- Java 25
- Gradle 9.8.2
- Spring Boot 4.1
- MariaDB
- PostgreSQL
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
## Swagger UI
```
http://localhost:8080/docs
```
## Format source code
```
gradle ktfmtFormat
```
## Keycloak docker compose
```
docker-compose up
```
```
docker-compose down
```
## Rebuild docker images
```
docker-compose up --build
```
## Email Server for Development
```
docker run --name mailhog -d -p 1025:1025 -p 8025:8025 mailhog/mailhog
```
