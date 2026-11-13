# User Management API

A Spring Boot test with a REST API containing options to create, view, update, and delete users.

## Run

### Dependencies

- JDK 25
- Docker

Start the database:

```sh
docker compose up -d --wait
```

Start the API from the same terminal. Use the `MYSQL_PASSWORD` value from `.env` when prompted.

**Git Bash**

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/user_management'
export SPRING_DATASOURCE_USERNAME='user_management'
read -rsp 'MYSQL_PASSWORD: ' SPRING_DATASOURCE_PASSWORD
echo
export SPRING_DATASOURCE_PASSWORD
./mvnw spring-boot:run
```

**PowerShell**

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://127.0.0.1:3306/user_management'
$env:SPRING_DATASOURCE_USERNAME = 'user_management'
$env:SPRING_DATASOURCE_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'MYSQL_PASSWORD' -AsSecureString)).Password
.\mvnw.cmd spring-boot:run
```

Available at `http://127.0.0.1:8080`.
It provides `POST`, `GET`, `PUT`, and `DELETE` endpoints under `/users`.

## Build and test

```sh
./mvnw verify       # Git Bash
.\mvnw.cmd verify   # PowerShell
```
