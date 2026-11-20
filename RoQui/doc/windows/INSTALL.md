# Installation and configuration

## Install
```powershell
.\install.ps1 `
  -IpMariaDB "localhost" `
  -DbUsername "keycloak" `
  -DbPassword "k" `
  -DominioTomcat "tomcat.vm.oe" `
  -DominioSPA "client.vm.oe" `
  -DominioKeycloak "keycloak.vm.oe" `
  -DbRoquiName "donpos" `
  -DbRoquiUsername "donpos" `
  -DbRoquiPassword "d" `
  -KeyProperty "dieciseisnumeros"
```
The script registers the RoQui environment variables in the `Tomcat11` Windows service
(`KEY_CORS_ALLOWED_ORIGIN`, `KEYCLOAK_ISSUER_URI`, `DB_ROQUI_*` and `KEY_PROPERTY`), so
`application.properties` does not need to be edited for each environment. If Tomcat was
already installed, re-run the script (or configure the service manually as described in
`../PRODUCTION.md`) and restart the service.

## Keycloak
- Create temporal user
```powershell
.\bin\kc.bat bootstrap-admin user
```
- Restart keycloak service
- Login into keycloak url

## Tomcat
- Verify the environmnet variables
```powershell
cd C:\app\Tomcat11\bin
```
```powershell
.\tomcat11.exe //PS//Tomcat11
```

## RoQui
- Compile RoQui from `RoQui/` (`.\gradlew.bat bootWar`)
- Copy the generated WAR into `C:\app\Tomcat11\webapps` (the file name defines the context path,
  e.g. `donpos.war` -> `/donpos`)
- Verify the environment variables reached the JVM:
  ```powershell
  Invoke-WebRequest -Uri "https://tomcat.vm.oe/donpos/ping" 
  ```
  `Access-Control-Allow-Origin` must be `https://client1.vm.oe`. In `/v3/api-docs`, the
  `keycloak` security scheme URLs must start with
  `https://keycloak1.vm.oe/realms/roqui-realm`.

## RoQui GUI

## Uninstall
```
.\uninstall.ps1 -DominioTomcat tomcat1.vm.oe -DominioSPA client1.vm.oe -DominioKeycloak keycloak1.vm.oe
```
