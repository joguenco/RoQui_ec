# Instalar RoQui API en IIS

En el ejemplo la aplicación queda en `C:\inetpub\wwwroot\RoQuiApi`.

Cada comando dice en qué PowerShell va y desde qué carpeta:

- **PowerShell Administrador**: Inicio → `PowerShell` → clic derecho → **Ejecutar como administrador**.
- **PowerShell normal**: abrirlo sin más.
- Si no dice carpeta, sirve desde cualquiera.

---

## 1. Instalar lo necesario

| Qué | Dónde | PowerShell | Cómo |
|---|---|---|---|
| IIS | servidor | Administrador | Windows 10/11: `Enable-WindowsOptionalFeature -Online -All -FeatureName IIS-WebServerRole, IIS-WebServer, IIS-ManagementConsole`<br>Windows Server: `Install-WindowsFeature -Name Web-Server -IncludeManagementTools` |
| .NET 10 Hosting Bundle | servidor | Administrador | `winget install Microsoft.DotNet.HostingBundle.10`<br>o [descargar](https://dotnet.microsoft.com/download/dotnet/10.0) → fila **Hosting Bundle** |
| .NET 10 SDK | PC donde se publica | Administrador | `winget install Microsoft.DotNet.SDK.10` |
| Entity Framework (`dotnet-ef`) | PC donde se publica | normal | `dotnet tool install --global dotnet-ef` |
| PostgreSQL 18 | servidor de base de datos | — | [descargar](https://www.postgresql.org/download/windows/) y ejecutar el instalador |

Después del Hosting Bundle. **PowerShell Administrador:**

```powershell
iisreset
```

Comprobar, tiene que decir `True`. **PowerShell normal:**

```powershell
Test-Path "C:\Program Files\IIS\Asp.Net Core Module\V2\aspnetcorev2.dll"
```

> Tiene que ser el **Hosting Bundle**. El SDK o el Runtime solos no instalan el
> módulo que usa IIS.

---

## 2. Crear la base de datos

1. Crear el usuario y la base `roqui`: ver [README](../../README.md#create-database-and-user-in-windows).
2. Crear las tablas, una sola vez.
   **PowerShell normal**, en la carpeta `RoQuiApi` del repositorio, la que tiene
   `RoQuiApi.csproj` (por ejemplo `C:\RoQui_ec\RoQuiApi`):

```powershell
cd C:\RoQui_ec\RoQuiApi
```

```powershell
dotnet ef database update --connection "Host=SERVIDOR;Port=5432;Database=roqui;Username=roqui;Password=CLAVE"
```

Las vistas y los datos iniciales los crea la API sola al arrancar.

---

## 3. Publicar

`publish` compila en `Release` y deja en la carpeta de IIS todo lo que hace falta.
(`dotnet build` sólo compila para desarrollo; no sirve para IIS.)

**PowerShell Administrador** (escribe en `C:\inetpub`), en la carpeta `RoQuiApi`
del repositorio, la que tiene `RoQuiApi.csproj`. Si no, sale el error
`MSB1003: Specify a project or solution file`:

```powershell
cd C:\RoQui_ec\RoQuiApi
```

Elegir **una**:

**Varios archivos** (recomendado)

```powershell
dotnet publish -c Release -o C:\inetpub\wwwroot\RoQuiApi
```

**Un solo archivo**

```powershell
dotnet publish -c Release -r win-x64 --self-contained false -p:PublishSingleFile=true -p:AspNetCoreHostingModel=OutOfProcess -o C:\inetpub\wwwroot\RoQuiApi
```

En los dos casos tienen que quedar en la carpeta: `web.config`, `appsettings.json`
y la carpeta `Sql`. Con un solo archivo, el programa es `RoQuiApi.exe`.

> La carpeta destino tiene que estar vacía o tener una publicación anterior.
> Nunca publicar sobre la carpeta del código fuente.

---

## 4. Configurar la conexión

Crea el archivo `appsettings.Production.json` con la conexión a PostgreSQL.
Cambiar `SERVIDOR` y `CLAVE` antes de ejecutarlo. **PowerShell Administrador:**

```powershell
@'
{
  "ConnectionStrings": {
    "DefaultConnection": "Host=SERVIDOR;Port=5432;Database=roqui;Username=roqui;Password=CLAVE"
  }
}
'@ | Set-Content -Encoding utf8 C:\inetpub\wwwroot\RoQuiApi\appsettings.Production.json
```

En IIS la API corre como `Production`, así que este archivo manda sobre
`appsettings.json`. Como no está en el proyecto, `dotnet publish` nunca lo borra
ni lo reemplaza: la conexión se configura una sola vez.

---

## 5. Registrar la aplicación en IIS

Para que IIS arranque la API sola desde que se enciende la PC, sin `dotnet run`.
Se hace una sola vez. **PowerShell Administrador**, los cuatro:

```powershell
C:\Windows\System32\inetsrv\appcmd.exe add apppool /name:RoQuiApi /managedRuntimeVersion:""
```

```powershell
C:\Windows\System32\inetsrv\appcmd.exe add app /site.name:"Default Web Site" /path:/RoQuiApi /physicalPath:"C:\inetpub\wwwroot\RoQuiApi" /applicationPool:RoQuiApi
```

```powershell
icacls "C:\inetpub\wwwroot\RoQuiApi" /grant "IIS AppPool\RoQuiApi:(OI)(CI)(RX)" /T
```

```powershell
New-Item -ItemType Directory -Force C:\inetpub\wwwroot\RoQuiApi\logs
icacls "C:\inetpub\wwwroot\RoQuiApi\logs" /grant "IIS AppPool\RoQuiApi:(OI)(CI)M" /T
```

| # | Qué hace |
|---|---|
| 1 | Crea el grupo de aplicaciones **sin código administrado** (`""`). Con `v4.0` no arranca |
| 2 | Crea la aplicación `/RoQuiApi` dentro del sitio por defecto |
| 3 | Da permiso de lectura al grupo de aplicaciones |
| 4 | Crea la carpeta `logs` y le da permiso de escritura, para el log |

Otras formas de registrarla (con el Administrador de IIS, en un sitio propio con
su puerto, etc.), en la documentación oficial de Microsoft:

- [Publicar una aplicación ASP.NET Core en IIS](https://learn.microsoft.com/aspnet/core/tutorials/publish-to-iis)
- [Hospedar ASP.NET Core en Windows con IIS](https://learn.microsoft.com/aspnet/core/host-and-deploy/iis/)

---

## 6. Comprobar

**PowerShell normal:**

```powershell
curl.exe http://localhost/RoQuiApi/Ping
```

Tiene que responder `{"message":"Pong"}`.

```powershell
curl.exe -H "X-API-KEY: TU_CLAVE" http://localhost/RoQuiApi/Version
```

Tiene que responder la versión de la API y la de PostgreSQL. Si responde, la API
llega a la base de datos.

---

## 7. Cambiar la API key

La que viene por defecto está en el código. En producción hay que cambiarla.
No va en PowerShell: en **PostgreSQL** (psql o pgAdmin), conectado a la base `roqui`:

```sql
UPDATE ele_parameters SET value = 'NUEVA_CLAVE' WHERE name = 'RoQui HTTP X-API-KEY';
```

El sistema que llama a la API tiene que usar la misma clave.

---

## Actualizar a una versión nueva

1. Publicar otra vez con el mismo comando del paso 3: **PowerShell Administrador**,
   en la carpeta que tiene `RoQuiApi.csproj`.
2. Reiniciar la aplicación. **PowerShell Administrador:**

```powershell
C:\Windows\System32\inetsrv\appcmd.exe recycle apppool /apppool.name:RoQuiApi
```

> Si al publicar sale que el archivo está en uso, primero detener la aplicación,
> publicar y volver a arrancarla. **PowerShell Administrador:**
>
> ```powershell
> C:\Windows\System32\inetsrv\appcmd.exe stop apppool /apppool.name:RoQuiApi
> ```
>
> ```powershell
> C:\Windows\System32\inetsrv\appcmd.exe start apppool /apppool.name:RoQuiApi
> ```

---

## Si algo falla

| Error | Causa |
|---|---|
| `500.19` | Falta el Hosting Bundle, falta el `iisreset` o faltan los permisos del paso 5 |
| `500.30` | La API arrancó y se cayó: casi siempre la conexión a la base de datos o falta la carpeta `Sql` |
| `404` en `/RoQuiApi/Ping` | No se registró la aplicación (paso 5) |
| `401` | Falta la cabecera `X-API-KEY` o la clave no coincide |
| `Access denied` | PowerShell no está como Administrador |
| `Cannot read configuration file due to insufficient permissions` (al usar `appcmd`) | PowerShell no está como Administrador: Inicio → `PowerShell` → clic derecho → **Ejecutar como administrador** |
| `Failed to add duplicate collection element` (al usar `appcmd`) | El grupo o la aplicación ya existen. No es un error: seguir con el paso siguiente |
| `MSB1003: Specify a project or solution file` | No se está en la carpeta que tiene `RoQuiApi.csproj` (paso 3) |

Ver el motivo de un `500.30`. **PowerShell normal:**

```powershell
Get-EventLog -LogName Application -Newest 3 -Source "IIS AspNetCore Module V2" | Format-List TimeGenerated, Message
```

También queda en `C:\inetpub\wwwroot\RoQuiApi\logs`.

---

## El orden completo

```
1. Instalar lo necesario          ← una vez
2. Crear la base de datos         ← una vez
3. dotnet publish                 ← cada versión nueva (copia los archivos)
4. Configurar la conexión         ← una vez
5. Registrar la aplicación en IIS ← una vez (IIS la arranca sola desde que se enciende la PC)
6. Comprobar con /Ping
```

**Cuando hay un cambio en RoQui API**, sólo se repite esto:

1. Paso 3: `dotnet publish`. **PowerShell Administrador**, en la carpeta que tiene
   `RoQuiApi.csproj`.
2. Reiniciar la aplicación. **PowerShell Administrador:**

```powershell
C:\Windows\System32\inetsrv\appcmd.exe recycle apppool /apppool.name:RoQuiApi
```

3. Paso 6: comprobar con `/Ping`. **PowerShell normal.**

Los demás pasos no se repiten. La conexión queda en `appsettings.Production.json`
(paso 4), que el `publish` no toca.
