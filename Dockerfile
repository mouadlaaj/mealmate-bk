FROM maven:3.9.8-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8001

CMD ["java", "-jar", "target/MealMate-0.0.1-SNAPSHOT.jar"]
