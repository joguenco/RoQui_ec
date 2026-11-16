# Httpie

# Authentication
```bash
TOKEN=$(http --form --body POST \
  http://localhost:9090/realms/roqui-realm/protocol/openid-connect/token \
  grant_type=password \
  client_id=roqui-client \
  username=checker \
  password=c | jq -r .access_token)
```
```fish
set TOKEN (http --form --body POST \
  http://localhost:9090/realms/roqui-realm/protocol/openid-connect/token \
  grant_type=password \
  client_id=roqui-client \
  username=checker \
  password=c | jq -r .access_token)
```
```shell
echo "$TOKEN"
```
```shell
http GET http://localhost:8080/roqui/v1/taxpayer "Authorization:Bearer $TOKEN"
```
Windows
```powershell
þ
```
```powershell
$TOKEN = $response.access_token
```
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/roqui/v1/taxpayer" `
  -Method Get `
  -Headers @{ Authorization = "Bearer $TOKEN" }
```