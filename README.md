# User Management API

A Spring Boot test with a REST API containing options to create, view, update, and delete users.

## Run

### Dependencies

- JDK 25
- Docker

**Git Bash**

```bash
bash start-db.sh
./mvnw spring-boot:run
```

**PowerShell**

```powershell
.\start-db.ps1
.\mvnw.cmd spring-boot:run
```

Available at `http://127.0.0.1:8080`.
It provides `POST`, `GET`, `PUT`, and `DELETE` endpoints under `/users`.

## Swagger

Open at /swagger-ui.html.

## Build and test

```sh
./mvnw verify       # Git Bash
.\mvnw.cmd verify   # PowerShell
```
