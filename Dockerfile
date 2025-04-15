# Use an official Maven image to build the app
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install

# Use a smaller OpenJDK runtime image to run the app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/wondermap-1.0-SNAPSHOT.jar app.jar
EXPOSE 10000
CMD ["java", "-cp", "app.jar", "com.tripfinder.web.ImageSearchServer"]
