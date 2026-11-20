#Requires -RunAsAdministrator

param(
    [string]$IpMariaDB = "localhost",
    [string]$DbUsername = "keycloak",
    [string]$DbPassword = "k",
    [string]$DominioTomcat = "tomcat.vm.oe",
    [string]$DominioSPA = "client.vm.oe",
    [string]$DominioKeycloak = "keycloak.vm.oe",
    [string]$DbRoquiName = "donpos",
    [string]$DbRoquiUsername = "donpos",
    [string]$DbRoquiPassword = "d",
    [string]$KeyProperty = "dieciseisnumeros"
)

# --- VERSIONES ---
$VersionJava          = "25.0.5+8"
$VersionTomcat        = "11.0.29"
$VersionNginx         = "1.30.6"
$VersionMkcert        = "1.4.4"
$VersionKeycloak      = "26.8.2"
$VersionCommonsDaemon = "1.6.1"

# --- CONFIGURACIÓN DE RUTAS, DOMINIOS Y USUARIO ---
$RutaBase      = "C:\app"
$RutaJava      = "$RutaBase\Java\Temurin25"
$RutaTomcat    = "$RutaBase\Tomcat11"
$RutaNginx     = "$RutaBase\nginx"
$RutaKeycloak  = "$RutaBase\keycloak"
$RutaBin       = "$RutaBase\bin"
$MkcertExe     = "$RutaBin\mkcert.exe"
$UsuarioActual = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name
$NssmExe       = "$RutaNginx\nssm.exe"

# 0. Verificación previa: mostrar qué componentes ya están instalados
#    (útil cuando se vuelve a ejecutar este script)
$YaJava   = Test-Path -Path "$RutaJava\bin\java.exe"
$YaTomcat = (Test-Path -Path "$RutaTomcat\conf\server.xml") -and (Test-Path -Path "$RutaTomcat\bin\service.bat")
$YaNginx  = Test-Path -Path "$RutaNginx\nginx.exe"
$YaNssm   = Test-Path -Path $NssmExe
$YaMkcert = Test-Path -Path $MkcertExe
$YaKeycloak = Test-Path -Path "$RutaKeycloak\bin\kc.bat"

Write-Host "`n--- Verificación de componentes ---" -ForegroundColor Cyan
if ($YaJava)   { Write-Host "Java 25 (Temurin): YA INSTALADO en $RutaJava" -ForegroundColor Green } else { Write-Host "Java 25 (Temurin): no instalado" -ForegroundColor Yellow }
if ($YaTomcat) { Write-Host "Tomcat 11        : YA INSTALADO en $RutaTomcat" -ForegroundColor Green } else { Write-Host "Tomcat 11        : no instalado" -ForegroundColor Yellow }
if ($YaNginx)  { Write-Host "Nginx            : YA INSTALADO en $RutaNginx" -ForegroundColor Green } else { Write-Host "Nginx            : no instalado" -ForegroundColor Yellow }
if ($YaMkcert) { Write-Host "mkcert           : YA INSTALADO en $MkcertExe" -ForegroundColor Green } else { Write-Host "mkcert           : no instalado" -ForegroundColor Yellow }
if ($YaNssm)   { Write-Host "NSSM             : YA INSTALADO en $NssmExe" -ForegroundColor Green } else { Write-Host "NSSM             : no instalado" -ForegroundColor Yellow }
if ($YaKeycloak) { Write-Host "Keycloak         : YA INSTALADO en $RutaKeycloak" -ForegroundColor Green } else { Write-Host "Keycloak         : no instalado" -ForegroundColor Yellow }
Write-Host ""

# 1. Crear carpeta base y asignar permisos exclusivos de edición al usuario actual
if (!(Test-Path -Path $RutaBase)) {
    New-Item -ItemType Directory -Path $RutaBase -Force | Out-Null
}
icacls $RutaBase /grant "${UsuarioActual}:(OI)(CI)M" /T /C | Out-Null

