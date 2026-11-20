# Deploy on production

## Tomcat Systemd (GNU/Linux)
- Create file:
```
touch /etc/systemd/system/tomcat.service
```
- Set this configuration:
```
[Unit]
Description=Tomcat
After=network.target

[Service]
Type=forking

User=roqui
Group=roqui

Environment="JAVA_HOME=/home/roqui/app/java-25"

Environment="CATALINA_BASE=/home/roqui/app/tomcat"
Environment="CATALINA_HOME=/home/roqui/app/tomcat"

# RoQui WAR configuration (placeholders in src/main/resources/application.properties)
Environment="KEY_CORS_ALLOWED_ORIGIN=https://client.vm.oe"
Environment="KEYCLOAK_ISSUER_URI=https://keycloak.vm.oe/realms/roqui-realm"
Environment="DB_ROQUI_NAME=donpos"
Environment="DB_ROQUI_USERNAME=donpos"
Environment="DB_ROQUI_PASSWORD=d"
Environment="KEY_PROPERTY=dieciseisnumeros"

ExecStart=/home/roqui/app/tomcat/bin/startup.sh
ExecStop=/home/roqui/app/tomcat/bin/shutdown.sh

RestartSec=18
Restart=always

[Install]
WantedBy=multi-user.target
```
- You can now load, enable, start, stop and restart your app by running the following as root.
```
systemctl daemon-reload
systemctl enable tomcat
systemctl start tomcat
systemctl stop tomcat
systemctl restart tomcat
```
## Reverse proxy in nginx
- Create file:
```
touch /etc/nginx/sites-available/tomcat
```
- Set this configuration:
```
server {

    listen       8001;

    server_name _;

    location / {
        proxy_pass http://127.0.0.1:8080;
    }
}
```
- Enable site:
```
ln -s /etc/nginx/sites-available/tomcat /etc/nginx/sites-enabled/tomcat
```
- Check configuration:
```
nginx -t
```
- Reload nginx
```
systemctl reload nginx.service
```

## Environment variables for the WAR (Tomcat)

`application.properties` uses placeholders such as `${KEY_CORS_ALLOWED_ORIGIN:...}` and
`${KEYCLOAK_ISSUER_URI:...}`. They are resolved at runtime from the environment of the JVM
process. When Tomcat runs as a service (systemd on Linux, Windows service on Windows) it
does **not** inherit variables exported in an interactive shell, so the default values
(`http://localhost:5173` and `http://localhost:9090/realms/roqui-realm`) are used silently.

| Variable | Example | Purpose |
| --- | --- | --- |
| `KEY_CORS_ALLOWED_ORIGIN` | `https://client.vm.oe` | Allowed SPA origin (scheme + host + optional port, no trailing slash) |
| `KEYCLOAK_ISSUER_URI` | `https://keycloak.vm.oe/realms/roqui-realm` | Must match exactly the `iss` claim of the JWT |
| `DB_ROQUI_NAME` | `donpos` | Database name |
| `DB_ROQUI_USERNAME` | `donpos` | Database user |
| `DB_ROQUI_PASSWORD` | `d` | Database password |
| `KEY_PROPERTY` | `dieciseisnumeros` | Key used to encrypt/decrypt stored secrets |

If Keycloak is published through nginx at `https://keycloak.vm.oe`, the issuer must be the
public URL (`https://keycloak.vm.oe/realms/roqui-realm`), not `http://localhost:9090/...`.

After changing the systemd unit, reload and restart:

```sh
systemctl daemon-reload
systemctl restart tomcat
```

### Windows service (Tomcat11)

`doc/windows/install.ps1` registers these variables in the service itself, so no manual step
is needed after running it. To configure them manually:

```powershell
& "C:\app\Tomcat11\bin\tomcat11.exe" //US//Tomcat11 `
  --Environment="KEY_CORS_ALLOWED_ORIGIN=https://client.vm.oe#KEYCLOAK_ISSUER_URI=https://keycloak.vm.oe/realms/roqui-realm"
Restart-Service Tomcat11
```

Alternatively, open `C:\app\Tomcat11\bin\tomcat11w.exe`, go to the **Environment** tab and add
each `name=value` pair. `bin\setenv.bat` only applies when Tomcat is started with
`startup.bat`/`catalina.bat`, not when it runs as a Windows service.

### Verify

```sh
curl -i -X OPTIONS https://tomcat.vm.oe/donpos/ping \
  -H "Origin: https://client.vm.oe" -H "Access-Control-Request-Method: GET"
```

`Access-Control-Allow-Origin` must be `https://client.vm.oe`. In `/v3/api-docs`, the
`keycloak` security scheme URLs must start with `https://keycloak.vm.oe/realms/roqui-realm`.
