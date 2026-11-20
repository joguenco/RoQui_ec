# Instalation and configuration

## Install
```powershell
.\install.ps1 `
  -IpMariaDB "192.168.56.1" `
  -DbUsername "keycloak" `
  -DbPassword "k" `
  -DominioTomcat "tomcat1.vm.oe" `
  -DominioSPA "client1.vm.oe" `
  -DominioKeycloak "keycloak1.vm.oe"
```
## Keycloak
- Create temporal user
```powershell
.\bin\kc.bat bootstrap-admin user
```
- Restart keycloak service
- Login into keycloak url
## RoQui
- Configure application.properties
- Compile RoQui
- Copy .war in C:\app\Tomcat11\webapps
## RoQui GUI

## Uninstall
```
.\uninstall.ps1 -DominioTomcat tomcat1.vm.oe -DominioSPA client1.vm.oe -DominioKeycloak keycloak1.vm.oe
```