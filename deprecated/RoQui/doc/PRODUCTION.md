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

Environment="JAVA_HOME=/home/roqui/app/java-21"

Environment="CATALINA_BASE=/home/roqui/app/tomcat"
Environment="CATALINA_HOME=/home/roqui/app/tomcat"

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