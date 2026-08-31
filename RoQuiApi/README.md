## Create Project
```
dotnet new webapi --use-controllers -o RoQuiApi
```

### Create database and user in Windows
```
psql
```
```
CREATE ROLE roqui WITH LOGIN NOSUPERUSER CREATEDB NOCREATEROLE INHERIT NOREPLICATION CONNECTION LIMIT -1 PASSWORD 'r';
```
```
psql -d postgres -U roqui -W
```
```
create database roqui;
```
### Create database on GNU/Linux and MacOS with Postgres.app
In Linux
```
sudo su - postgres
```
```
psql
```
```
CREATE ROLE roqui WITH LOGIN NOSUPERUSER CREATEDB NOCREATEROLE INHERIT NOREPLICATION CONNECTION LIMIT -1 PASSWORD 'r';
```
```
psql -d roqui -U roqui -W
```
```
create database roqui;
```

## Format
```
dotnet format
```
## Migrate to database
```
dotnet ef migrations add init
```
```
dotnet ef database update
```
## Undo migration and remove
```
dotnet ef database update 0
```
```
dotnet ef migrations remove
```

## Run
```
dotnet run --launch-profile https
```
## Hot Reload
```
dotnet watch --launch-profile https
```
