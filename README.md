# Clean Architecture for Spring

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/AntonShapovalov/Clean-Architecture-Spring/actions/workflows/push-artifacts.yml)
[![Test Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)](https://github.com/AntonShapovalov/Clean-Architecture-Spring/wiki/Test-coverage)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

The project demonstrates `how to` use Spring Boot, Clean Architecture, and Kotlin Coroutines to build a reactive web application.

The application searches for movies using the external [OMDB API](https://www.omdbapi.com), saves the search history to a local database, and serves the stored results on the next visit. Repeated searches are answered from the database and refreshed from the API only when the saved search is older than one month.

The backend is a reactive Spring Boot service written in Kotlin (WebFlux, Coroutines, R2DBC over an in-memory H2 database). The frontend is an Angular application with a search history sidebar and a movie grid. Because H2 runs in memory, the search history is reset when the backend restarts.

### Install and Run
For simplicity and demonstration purposes, the project contains both `backend` and `frontend` modules in one repository. [Docker](https://docs.docker.com/get-started/get-docker/) Compose starts both services and exposes the frontend via default port 80.
For local development, the Spring Boot backend and Angular frontend can be started using an IDE or command line.

#### Docker Compose
* Clone the repository
* Start the application:
```shell
docker compose up --build
```
* Open the browser and navigate to [http://localhost:80](http://localhost:80)

Compose packs the release artifacts from the `release` directory, so it does not need Gradle, npm, or JDK installed. The nginx container serves the frontend and proxies `/api` calls to the backend container.

#### Prerequisites for the manual run
The steps below, unlike Docker Compose, build the application from sources and require:
* JDK 17 or newer
* Node.js 22 and npm, to run the frontend from sources
* An OMDB API key, free of charge from [omdbapi.com](https://www.omdbapi.com/apikey.aspx)

The backend reads the key from `backend/src/main/resources/secrets.properties`, which is not stored in the repository. Create it before running the backend from sources:
```properties
omdb.api-key=<your key>
```
The application starts without this file, but every call to the OMDB API fails.

#### Run Spring backend via IDE
* Open the project in your IDE and run the `backend/src/main/kotlin/clean/architecture/omdb/OmdbApplication.kt` class
* Navigate to [http://localhost:8080](http://localhost:8080)

The same can be done from the terminal, in the repository root:
```shell
./gradlew bootRun
```

#### Run Angular frontend via the terminal
* Open a terminal window and navigate to the `frontend` folder
* Install the dependencies:
```shell
npm ci
```
* Run the dev server:
```shell
ng serve 
```
* Click the link to open a browser and navigate to [http://localhost:4200](http://localhost:4200)

The dev server proxies `/api` requests to [http://localhost:8080](http://localhost:8080), so the backend has to be running as well.

#### Build and test
```shell
./gradlew build detekt    # backend, compile and run all tests and static analysis
npm run lint              # frontend, in the "frontend" folder
npm test                  # frontend, in the "frontend" folder
```

### API Specification
The API specification is available via Swagger UI when the backend is running: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
You can also find the static specification file here: [openapi/openapi.yaml](openapi/openapi.yaml)

The file is generated from the source code by the `./gradlew generateOpenApiDocs` task and updated by the CI pipeline, so it should not be edited manually.

Errors are returned as [Problem Details](https://datatracker.ietf.org/doc/html/rfc7807) with the `application/problem+json` content type, and every error response includes a timestamp and a request id.

### Key Points
Overall, the application architecture provides a solid foundation for building clean and maintainable code. It emphasizes the importance of separating concerns, isolating core business logic, and making the code testable and reusable:
* The business logic is isolated in the domain layer 
* The domain is independent of any external frameworks
* Fast unit tests
* The data layer as a single source of truth
* Unidirectional data flow
* Single Responsibility Principle for every class and layer
* The data layer only provides data
* The domain layer applies data transformations and business rules
* The presentation layer only displays data and passes user input to the domain layer

The practical benefit is that every layer can be changed on its own. The database and the OMDB API are hidden behind repositories, so replacing H2 with a persistent database, or OMDB with another movie provider, does not touch a single business rule. The rules themselves live in use cases that know nothing about HTTP, Spring, or SQL, and can be verified in milliseconds without starting the application context.

#### Test Coverage
The layered structure enables testing of each layer independently. The domain layer can be completely covered with fast and isolated unit tests. Data and presentation layers can be additionally covered with integration and end-to-end tests. 
In this way, the [test coverage](https://github.com/AntonShapovalov/Clean-Architecture-Spring/wiki/Test-coverage) of the application can reach 100% while keeping tests fast, simple, and reliable.

#### Clean Architecture Layers
Unlike Android, which does not define application layers by default, Spring provides predefined stereotypes that can be used to organize the three main layers: data, domain, and presentation.

* The data layer is typically implemented using `@Repository` classes
* The domain layer is typically implemented using `@Service` classes
* The presentation layer is typically implemented using `@Controller` classes

However, placing all business logic in service classes can make them complex and difficult to test. Instead, business logic and data transformations should be encapsulated in dedicated use case classes. This approach keeps services lightweight and makes the business logic easier to cover with fast unit tests.

In this project the layers are organized as follows:

* `controller` - REST controllers, they only validate the incoming request and return the result
* `service` - a thin bridge between controllers and use cases, it also switches coroutine dispatchers
* `domain` - use cases with all business rules, and domain models, no framework dependencies
* `data` - repositories as a facade over the local database and the remote API, with mappers that convert entities and API responses to domain models
