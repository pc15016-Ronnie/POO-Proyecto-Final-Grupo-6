FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S -g 10001 spring && adduser -S -D -H -u 10001 -G spring spring
WORKDIR /app

COPY --from=builder /build/target/eventos-api.jar /app/eventos-api.jar

RUN chown -R spring:spring /app
USER 10001:10001

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=5 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/eventos-api.jar"]
