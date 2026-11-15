FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S dyfi && adduser -S dyfi -G dyfi \
    && mkdir -p /app/uploads && chown -R dyfi:dyfi /app

COPY --from=build /workspace/target/dyfiwildlifecentre-1.0.jar /app/app.jar

USER dyfi
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=25s --retries=3 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
