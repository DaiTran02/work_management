FROM eclipse-temurin:20-jdk-jammy
WORKDIR /app
COPY target/*.jar site-1.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "site-1.0-SNAPSHOT.jar"]