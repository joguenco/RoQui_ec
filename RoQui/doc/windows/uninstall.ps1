#Requires -RunAsAdministrator

param(
    [string]$DominioTomcat = "tomcat.vm.oe",
    [string]$DominioSPA = "client.vm.oe",
    [string]$DominioKeycloak = "keycloak.vm.oe"
)

# --- CONFIGURACIÓN DE RUTAS Y DOMINIOS ---
$RutaBase       = "C:\app"
$RutaJava       = "$RutaBase\Java\Temurin25"
$RutaTomcat     = "$RutaBase\Tomcat11"
$RutaNginx      = "$RutaBase\nginx"
$RutaKeycloak   = "$RutaBase\keycloak"
$RutaBin        = "$RutaBase\bin"
$MkcertExe      = "$RutaBin\mkcert.exe"
$ServicioTomcat = "Tomcat11"
$ServicioNginx  = "nginx"
$ServicioKeycloak = "keycloak"
$NssmExe        = "$RutaNginx\nssm.exe"
$ArchivoHosts   = "$env:SystemRoot\System32\drivers\etc\hosts"

Write-Host "Iniciando desinstalación limpia del entorno..." -ForegroundColor Yellow

# 1. Detener servicios si siguen activos
Stop-Service -Name $ServicioNginx, $ServicioTomcat, $ServicioKeycloak -Force -ErrorAction SilentlyContinue

# 2. Eliminar el servicio de Windows de Tomcat (instalado manualmente en $RutaTomcat)
if (Get-Service -Name $ServicioTomcat -ErrorAction SilentlyContinue) {
    Write-Host "Eliminando servicio $ServicioTomcat..." -ForegroundColor Cyan
    if (Test-Path -Path "$RutaTomcat\bin\service.bat") {
        & "$RutaTomcat\bin\service.bat" remove $ServicioTomcat 2>$null | Out-Null
    } else {
        & sc.exe delete $ServicioTomcat 2>$null | Out-Null
    }
}

# 3. Eliminar el servicio de Windows de Nginx (registrado con NSSM)
if (Get-Service -Name $ServicioNginx -ErrorAction SilentlyContinue) {
    Write-Host "Eliminando servicio $ServicioNginx..." -ForegroundColor Cyan
    if (Test-Path -Path $NssmExe) {
        & $NssmExe remove $ServicioNginx confirm 2>$null | Out-Null
    } else {
        & sc.exe delete $ServicioNginx 2>$null | Out-Null
    }
}

# 4. Eliminar el servicio de Windows de Keycloak (Apache Commons Daemon / prunsrv.exe)
if (Get-Service -Name $ServicioKeycloak -ErrorAction SilentlyContinue) {
    Write-Host "Eliminando servicio $ServicioKeycloak..." -ForegroundColor Cyan
    if (Test-Path -Path "$RutaKeycloak\bin\kc.bat") {
        & "$RutaKeycloak\bin\kc.bat" tools windows-service uninstall --name=$ServicioKeycloak 2>$null | Out-Null
    }
}
if (Get-Service -Name $ServicioKeycloak -ErrorAction SilentlyContinue) {
    & sc.exe delete $ServicioKeycloak 2>$null | Out-Null
    Start-Sleep -Seconds 2
}
$RutaRegistroKeycloak = "HKLM:\SOFTWARE\WOW6432Node\Apache Software Foundation\ProcRun 2.0\$ServicioKeycloak"
if (Test-Path -Path $RutaRegistroKeycloak) {
    Remove-Item -Path $RutaRegistroKeycloak -Recurse -Force -ErrorAction SilentlyContinue
}

# 5. Desinstalar la Autoridad de Certificación (CA) local de mkcert antes de quitar el binario
if (Test-Path -Path $MkcertExe) {
    Write-Host "Eliminando certificado raíz local de mkcert..." -ForegroundColor Cyan
    & $MkcertExe -uninstall
}

# 6. Limpiar dominios locales del archivo hosts de Windows
if (Test-Path -Path $ArchivoHosts) {
    Write-Host "Limpiando dominios en el archivo hosts..." -ForegroundColor Cyan
    $LineasLimpias = Get-Content -Path $ArchivoHosts | Where-Object {
        ($_ -notmatch [regex]::Escape($DominioTomcat)) -and
        ($_ -notmatch [regex]::Escape($DominioSPA)) -and
        ($_ -notmatch [regex]::Escape($DominioKeycloak))
    }
    Set-Content -Path $ArchivoHosts -Value $LineasLimpias -Encoding ASCII
}

# 7. Eliminar variables de entorno de Java y Tomcat a nivel de sistema
Write-Host "Eliminando variables JAVA_HOME, CATALINA_HOME y CATALINA_BASE..." -ForegroundColor Cyan
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", $null, "Machine")
[System.Environment]::SetEnvironmentVariable("CATALINA_HOME", $null, "Machine")
[System.Environment]::SetEnvironmentVariable("CATALINA_BASE", $null, "Machine")

$PathMaquina = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
if ($PathMaquina) {
    $PathLimpio = ($PathMaquina -split ';' | Where-Object { $_ -and $_ -ne "$RutaJava\bin" -and $_ -ne "$RutaBin" }) -join ';'
    [System.Environment]::SetEnvironmentVariable("Path", $PathLimpio, "Machine")
}

# 8. Eliminar Java, Tomcat, Nginx, Keycloak y la carpeta raíz con archivos residuales
foreach ($Directorio in @($RutaJava, $RutaTomcat, $RutaNginx, $RutaKeycloak, $RutaBin, $RutaBase)) {
    if (Test-Path -Path $Directorio) {
        Write-Host "Eliminando directorio $Directorio..." -ForegroundColor Cyan
        Remove-Item -Path $Directorio -Recurse -Force -ErrorAction SilentlyContinue
    }
}

Write-Host "`nDesinstalación limpia completada. Reinicia la terminal para refrescar las variables de entorno." -ForegroundColor Green
