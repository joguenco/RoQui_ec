# Install
```powershell
.\install.ps1 `
  -IpMariaDB "192.168.56.1" `
  -DbUsername "keycloak" `
  -DbPassword "k" `
  -DominioTomcat "tomcat1.vm.oe" `
  -DominioSPA "client1.vm.oe" `
  -DominioKeycloak "keycloak1.vm.oe"
```
# Uninstall
```
.\uninstall.ps1 -DominioTomcat tomcat1.vm.oe -DominioSPA client1.vm.oe -DominioKeycloak keycloak1.vm.oe
```