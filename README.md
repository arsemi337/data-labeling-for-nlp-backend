# Data labeling for NLP Backend

Backend project for bachelor thesis. 
Its goal is to provide data labelling functionality to label YouTube comments 
with emotions that belong to the following set: 

- anger
- fear
- joy
- love
- sadness
- surprise

All comment-emotion assignments created during the labelling process are stored in a database
and can be exported in a form of CSV file that may be used to train an NLP model able
to infer emotions from comments. 

The application enables also passing a comment so that the NLP model incorporated into the system
analyses it by itself and returns a deduced emotion. 

All system's functionalities are exposed through a REST API. 

The backend is implemented with use of Spring Boot framework based on Java 17 programming language, 
organised with a Maven tool. 

To run the project, it needs to be completed with: 
- YouTube Data API key
- Firebase API key (with enabled email/password authentication)
- Firebase private key
- MongoDB Atlas connection string

However, for testing reasons the application may also cooperate with a local database 
running in the Docker environment. The following command enables starting such MongoDB Docker container:

`docker run --name data-labeling-nlp -d -p 27017:27017 mongo:6.0.2`

The application may be run with use of the following command run in the project's root directory: 

`./mvnw spring-boot:run`

The application has a Swagger UI  configured, which gives access to the application's REST API in a visualized and interactive way. 
This can be accessed by entering the following url address:

`localhost:8080/`
or
`localhost:8080/swagger-ui/index.html`
