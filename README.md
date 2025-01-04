# RoQui_ec
E-Invoicing for Ecuador

## Dependencies
- Java 21
- Gradle 8.11.1
-c MariaDB 11.4

## Hot Reload
In one terminal run:
```
gradle compileKotlin --continuous --parallel --build-cache --configuration-cache
```
In second terminal run:
```
gradle bootRun
```

