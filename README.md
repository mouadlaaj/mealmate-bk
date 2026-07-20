# MealMate Backend

Spring Boot REST API for MealMate.

## Requirements

- Java 17
- MySQL on port `3306`

## Run locally

Start MySQL from the project root:

```powershell
docker compose up -d mysql
```

Set the Groq API key in the backend terminal:

```powershell
$env:GROQ_API_KEY="your-groq-api-key"
```

Then start the backend:

```powershell
.\mvnw.cmd spring-boot:run
```

The API runs on `http://localhost:8001`.

Swagger UI:

```text
http://localhost:8001/swagger-ui/index.html
```

## Database defaults

```text
Database: mealmate
Username: root
Password: password
Port:     3306
```

## Run tests

```powershell
.\mvnw.cmd test
```

## Run with Docker

From the project root:

```powershell
docker compose up -d --build
```
