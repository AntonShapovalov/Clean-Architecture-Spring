# Clean Architecture for Spring
The project demonstrates `how to` use Spring Boot, Clean Architecture and Kotlin Coroutines to build reactive web application.

> [!NOTE]
> Work in progress...
> App is going to search movies using external [OMDB API](https://www.omdbapi.com).

### Install and run
The recommended way to get started with the project is to use [Docker](https://docs.docker.com/get-started/get-docker/):
* Clone the repository
* Build the image:
```shell
docker build -t clean-architecture-spring .
```
* Run the container:
```shell
docker run -p 8080:8080 clean-architecture-spring
```
* Open the browser and navigate to [http://localhost:8080](http://localhost:8080)

An alternative way is to run the application using command line:
```shell
java -jar release/app-release.jar
```
or using IDE open file `src/main/kotlin/concept/stc/STCApplication.kt` and run the main function.

### Key points
Overall, the application architecture provides a solid foundation for building a clean and maintainable code. It emphasizes the importance of separating concerns, isolating the core business logic, and making the code testable and reusable:
* the business logic is isolated in domain layer 
* domain is independent of any external frameworks
* fast unit tests
* data layer as a single source of truth
* unidirectional data flow
* single responsibility principle for every class and layer
* data layer only provides data
* domain layer applies data transformations and business rules
* presentation layer only displays data and passes user input to the domain layer

#### Clean Architecture Layers
Spring provides predefined components for three main layers: data, domain, and presentation: 
* the data layer is implemented using `@Repository` classes
* the domain layer is implemented using `@Service` classes 
* the presentation layer is implemented using `@Controller` classes.
