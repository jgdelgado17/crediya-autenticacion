FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /builder
COPY ./crediya-autenticacion.jar .
RUN java -Djarmode=tools -jar crediya-autenticacion.jar extract --layers --destination extracted

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "crediya-autenticacion.jar"]