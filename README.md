# File Uploader

This is project is backend implementation for File Uploader.

Uses

* Spring Boot 3
* Java 19
* Maven 3.8.x
* Postgres 14.2
* Docker

# Compile & Start locally

    # Execute in project root, unless otherwise states

    # To download packages, build and run tests
    $ ./mvnw clean install
    
    # Start postgres container
    $ scripts/setup-postgress-docker.sh

    # Before starting server you need to setup db
    # there is a script called create-db-using-docker-windows.sh which runs drop-db.sql 
    # and create-db-local.sql inside postgres docker container.
    # this works only on windows
    $ scripts/create-db-using-docker-windows.sh

    # To start dev server locally
    $ ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
    
    # one can also start application from FileUploaderApplication.java by using IDE
    # If running from IDE remember to add active profile

    # To run test
    $ ./mvnw clean verify
    
    # You can find coverage report from
    # target/site/jacoco/index.html

    # To get static analysis reports
    # (NOTE: First time it may take long time to download all dependencies) 
    $ ./mvnw site
    # Reports found
    #  - target/site/pmd.html
    #  - target/site/spotbugs.html
    #  - target/site/checkstyle.html
    # OR
    # target/site/index.html
    # and navigating to `Project Reports`

    # To generate DTOs as typescript to target/typescript-generator
    $ ./mvnw typescript-generator:generate
    
    # Build docker container
    $ docker build -t timo-laasonen/spring-boot-file-uploader-backend .

# Authentication

Local Keycloak is used to authenticate request to backend as this Rest API is stateless.

One can start local Keycloak from Docker image by using this command

`docker run -p 8081:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:17.0.1 start-dev`

Following this https://www.baeldung.com/spring-boot-keycloak-integration-testing guide Keycloak can be configured

In Keycloak _Valid Redirect URIs_ and _Web Origins_ must be set to http://localhost:3000

application.yml contains path to _oauth2 issuer-uri_ which is in local Keycloak based on defined Realm name like
http://localhost:8081/auth/realms/fileupload

client application uses http://localhost:8081 as authentication url, _frontend_ as clientId and _fileupload_ as realm name

# Integration test authentication

Integration tests are mocking Keycloak authentication by default. This is done by mocking JwtDecoder bean. 
There is also possibility to use Keycloak test container so that you take export from your local Keycloak settings
and use that export file when Keycloak test container is started on Docker. This way it is possible to test end to end behavior, 
but it is also much slower to run tests this way
