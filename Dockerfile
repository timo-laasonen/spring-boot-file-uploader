FROM eclipse-temurin:18-jdk-alpine AS builder
WORKDIR /app
COPY mvnw .
COPY lombok.config .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN chmod +x mvnw && ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)


FROM eclipse-temurin:18-jre-alpine
LABEL org.opencontainers.image.description="file-uploader backend component"
VOLUME /tmp
ARG DEPENDENCY=/app/target/dependency
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","fi.fileuploader.FileUploaderApplication"]
