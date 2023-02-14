FROM eclipse-temurin:18-jre-alpine AS builder
WORKDIR app
ARG JAR_FILE=target/spring-boot-file-uploader-*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:18-jre-alpine
LABEL org.opencontainers.image.description="omasuuntima backend component"
WORKDIR app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=builder app/application/ ./
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
RUN chown -R spring:spring .
USER spring:spring
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
