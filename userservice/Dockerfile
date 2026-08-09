# ============================================================
# userservice/Dockerfile
# Exposes REST (8081) and gRPC (9090)
# ============================================================

# ------------------------------------------------------------
# Stage 1: Build the application with Maven
# ------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only the pom.xml first to leverage Docker layer caching —
# dependencies are re-downloaded only when pom.xml actually changes
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the rest of the source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------
# Stage 2: Run the built jar on a lightweight JRE image
# ------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the built jar from the build stage — keeps final image small
COPY --from=build /app/target/*.jar app.jar

# REST API port
EXPOSE 8081
# gRPC port
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
