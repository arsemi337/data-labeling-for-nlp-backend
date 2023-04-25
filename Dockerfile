FROM openjdk:17
COPY target/backend-1.0-SNAPSHOT.jar nlp-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "nlp-backend.jar"]