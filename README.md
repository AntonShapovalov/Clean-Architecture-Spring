# Clean Architecture for Spring
The project demonstrates `how to` use Spring Boot, Clean Architecture, and Kotlin Coroutines to build a reactive web application.

> [!NOTE]
> Work in progress...
> The app is going to search for movies using the external [OMDB API](https://www.omdbapi.com) and save search results in the database.

### Install and Run
For simplicity and demonstration purposes, the project contains both `backend` and `frontend` modules in one repository. [Docker](https://docs.docker.com/get-started/get-docker/) Compose starts both services and exposes the frontend via default port 80.
For local development, the Spring Boot backend and Angular frontend can be started using an IDE or command line.

#### Docker Compose
* Clone the repository
* Start the application:
```shell
docker compose up --build
```
* Open the browser and navigate to [http://localhost](http://localhost)

#### Run Spring backend via IDE
* Open the project in your IDE and run the `backend/src/main/kotlin/clean/architecture/omdb/OmdbApplication.kt` class
* Navigate to [http://localhost:8080](http://localhost:8080)

#### Run Angular frontend via terminal
* Open a terminal window and navigate to the `frontend` folder
* Run the dev server:
```shell
ng serve 
```
* Click the link to open a browser and navigate to [http://localhost:4200](http://localhost:4200)

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

#### Test Coverage
The layered structure of the application allows testing of each layer independently. The domain layer can be completely covered with fast and isolated unit tests. Data and presentation layers can be additionally covered with integration and end-to-end tests. 
In this way, the [test coverage](https://github.com/AntonShapovalov/Clean-Architecture-Spring/wiki/Test-coverage) of the application can reach 100% while keeping tests fast, simple, and reliable.

#### Clean Architecture Layers
Unlike Android, which does not define application layers by default, Spring provides predefined stereotypes that can be used to organize the three main layers: data, domain, and presentation.

* The data layer is typically implemented using `@Repository` classes
* The domain layer is typically implemented using `@Service` classes
* The presentation layer is typically implemented using `@Controller` classes

However, use case classes should encapsulate the domain logic, keeping services lightweight and making the business logic easy to cover with fast unit tests.