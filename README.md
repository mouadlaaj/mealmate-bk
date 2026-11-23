# MealMate Backend

Spring Boot REST API for MealMate. It requires Java 17 and MySQL 8.4. The API listens on port 8001.

## Local configuration

Create a private `.env` beside `docker-compose.yml` for Docker Compose. Fill these entries on your machine using a password manager. Generate a different random value for each password. For `JWT_SECRET`, generate 64 random bytes and Base64-encode them for HS512. Keep this key stable across restarts and all backend instances.

```dotenv
MYSQL_ROOT_PASSWORD=
SPRING_DATASOURCE_USERNAME=mealmate
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
GROQ_API_KEY=
```

Groq is optional and enables recipe imports. Leave its entry empty to run the other features. The application uses the dedicated MySQL user above. For an existing database volume, create or update that user in MySQL, grant access to the `mealmate` schema and rotate the root password in MySQL before restarting. The container sets initial credentials only on a new data directory.

The Groq key in an early commit was revoked before that commit and was included as a classroom example. To enable recipe imports, create your own key in the Groq Console and set `GROQ_API_KEY` in your local `.env` file or shell. Keep your key out of Git.

## Run with Docker

Clone `mealmate-ui` next to this backend checkout. From the backend directory run:

```powershell
docker compose config --quiet
docker compose up -d --build
```

If you keep the Compose file in a different directory, set `MEALMATE_BACKEND_CONTEXT` and `MEALMATE_FRONTEND_CONTEXT` to the two checkout paths.

## Run the backend directly

Start the database with `docker compose up -d mysql`. Set `SPRING_DATASOURCE_PASSWORD` and `JWT_SECRET` in the terminal or IDE that starts Java, plus `SPRING_DATASOURCE_USERNAME` if you changed the username. Set `GROQ_API_KEY` there if using recipe imports. Compose reads the local `.env` file automatically. A standalone Java process needs the variables in its environment.

```powershell
.\mvnw.cmd spring-boot:run
```

Swagger UI is available at `http://localhost:8001/swagger-ui/index.html`. Run the tests with `.\mvnw.cmd test` in a test environment.

## Rotating an exposed key

Replace the JWT key on every backend instance and restart the service. Users must sign in again. Revoke exposed Groq keys in the Groq console and review usage. If a backend image was built with a real `.env` in the build directory, rebuild it from this configuration and retire the affected image.
