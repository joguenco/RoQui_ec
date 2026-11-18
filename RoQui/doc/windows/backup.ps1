#Requires -RunAsAdministrator

# --- CONFIGURACIÓN DE RUTAS ---
$RutaBase       = "D:\Herramientas"
$CarpetaBackup  = "D:\Backups_Servidor"
$Fecha          = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$DestinoTemp    = "$CarpetaBackup\Temp_$Fecha"
$ArchivoZip     = "$CarpetaBackup\Respaldo_Entorno_$Fecha.zip"
$ArchivoHosts   = "$env:SystemRoot\System32\drivers\etc\hosts"

if (!(Test-Path -Path $RutaBase)) {
    Write-Host "No se encontró la carpeta $RutaBase. Nada que respaldar." -ForegroundColor Yellow
    exit
}

# 1. Crear directorio de respaldos
if (!(Test-Path -Path $CarpetaBackup)) {
    New-Item -ItemType Directory -Path $CarpetaBackup -Force | Out-Null
}
New-Item -ItemType Directory -Path $DestinoTemp -Force | Out-Null

# 2. Detener servicios temporalmente para evitar archivos bloqueados (logs/webapps)
Write-Host "Deteniendo servicios Nginx y Tomcat..." -ForegroundColor Cyan
Stop-Service -Name "nginx", "Tomcat*" -Force -ErrorAction SilentlyContinue

# 3. Copiar carpeta Herramientas y archivo hosts al directorio temporal
Write-Host "Copiando archivos de configuración, certificados y aplicaciones..." -ForegroundColor Cyan
Copy-Item -Path $RutaBase -Destination "$DestinoTemp\Herramientas" -Recurse -Force
Copy-Item -Path $ArchivoHosts -Destination "$DestinoTemp\hosts.bak" -Force

# 4. Reiniciar servicios inmediatamente para minimizar inactividad
Write-Host "Reiniciando servicios..." -ForegroundColor Cyan
Start-Service -Name "Tomcat*", "nginx" -ErrorAction SilentlyContinue

# 5. Comprimir respaldo y limpiar carpeta temporal
Write-Host "Comprimiendo respaldo en $ArchivoZip..." -ForegroundColor Cyan
Compress-Archive -Path "$DestinoTemp\*" -DestinationPath $ArchivoZip -CompressionLevel Optimal -Force
Remove-Item -Path $DestinoTemp -Recurse -Force

Write-Host "`nRespaldo completado exitosamente en: $ArchivoZip" -ForegroundColor Green
