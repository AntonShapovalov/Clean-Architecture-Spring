# Clean Architecture for Spring
The project demonstrates `how to` use Spring Boot, Clean Architecture, and Kotlin Coroutines to build reactive web application.

> [!NOTE]
> Work in progress...
> App is going to search for movies using external [OMDB API](https://www.omdbapi.com) and save search results in the database.

### Install and run
For simplicity and demostration purposes, the project contains both `backend` and `frontend` modules in one repository. [Docker](https://docs.docker.com/get-started/get-docker/) compose starts both services and exposes the frontend via default port 80.
For local development, Spring Boot backend and Angular frontend could be started using IDE or command line.

#### Docker compose
* Clone the repository
* Start the application:
```shell
docker compose up --build
```
* Open the browser and navigate to [http://localhost](http://localhost)

#### Run Spring backend via IDE
* Open the project in IDE and run the `backend/src/main/kotlin/clean/architecture/omdb/OmdbApplication.kt` class
* Navigate to [http://localhost:8080](http://localhost:8080)

#### Run Angular frontend via terminal
* Open terminal window and navigate to `frontend` folder
* Run dev server:
```shell
ng serve 
```
* Click the link to open a browser and navigate to [http://localhost:4200](http://localhost:4200)

### Key points
Overall, the application architecture provides a solid foundation for building a clean and maintainable code. It emphasizes the importance of separating concerns, isolating the core business logic, and making the code testable and reusable:
* the business logic is isolated in the domain layer 
* domain is independent of any external frameworks
* fast unit tests
* data layer as a single source of truth
* unidirectional data flow
* single responsibility principle for every class and layer
* data layer only provides data
* domain layer applies data transformations and business rules
* the presentation layer only displays data and passes user input to the domain layer

#### Test coverage
The layered structure of the application allows testing of each layer independently. The domain layer can be completely covered with fast and isolated unit tests. Data and presentation layers could be additionally covered with integration and end-to-end tests. 
In this way the [test coverage](https://github.com/AntonShapovalov/Clean-Architecture-Spring/wiki/Test-coverage) of the application can reach 100% with keeping tests fast, simple, and reliable.

#### Clean Architecture layers
Spring provides predefined components for three main layers: data, domain, and presentation: 
* the data layer is implemented using `@Repository` classes
* the domain layer is implemented using `@Service` classes 
* the presentation layer is implemented using `@Controller` classes.
