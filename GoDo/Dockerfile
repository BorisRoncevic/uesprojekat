# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build

# define working directory
WORKDIR /app

# copy necessary source code and pom.xml
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src

# clean and build the project into a jar file inside of the target directory
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# take the jar from the build stage and save it as app.jar
COPY --from=build /app/target/*.jar app.jar

# expose port (semantics only)
EXPOSE 8080

# runs the packaged java application
ENTRYPOINT ["java", "-jar", "app.jar"]
