# ─────────────── STAGE 1: build the fat JAR ───────────────
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app

# cache only pom.xml to leverage Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copy sources and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─────────────── STAGE 2: run with slim JDK ───────────────
FROM openjdk:17-slim
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

ARG JAR_FILE=/app/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
COPY wait-for.sh .
RUN chmod +x wait-for.sh
# change 8080 if your service listens on a different port
EXPOSE 8082

ENTRYPOINT ["./wait-for.sh", "kafka:9092", "java", "-jar", "/app.jar"]
