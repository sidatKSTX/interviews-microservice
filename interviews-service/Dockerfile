# Stage 1: Build the application
FROM openjdk:21-slim AS build
WORKDIR /interviews-service

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight runtime image
FROM openjdk:21-slim
WORKDIR /interviews-service
COPY --from=build /interviews-service/target/*.jar interviews-service.jar

# Use a minimal command to run the application
ENTRYPOINT ["java", "-jar", "interviews-service.jar"]
