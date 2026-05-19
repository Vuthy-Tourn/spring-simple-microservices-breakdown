FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

COPY build.gradle ./
COPY settings.gradle ./
COPY src ./src

RUN gradle build --no-daemon -x test

FROM eclipse-temurin:21-jdk
WORKDIR /app

RUN apt-get update \
    && apt-get install -y curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