# 2. Descargar e instalar Eclipse Temurin 25 manualmente en $RutaJava
$UrlJava     = "https://api.adoptium.net/v3/binary/version/jdk-$($VersionJava -replace '\+', '%2B')/windows/x64/jdk/hotspot/normal/eclipse"
$ZipJava     = "$env:TEMP\Temurin25-jdk.zip"
$TempJava    = "$env:TEMP\Temurin25-extract"

[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor [System.Net.SecurityProtocolType]::Tls12

if (!(Test-Path -Path "$RutaJava\bin\java.exe")) {
    if (!(Test-Path -Path $ZipJava)) {
        Write-Host "Descargando Eclipse Temurin 25 ($VersionJava)..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $UrlJava -OutFile $ZipJava -UseBasicParsing
    }

    if (Test-Path -Path $TempJava) { Remove-Item -Path $TempJava -Recurse -Force }
    Write-Host "Extrayendo Java en $RutaJava..." -ForegroundColor Cyan
    Expand-Archive -Path $ZipJava -DestinationPath $TempJava -Force

    $OrigenJava = (Get-ChildItem -Path $TempJava -Directory | Select-Object -First 1).FullName
    if (!(Test-Path -Path $RutaJava)) { New-Item -ItemType Directory -Path $RutaJava -Force | Out-Null }
    Copy-Item -Path "$OrigenJava\*" -Destination $RutaJava -Recurse -Force
    Remove-Item -Path $TempJava -Recurse -Force -ErrorAction SilentlyContinue
} else {
    Write-Host "Java ya está instalado en $RutaJava. Omitiendo descarga e instalación." -ForegroundColor DarkGreen
}

# 3. Configurar JAVA_HOME y PATH a nivel de sistema (Machine)
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", $RutaJava, "Machine")
$PathMaquina = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
if ($PathMaquina -notlike "*$RutaJava\bin*") {
    [System.Environment]::SetEnvironmentVariable("Path", "$PathMaquina;$RutaJava\bin", "Machine")
}

# 4. Actualizar variables en la sesión actual para que Tomcat detecte JAVA_HOME
$env:JAVA_HOME = $RutaJava
$env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path", "User")

# 5. Verificar la instalación de Java
& "$RutaJava\bin\java.exe" -version

# 6. Instalar Apache Tomcat 11 directamente en $RutaTomcat
#    El paquete tomcat11 ignora el destino "/D=..." y no permite elegir
#    carpeta, por eso se instala manualmente desde el ZIP oficial de Apache.
$UrlTomcat     = "https://archive.apache.org/dist/tomcat/tomcat-11/v$VersionTomcat/bin/apache-tomcat-$VersionTomcat-windows-x64.zip"
$ZipTomcat     = "$env:TEMP\apache-tomcat-$VersionTomcat-windows-x64.zip"
$TempTomcat    = "$env:TEMP\apache-tomcat-$VersionTomcat-extract"
$OrigenTomcat  = "$TempTomcat\apache-tomcat-$VersionTomcat"

[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor [System.Net.SecurityProtocolType]::Tls12

if (!(Test-Path -Path "$RutaTomcat\conf\server.xml") -or !(Test-Path -Path "$RutaTomcat\bin\service.bat")) {
    if (!(Test-Path -Path $ZipTomcat)) {
        Write-Host "Descargando Apache Tomcat $VersionTomcat..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $UrlTomcat -OutFile $ZipTomcat -UseBasicParsing
    }

    if (Test-Path -Path $TempTomcat) { Remove-Item -Path $TempTomcat -Recurse -Force }
    Write-Host "Extrayendo Tomcat en $RutaTomcat..." -ForegroundColor Cyan
    Expand-Archive -Path $ZipTomcat -DestinationPath $TempTomcat -Force

    if (!(Test-Path -Path $RutaTomcat)) { New-Item -ItemType Directory -Path $RutaTomcat -Force | Out-Null }
    Copy-Item -Path "$OrigenTomcat\*" -Destination $RutaTomcat -Recurse -Force
    Remove-Item -Path $TempTomcat -Recurse -Force -ErrorAction SilentlyContinue
} else {
    Write-Host "Tomcat 11 ya está instalado en $RutaTomcat. Omitiendo descarga e instalación." -ForegroundColor DarkGreen
}

# 7. Configurar CATALINA_HOME / CATALINA_BASE e instalar el servicio de Windows
[System.Environment]::SetEnvironmentVariable("CATALINA_HOME", $RutaTomcat, "Machine")
[System.Environment]::SetEnvironmentVariable("CATALINA_BASE", $RutaTomcat, "Machine")
$env:CATALINA_HOME = $RutaTomcat
$env:CATALINA_BASE = $RutaTomcat

if (Test-Path -Path "$RutaTomcat\bin\service.bat") {
    Stop-Service -Name "Tomcat11" -Force -ErrorAction SilentlyContinue
    if (Get-Service -Name "Tomcat11" -ErrorAction SilentlyContinue) {
        & "$RutaTomcat\bin\service.bat" remove Tomcat11 | Out-Null
    }
    & "$RutaTomcat\bin\service.bat" install Tomcat11 | Out-Null
}

# 7a. Variables de entorno para el WAR de RoQui.
#     El servicio de Windows no hereda las variables definidas en la sesion interactiva,
#     por eso se registran en la configuracion propia del servicio (tomcat11.exe //US).
#     El issuer debe coincidir exactamente con el claim "iss" de los tokens de Keycloak.
$EntornoTomcat = @(
    "KEY_CORS_ALLOWED_ORIGIN=https://$DominioSPA",
    "KEYCLOAK_ISSUER_URI=https://$DominioKeycloak/realms/roqui-realm",
    "DB_ROQUI_NAME=$DbRoquiName",
    "DB_ROQUI_USERNAME=$DbRoquiUsername",
    "DB_ROQUI_PASSWORD=$DbRoquiPassword",
    "KEY_PROPERTY=$KeyProperty"
) -join '#'

if (Test-Path -Path "$RutaTomcat\bin\tomcat11.exe") {
    & "$RutaTomcat\bin\tomcat11.exe" //US//Tomcat11 "--Environment=$EntornoTomcat" | Out-Null
    Write-Host "Variables de entorno del WAR configuradas en el servicio Tomcat11." -ForegroundColor Green
}


# 8. Configurar RemoteIpValve en server.xml de Tomcat 11 para soporte de Proxy Inverso HTTPS
$ArchivoServerXml = "$RutaTomcat\conf\server.xml"
if (Test-Path -Path $ArchivoServerXml) {
    Copy-Item -Path $ArchivoServerXml -Destination "$ArchivoServerXml.bak" -Force
    [xml]$XmlTomcat = Get-Content -Path $ArchivoServerXml

    $HostNode = $XmlTomcat.Server.Service.Engine.Host | Where-Object { $_.name -eq "localhost" }
    $ValveExiste = $HostNode.Valve | Where-Object { $_.className -eq "org.apache.catalina.valves.RemoteIpValve" }

    if (-not $ValveExiste -and $HostNode) {
        $RemoteIpValve = $XmlTomcat.CreateElement("Valve")
        $RemoteIpValve.SetAttribute("className", "org.apache.catalina.valves.RemoteIpValve")
        $RemoteIpValve.SetAttribute("remoteIpHeader", "X-Forwarded-For")
        $RemoteIpValve.SetAttribute("protocolHeader", "X-Forwarded-Proto")
        $RemoteIpValve.SetAttribute("protocolHeaderHttpsValue", "https")
        $HostNode.AppendChild($RemoteIpValve) | Out-Null

        $XmlTomcat.Save($ArchivoServerXml)
    }
}

# Asegurar que el servicio de Tomcat quede configurado e iniciado
Set-Service -Name "Tomcat11" -StartupType Automatic -ErrorAction SilentlyContinue
$ServicioTomcat = Get-Service -Name "Tomcat11" -ErrorAction SilentlyContinue
if ($ServicioTomcat -and $ServicioTomcat.Status -eq "Running") {
    Restart-Service -Name "Tomcat11" -Force -ErrorAction SilentlyContinue
} else {
    Start-Service -Name "Tomcat11" -ErrorAction SilentlyContinue
}

# 9. Instalar Nginx manualmente en $RutaNginx y registrarlo como servicio con NSSM
$UrlNginx     = "https://nginx.org/download/nginx-$VersionNginx.zip"
$ZipNginx     = "$env:TEMP\nginx-$VersionNginx.zip"
$TempNginx    = "$env:TEMP\nginx-$VersionNginx-extract"
$OrigenNginx  = "$TempNginx\nginx-$VersionNginx"

if (!(Test-Path -Path "$RutaNginx\nginx.exe")) {
    if (!(Test-Path -Path $ZipNginx)) {
        Write-Host "Descargando Nginx $VersionNginx..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $UrlNginx -OutFile $ZipNginx -UseBasicParsing
    }

    if (Test-Path -Path $TempNginx) { Remove-Item -Path $TempNginx -Recurse -Force }
    Write-Host "Extrayendo Nginx en $RutaNginx..." -ForegroundColor Cyan
    Expand-Archive -Path $ZipNginx -DestinationPath $TempNginx -Force

    if (!(Test-Path -Path $RutaNginx)) { New-Item -ItemType Directory -Path $RutaNginx -Force | Out-Null }
    Copy-Item -Path "$OrigenNginx\*" -Destination $RutaNginx -Recurse -Force
    Remove-Item -Path $TempNginx -Recurse -Force -ErrorAction SilentlyContinue
} else {
    Write-Host "Nginx $VersionNginx ya está instalado en $RutaNginx. Omitiendo descarga e instalación." -ForegroundColor DarkGreen
}

# NSSM se usa para ejecutar nginx.exe como servicio de Windows
$UrlNssm    = "https://nssm.cc/release/nssm-2.24.zip"
$ZipNssm    = "$env:TEMP\nssm-2.24.zip"
$TempNssm   = "$env:TEMP\nssm-2.24-extract"
$ServicioNginx = "nginx"

if (!(Test-Path -Path $NssmExe)) {
    if (!(Test-Path -Path $ZipNssm)) {
        Write-Host "Descargando NSSM..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $UrlNssm -OutFile $ZipNssm -UseBasicParsing
    }

    if (Test-Path -Path $TempNssm) { Remove-Item -Path $TempNssm -Recurse -Force }
    Expand-Archive -Path $ZipNssm -DestinationPath $TempNssm -Force
    Copy-Item -Path "$TempNssm\nssm-2.24\win64\nssm.exe" -Destination $NssmExe -Force
    Remove-Item -Path $TempNssm -Recurse -Force -ErrorAction SilentlyContinue
} else {
    Write-Host "NSSM ya está instalado en $NssmExe. Omitiendo descarga e instalación." -ForegroundColor DarkGreen
}

Write-Host "Registrando el servicio $ServicioNginx..." -ForegroundColor Cyan
Stop-Service -Name $ServicioNginx -Force -ErrorAction SilentlyContinue

# Detener cualquier instancia de nginx iniciada manualmente que ocupe los puertos 80/443
$ProcesosNginx = Get-Process -Name nginx -ErrorAction SilentlyContinue
if ($ProcesosNginx) {
    Write-Host "Deteniendo $(@($ProcesosNginx).Count) proceso(s) de nginx iniciado(s) manualmente..." -ForegroundColor Yellow
    $ProcesosNginx | Stop-Process -Force -ErrorAction SilentlyContinue
}

# Esperar a que se liberen los puertos antes de registrar/iniciar el servicio
$PuertosOcupados = Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue |
    Where-Object { $_.LocalPort -in 80, 443 }
if ($PuertosOcupados) {
    Write-Host "Esperando a que se liberen los puertos 80/443..." -ForegroundColor Yellow
    for ($i = 0; $i -lt 10; $i++) {
        Start-Sleep -Seconds 1
        $PuertosOcupados = Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue |
            Where-Object { $_.LocalPort -in 80, 443 }
        if (!$PuertosOcupados) { break }
    }
}
if ($PuertosOcupados) {
    Write-Host "Advertencia: los puertos 80/443 siguen ocupados; el servicio nginx podría no iniciar." -ForegroundColor Red
}

if (Get-Service -Name $ServicioNginx -ErrorAction SilentlyContinue) {
    & $NssmExe remove $ServicioNginx confirm 2>$null | Out-Null
}
& $NssmExe install $ServicioNginx "$RutaNginx\nginx.exe" 2>$null | Out-Null
& $NssmExe set $ServicioNginx AppDirectory "$RutaNginx" 2>$null | Out-Null
& $NssmExe set $ServicioNginx DisplayName "nginx" 2>$null | Out-Null
& $NssmExe set $ServicioNginx Start SERVICE_AUTO_START 2>$null | Out-Null
& $NssmExe start $ServicioNginx 2>$null | Out-Null


# 10. Instalar mkcert manualmente y registrar la Autoridad de Certificación (CA) local en Windows
$UrlMkcert     = "https://github.com/FiloSottile/mkcert/releases/download/v$VersionMkcert/mkcert-v$VersionMkcert-windows-amd64.exe"

if (!(Test-Path -Path $MkcertExe)) {
    if (!(Test-Path -Path $RutaBin)) { New-Item -ItemType Directory -Path $RutaBin -Force | Out-Null }
    Write-Host "Descargando mkcert $VersionMkcert..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $UrlMkcert -OutFile $MkcertExe -UseBasicParsing
} else {
    Write-Host "mkcert ya está instalado en $MkcertExe. Omitiendo descarga." -ForegroundColor DarkGreen
}

$PathMaquina = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
if ($PathMaquina -notlike "*$RutaBin*") {
    [System.Environment]::SetEnvironmentVariable("Path", "$PathMaquina;$RutaBin", "Machine")
}
$env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path", "User")

& $MkcertExe -install

# 11. Añadir ambos dominios locales al archivo hosts de Windows (si no existen)
$ArchivoHosts = "$env:SystemRoot\System32\drivers\etc\hosts"
foreach ($Dominio in @($DominioTomcat, $DominioSPA, $DominioKeycloak)) {
    $ContenidoHosts = Get-Content -Path $ArchivoHosts -Raw -ErrorAction SilentlyContinue
    if ($ContenidoHosts -notmatch [regex]::Escape($Dominio)) {
        Add-Content -Path $ArchivoHosts -Value "`r`n127.0.0.1`t$Dominio" -Encoding ASCII
    }
}

# 12. Crear carpeta ssl y generar certificados con mkcert para Tomcat y SPA
$CarpetaSSL = "$RutaNginx\conf\ssl"
if (!(Test-Path -Path $CarpetaSSL)) {
    New-Item -ItemType Directory -Path $CarpetaSSL -Force | Out-Null
}

& $MkcertExe -cert-file "$CarpetaSSL\$DominioTomcat.pem" -key-file "$CarpetaSSL\$DominioTomcat-key.pem" $DominioTomcat localhost 127.0.0.1
& $MkcertExe -cert-file "$CarpetaSSL\$DominioSPA.pem" -key-file "$CarpetaSSL\$DominioSPA-key.pem" $DominioSPA
& $MkcertExe -cert-file "$CarpetaSSL\$DominioKeycloak.pem" -key-file "$CarpetaSSL\$DominioKeycloak-key.pem" $DominioKeycloak localhost 127.0.0.1

# 13. Configurar Nginx de forma modular (nginx.conf, conf.d/tomcat.conf y conf.d/client.conf)
$ArchivoNginxConf  = "$RutaNginx\conf\nginx.conf"
$CarpetaConfD      = "$RutaNginx\conf\conf.d"
$ArchivoTomcatConf = "$CarpetaConfD\tomcat.conf"
$ArchivoClientConf = "$CarpetaConfD\client.conf"
$ArchivoKeycloakProxy = "$CarpetaConfD\keycloak.conf"

if (Test-Path -Path $ArchivoNginxConf) {
    Copy-Item -Path $ArchivoNginxConf -Destination "$ArchivoNginxConf.bak" -Force

    if (!(Test-Path -Path $CarpetaConfD)) {
        New-Item -ItemType Directory -Path $CarpetaConfD -Force | Out-Null
    }

    # 13a. Escribir nginx.conf principal
    $ContenidoNginxPrincipal = @"
worker_processes  1;

events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    include       conf.d/*.conf;
}
"@
    [System.IO.File]::WriteAllText($ArchivoNginxConf, $ContenidoNginxPrincipal, (New-Object System.Text.UTF8Encoding $false))

    # 13b. Escribir tomcat.conf (Proxy Inverso HTTPS -> Tomcat 8080)
    $ContenidoTomcatProxy = @"
server {
    listen       80;
    server_name  $DominioTomcat localhost;
    return 301   https://`$host`$request_uri;
}

server {
    listen       443 ssl;
    server_name  $DominioTomcat localhost;

    ssl_certificate      ssl/$DominioTomcat.pem;
    ssl_certificate_key  ssl/$DominioTomcat-key.pem;

    ssl_session_cache    shared:SSL:1m;
    ssl_session_timeout  5m;
    ssl_protocols        TLSv1.2 TLSv1.3;
    ssl_ciphers          HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers  on;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host `$host;
        proxy_set_header X-Real-IP `$remote_addr;
        proxy_set_header X-Forwarded-For `$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto `$scheme;
    }
}
"@
    [System.IO.File]::WriteAllText($ArchivoTomcatConf, $ContenidoTomcatProxy, (New-Object System.Text.UTF8Encoding $false))

    # 13c. Escribir client.conf (Single Page Application HTTPS en nginx/html)
    $ContenidoClientSPA = @"
server {
    listen       80;
    server_name  $DominioSPA;
    return 301   https://`$host`$request_uri;
}

server {
    listen       443 ssl;
    server_name  $DominioSPA;

    ssl_certificate      ssl/$DominioSPA.pem;
    ssl_certificate_key  ssl/$DominioSPA-key.pem;

    ssl_session_cache    shared:SSL:1m;
    ssl_session_timeout  5m;
    ssl_protocols        TLSv1.2 TLSv1.3;
    ssl_ciphers          HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers  on;

    root   html;
    index  index.html;

    location / {
        try_files `$uri `$uri/ /index.html;
    }

    location ~* \.(?:js|css|png|jpg|jpeg|gif|ico|svg|woff2?)$ {
        expires 30d;
        add_header Cache-Control "public, no-transform";
    }
}
"@
    [System.IO.File]::WriteAllText($ArchivoClientConf, $ContenidoClientSPA, (New-Object System.Text.UTF8Encoding $false))

    # 13d. Escribir keycloak.conf (Proxy Inverso HTTPS -> Keycloak 9090)
    $ContenidoKeycloakProxy = @"
server {
    listen       80;
    server_name  $DominioKeycloak;
    return 301   https://`$host`$request_uri;
}

server {
    listen       443 ssl;
    server_name  $DominioKeycloak;

    ssl_certificate      ssl/$DominioKeycloak.pem;
    ssl_certificate_key  ssl/$DominioKeycloak-key.pem;

    ssl_session_cache    shared:SSL:1m;
    ssl_session_timeout  5m;
    ssl_protocols        TLSv1.2 TLSv1.3;
    ssl_ciphers          HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers  on;

    client_max_body_size 10m;

    location / {
        proxy_pass https://127.0.0.1:9090;

        # Keycloak sirve HTTPS con certificado local (mkcert)
        proxy_ssl_verify off;
        proxy_ssl_server_name on;
        proxy_ssl_name $DominioKeycloak;

        proxy_http_version 1.1;
        proxy_set_header Host `$host;
        proxy_set_header X-Real-IP `$remote_addr;
        proxy_set_header X-Forwarded-For `$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto `$scheme;
        proxy_set_header X-Forwarded-Host `$host;
        proxy_set_header X-Forwarded-Port  443;

        # WebSocket para la consola de administración
        proxy_set_header Upgrade `$http_upgrade;
        proxy_set_header Connection "upgrade";

        proxy_connect_timeout 60s;
        proxy_send_timeout    300s;
        proxy_read_timeout    300s;
        proxy_buffering       off;
    }
}
"@
    [System.IO.File]::WriteAllText($ArchivoKeycloakProxy, $ContenidoKeycloakProxy, (New-Object System.Text.UTF8Encoding $false))

    # Validar sintaxis de Nginx y reiniciar el servicio
    Push-Location $RutaNginx
    & "$RutaNginx\nginx.exe" -t
    Pop-Location
    Restart-Service -Name "nginx" -ErrorAction SilentlyContinue
}

# 14. Instalar Keycloak 26.8.2, configurarlo y registrarlo como servicio con Apache Commons Daemon (prunsrv.exe)
$UrlKeycloak      = "https://github.com/keycloak/keycloak/releases/download/$VersionKeycloak/keycloak-$VersionKeycloak.zip"
$ZipKeycloak      = "$env:TEMP\keycloak-$VersionKeycloak.zip"
$ZipKeycloakLocal = "$RutaBase.bak\keycloak-$VersionKeycloak.zip"
$TempKeycloak     = "$env:TEMP\keycloak-$VersionKeycloak-extract"
$ServicioKeycloak = "keycloak"

if (!(Test-Path -Path "$RutaKeycloak\bin\kc.bat")) {
    if (!(Test-Path -Path $ZipKeycloak)) {
        if (Test-Path -Path $ZipKeycloakLocal) {
            Write-Host "Copiando Keycloak $VersionKeycloak desde $ZipKeycloakLocal..." -ForegroundColor Cyan
            Copy-Item -Path $ZipKeycloakLocal -Destination $ZipKeycloak -Force
        } else {
            Write-Host "Descargando Keycloak $VersionKeycloak..." -ForegroundColor Cyan
            Invoke-WebRequest -Uri $UrlKeycloak -OutFile $ZipKeycloak -UseBasicParsing
        }
    }

    if (Test-Path -Path $TempKeycloak) { Remove-Item -Path $TempKeycloak -Recurse -Force }
    Write-Host "Extrayendo Keycloak en $RutaKeycloak..." -ForegroundColor Cyan
    Expand-Archive -Path $ZipKeycloak -DestinationPath $TempKeycloak -Force

    if (!(Test-Path -Path $RutaKeycloak)) { New-Item -ItemType Directory -Path $RutaKeycloak -Force | Out-Null }
    Copy-Item -Path "$TempKeycloak\keycloak-$VersionKeycloak\*" -Destination $RutaKeycloak -Recurse -Force
    Remove-Item -Path $TempKeycloak -Recurse -Force -ErrorAction SilentlyContinue
} else {
    Write-Host "Keycloak $VersionKeycloak ya está instalado en $RutaKeycloak. Omitiendo descarga e instalación." -ForegroundColor DarkGreen
}

# 14a. Escribir conf/keycloak.conf (HTTPS en 9090, hostname y MariaDB)
Write-Host "Configurando Keycloak con MariaDB en $IpMariaDB (usuario $DbUsername)..." -ForegroundColor Cyan
$ArchivoKeycloakConf = "$RutaKeycloak\conf\keycloak.conf"
$ContenidoKeycloakConf = @"
https-port=9090
https-certificate-file=C:/app/nginx/conf/ssl/$DominioKeycloak.pem
https-certificate-key-file=C:/app/nginx/conf/ssl/$DominioKeycloak-key.pem
hostname=$DominioKeycloak

db=mariadb
db-username=$DbUsername
db-password=$DbPassword
db-url=jdbc:mariadb://$IpMariaDB/keycloak
"@
[System.IO.File]::WriteAllText($ArchivoKeycloakConf, $ContenidoKeycloakConf, (New-Object System.Text.UTF8Encoding $false))

# 14b. Descargar Apache Commons Daemon (prunsrv.exe) y copiarlo a bin de Keycloak
$UrlCommonsDaemon     = "https://dlcdn.apache.org/commons/daemon/binaries/windows/commons-daemon-$VersionCommonsDaemon-bin-windows.zip"
$ZipCommonsDaemon     = "$env:TEMP\commons-daemon-$VersionCommonsDaemon-bin-windows.zip"
$TempCommonsDaemon    = "$env:TEMP\commons-daemon-$VersionCommonsDaemon-extract"
$PrunsrvExe           = "$RutaKeycloak\bin\prunsrv.exe"

if (!(Test-Path -Path $PrunsrvExe)) {
    if (!(Test-Path -Path $ZipCommonsDaemon)) {
        Write-Host "Descargando Apache Commons Daemon $VersionCommonsDaemon..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $UrlCommonsDaemon -OutFile $ZipCommonsDaemon -UseBasicParsing
    }

    if (Test-Path -Path $TempCommonsDaemon) { Remove-Item -Path $TempCommonsDaemon -Recurse -Force }
    Expand-Archive -Path $ZipCommonsDaemon -DestinationPath $TempCommonsDaemon -Force
    Copy-Item -Path "$TempCommonsDaemon\amd64\prunsrv.exe" -Destination $PrunsrvExe -Force
    Remove-Item -Path $TempCommonsDaemon -Recurse -Force -ErrorAction SilentlyContinue
} else {
    Write-Host "prunsrv.exe ya está instalado en $PrunsrvExe. Omitiendo descarga." -ForegroundColor DarkGreen
}

# 14c. Construir la distribución optimizada (opcional, acelera el primer arranque)
Write-Host "Construyendo Keycloak (kc.bat build)..." -ForegroundColor Cyan
Push-Location $RutaKeycloak
& "$RutaKeycloak\bin\kc.bat" build 2>&1 | Out-Null
Pop-Location

# 14d. Instalar el servicio con prunsrv.exe según la documentación oficial de Keycloak
Write-Host "Registrando el servicio $ServicioKeycloak con Apache Commons Daemon..." -ForegroundColor Cyan
Stop-Service -Name $ServicioKeycloak -Force -ErrorAction SilentlyContinue
if (Get-Service -Name $ServicioKeycloak -ErrorAction SilentlyContinue) {
    # Si existe una versión anterior del servicio (incluso creada con NSSM), se elimina
    & "$RutaKeycloak\bin\kc.bat" tools windows-service uninstall --name=$ServicioKeycloak 2>$null | Out-Null
}
if (Get-Service -Name $ServicioKeycloak -ErrorAction SilentlyContinue) {
    & sc.exe delete $ServicioKeycloak 2>$null | Out-Null
    Start-Sleep -Seconds 2
}

Push-Location $RutaKeycloak
& "$RutaKeycloak\bin\kc.bat" tools windows-service install --name=$ServicioKeycloak --display-name=Keycloak --stop-timeout=60 2>&1 | Out-Null
Pop-Location

Start-Service -Name $ServicioKeycloak -ErrorAction SilentlyContinue

# Esperar brevemente a que Keycloak escuche en 9090
for ($i = 0; $i -lt 30; $i++) {
    if (Get-NetTCPConnection -State Listen -LocalPort 9090 -ErrorAction SilentlyContinue) { break }
    Start-Sleep -Seconds 1
}

# 15. Reaplicar permisos tras las instalaciones y creación de archivos
icacls $RutaBase /grant "${UsuarioActual}:(OI)(CI)M" /T /C | Out-Null

# 16. Verificación de instalaciones y servicios
Write-Host "`n--- Verificando Java 25 y mkcert ---" -ForegroundColor Cyan
java -version
& $MkcertExe -version

Write-Host "`n--- Verificando Servicios (Tomcat 11, Nginx y Keycloak) ---" -ForegroundColor Cyan
Get-Service -Name "Tomcat*", "nginx", "keycloak" -ErrorAction SilentlyContinue

Write-Host "`nEntorno desplegado correctamente:" -ForegroundColor Green
Write-Host " -> Backend Tomcat 11 : https://$DominioTomcat" -ForegroundColor Green
Write-Host " -> Frontend SPA      : https://$DominioSPA (Archivos en $RutaNginx\html)" -ForegroundColor Green
Write-Host " -> Keycloak          : https://$DominioKeycloak" -ForegroundColor Green
