### About
Project Post is a microservice that provides additional API implementation for the Post application.

### Prerequisites

- JDK 17
- Apache Maven
- PostgreSQL

### Installation
Installation can be done executing `mvn clean install` with optional `-DskipTests=true` parameter for skipping tests.

### DB connection
Fill in your db connection credentials such as database url, username and password to [application.properties](sportdiary-core/src/main/resources/application.properties) file.

### Running
The project runs on Spring Boot and it can be run by clicking on the ► symbol in [PostServiceApplication](sportdiary-service/src/main/java/com/yoso/sportdiary/SportdiaryApplication.java) class.

Otherwise you can start the application with *Ctrl+Shift+F10* shortcut, or by running it from Run/Debug configuration panel(after first start).

```
docker run --name some-postgres -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d postgres
```
