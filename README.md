# RoQui_ec
E-Invoicing for Ecuador developed in Kotlin with Spring Boot 3

## Dependencies
- Java 21
- Gradle 8.13
- MariaDB 11.4

## Libraries
- https://github.com/joguenco/RoquiSigner
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
Frontend for RoQui_ec developed in Vue 3 with Vite
## Dependencies
- Node 22

## Initialization
```
pnpm install
```
```
pnpm format
```
```
pnpm dev
```