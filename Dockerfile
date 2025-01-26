FROM eclipse-temurin:21-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY release/app-release.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]