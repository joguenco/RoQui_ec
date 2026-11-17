# Mariadb 12.3
## Show collation
```
SHOW VARIABLES LIKE 'collation%';
```
## Configure in C:\Program Files\MariaDB 12.3\data\my.ini
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
## Show collation tables
```
SELECT table_name, table_collation 
FROM information_schema.tables 
WHERE table_schema = 'schema_name'
ORDER BY table_name;
```