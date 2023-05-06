FROM openjdk:17
COPY target/backend-2.0.jar nlp-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "nlp-backend.jar"]