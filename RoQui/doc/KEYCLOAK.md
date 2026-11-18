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
## Install Chocolatey
https://chocolatey.org/install
## Set hostname
- C:\Windows\System32\drivers\etc\hosts
```
192.168.56.102      k.vm10.oe
```
## Certificates
- Install mkcert
```powershell
sudo choco install mkcert
```
- Install Certificate Authority in local
```powershell
sudo mkcert -install
```
- Generate Subject Alternative Name 
```powershell
mkcert k.vm10.oe
```
## Keycloak
- Configure: conf/keycloak.conf
```
https-port=9090
https-certificate-file=C:/app/cert/k.vm10.oe.pem
https-certificate-key-file=C:/app/cert/k.vm10.oe-key.pem
hostname=k.vm10.oe
```
- MariaDB
```
db=mariadb
db-username=keycloak
db-password=k
db-url=jdbc:mariadb://localhost/keycloak
```
- Add for set JAVA_HOME: bin/kc.bat
```
set "JAVA_HOME=C:\Java\AdoptiumJDK-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo JAVA_HOME is set to %JAVA_HOME%
java -version
```
- Show configuraton
```powershell
.\bin\kc.bat show-config
```
- Create temporal administrator user
```powershell
.\bin\kc.bat bootstrap-admin user
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
## Install nginx as service
- Download nginx: https://nginx.org/en/download.html
- Extract in C:\app\nginx
- Donwload nssm: https://nssm.cc/download, and copy in C:\app\nginx
- Install nginx as a services
```powershell
sudo C:\app\nginx\nssm.exe install nginx C:\app\nginx\nginx.exe
```
- Remove service
```powershell
sudo C:\app\nginx\nssm.exe remove nginx  confirm
```
- Command for nginx
```powershell
.\nginx.exe -t       # validate config
.\nginx.exe -s reload  
.\nginx.exe -s stop    
```
- Add in C:\app\nginx\conf\nginx.conf:
```
include conf.d/*.conf;
```
and create conf.d/ folder and k.vm10.oe.conf file
```
# Reverse proxy para Keycloak
# https://k.vm10.oe  ->  https://k.vm10.oe:9090

# Redirigir HTTP a HTTPS
server {
    listen      80;
    server_name k.vm10.oe;

    return 301 https://$host$request_uri;
}

server {
    listen      443 ssl;
    http2       on;
    server_name k.vm10.oe;

    # Certificados (emitidos para k.vm10.oe)
    ssl_certificate     C:/app/cert/k.vm10.oe.pem;
    ssl_certificate_key C:/app/cert/k.vm10.oe-key.pem;

    ssl_session_cache   shared:SSL:10m;
    ssl_session_timeout 10m;
    ssl_protocols       TLSv1.2 TLSv1.3;
    ssl_ciphers         HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    client_max_body_size 10m;

    location / {
        proxy_pass https://k.vm10.oe:9090;

        # El backend usa un certificado local/self-signed
        proxy_ssl_verify off;
        proxy_ssl_server_name on;
        proxy_ssl_name k.vm10.oe;

        proxy_http_version 1.1;
        proxy_set_header Host              $host;
        proxy_set_header X-Real-IP         $remote_addr;
        proxy_set_header X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host  $host;
        proxy_set_header X-Forwarded-Port  443;

        # Soporte para WebSocket (consola de administración de Keycloak)
        proxy_set_header Upgrade    $http_upgrade;
        proxy_set_header Connection "upgrade";

        proxy_connect_timeout 60s;
        proxy_send_timeout    300s;
        proxy_read_timeout    300s;
        proxy_buffering       off;
    }
}
```