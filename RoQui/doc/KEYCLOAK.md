# Keycloak Configuration

## Mariadb
## Create database and user
```
CREATE database keycloak;
```
```
CREATE USER 'keycloak'@'%' IDENTIFIED BY 'k';
```
```
GRANT ALL PRIVILEGES ON keycloak.* TO 'keycloak'@'%' WITH GRANT OPTION;
```

## keycloak.conf
Only http

```
http-enabled=true
hostname-strict=false
http-port=9090
```
### Mariadb
```
db=mariadb
db-username=keycloak
db-password=k
db-url=jdbc:mariadb://localhost/keycloak
```
