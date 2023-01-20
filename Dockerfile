FROM openjdk:18-jdk-alpine
LABEL org.opencontainers.image.description="fileuploader backend component"

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/spring-boot-file-uploader-*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
