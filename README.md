# RoQui_ec
E-Invoicing for Ecuador

## Dependencies
- Java 21
- Gradle 8.13
- MariaDB 11.4

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