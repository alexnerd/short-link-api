# short-link-api

The purpose of the application is to convert a long link into a short one. Application supports multithreading environment, 
you can run as many instance of application at the same time as you want.
The PostgresSQL database is used to store long link - short link bindings.

This project uses Quarkus, the Supersonic Subatomic Java Framework https://quarkus.io/ 

## Running application

### Setting up database
First at all you need running PostgresSQL. You can run it as standalone service or as docker container it's your choice.
To run PostgresSQL as docker container, download it from dockerhub:
```shell script
docker pull postgres
```
And run it:
```shell script
docker run -p 5432:5432 --name local-postgres -e POSTGRES_PASSWORD=mysecretpassword -d postgres
```
run docker script uses parameter POSTGRES_PASSWORD described in the next block, and they should equal

### Description for application parameters
To run this application you must specify some parameters in application.properties file or used in maven parameters when 
start application (via -D param):
 - quarkus.datasource.jdbc.url - url for database connection (for example: jdbc:postgresql://localhost:5432/postgres)
 - quarkus.datasource.username - username for connection to database (for example: postgres)
 - quarkus.datasource.password - password for connection to database (for example: mysecretpassword)
 - application.host - return this link when short link not found (for example: https://localhost:8080)
Other useful parameters:
 - default.link-length - max short link length (default 9 symbols)
 - default.retry-num - the maximum number of attempts for creating short link after which throws exception (default 5 retry, exception may acquire if generated sequince already exists in database)
 - default.job-scheduler.month - if the link is not used for the specified number of months, then it is deleted (default 12 month)

If you start application in docker image, you can specify this params via environment variables.

## Running the application in dev mode

You can run this application in dev mode that enables live coding using maven:
```shell script
./mvnw quarkus:dev -Dquarkus.datasource.username=postgres -Dquarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/postgres -Dquarkus.datasource.password=mysecretpassword -Dapplication.host=https://localhost:8080  
```
You must use your own parameters like database connection url, password, etc

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

And again, you must specify application params when you run it, for example: `java -jar 
-Dquarkus.datasource.username=postgres -Dquarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/postgres 
-Dquarkus.datasource.password=mysecretpassword -Dapplication.host=https://localhost:8080 target/quarkus-app/quarkus-run.jar ` 

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/short-link-api-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Provided API

Application provides REST API for working with it.

### REST endpoints

**************************
POST /short-link  - creates short link, if long link exists, then just return it
**************************

Request
```shell script
curl -X POST --location "http://localhost:8080/link/short-link" \
-H "Content-Type: application/json" \
-d "{
\"redirectLink\": \"https://alexnerd.com\"
}"
```

Response
```shell script
HTTP/1.1 201 Created
Location: http://localhost:8080/link/SvvRMJze2I-T
content-length: 0
```

**************************
GET  /{shortLink} - redirects from short link to long, if long link not exists - redirect to default
**************************

Request
```shell sxript
curl -X GET --location "http://localhost:8080/link/{shortLink}"
```

Response
```shell script
HTTP/1.1 201 Created
Location: http://localhost:8080/link/SvvRMJze2I-T
content-length: 0
```
