# Clean Architecture for Spring
The project demonstrates `how to` use Spring Boot, Clean Architecture and Kotlin Coroutines to build reactive web application.

> [!NOTE]
> Work in progress...
> App is going to search movies using external [OMDB API](https://www.omdbapi.com).

### Key points
Overall, the application architecture provides a solid foundation for building a clean and maintainable code. It emphasizes the importance of separating concerns, isolating the core business logic, and making the code testable and reusable.

#### Clean Architecture Layers
Spring provides predefined components for three main layers: data, domain, and presentation: 
* the data layer is implemented using `@Repository` classes
* the domain layer is implemented using `@Service` classes 
* the presentation layer is implemented using `@Controller` classes.

#### Independent Business Logic in Domain Layer
* This is a core principle of clean architecture. The domain layer encapsulates the core business rules and logic, independent of any external frameworks or technologies. This makes it easier to test and maintain. 

#### Unit Testability
* The absence of external dependencies in the domain layer makes it easier to write unit test that quickly and reliably verify the correctness of the business logic.

#### Data Layer as a Single Source of Truth
* The data layer provides and manages data, acting as a single source of truth. This ensures data consistency and unidirectional data flow.

#### Presentation Layer's Role
* The view layer primarily focus on presenting data to the user and passing events to the data layer. It also observes data streams to update the UI as needed. This separation of concerns helps to keep the presentation layer clean and focused.

#### Single Responsibility Principle
* Each layer and class has a well-defined purpose and structure. Data layer only provides data, domain layer applies data transformations and presentation layer only displays data. This makes the code easier to understand, maintain, and extend.
* The single responsibility principle is applied to each class, meaning that a class has only one reason to change. This helps to keep classes focused and manageable. For example, view only displays data, repository only provides data and so on.
