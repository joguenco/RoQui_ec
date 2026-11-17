# Keycloak Configuration

## Mariadb
## Create database and user
```sh
CREATE database keycloak;
```
```sh
CREATE USER 'keycloak'@'%' IDENTIFIED BY 'k';
```
```sh
GRANT ALL PRIVILEGES ON keycloak.* TO 'keycloak'@'%' WITH GRANT OPTION;
```

## conf/keycloak.conf
Only http and mariadb
```
http-enabled=true
hostname-strict=false
http-port=9090
db=mariadb
db-username=keycloak
db-password=k
db-url=jdbc:mariadb://localhost/keycloak
```
## bin/kc.bat
Add for set JAVA_HOME
```
set "JAVA_HOME=C:\Java\AdoptiumJDK-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo JAVA_HOME is set to %JAVA_HOME%
java -version
```

## Windows Service
- Download, extract and copy
https://downloads.apache.org/commons/daemon/binaries/windows/
```powershell
copy "path\to\prunsrv.exe" "%KEYCLOAK_HOME%\bin\prunsrv.exe"
```
Delayed auto-start:
```powershell
bin\kc.bat tools windows-service install --name keycloak --display-name="Keycloak Server" --depends-on="MariaDB" --startup=delayed
```
Start the service:
```powershell
net start keycloak
```
Stop the service:
```powershell
net stop keycloak
```
Service status
```powershell
Get-Service -Name "keycloak"
```
Uninstall the service:
```powershell
bin\kc.bat tools windows-service uninstall --name keycloak
```
Forcefully terminate the service
```powershell
taskkill /f /im prunsrv.exe
```