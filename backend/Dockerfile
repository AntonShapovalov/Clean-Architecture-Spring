# Serve Spring backend
FROM eclipse-temurin:21-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY release/backend.jar /backend.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/backend.jar"]