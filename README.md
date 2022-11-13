# Data labeling for NLP Backend

Backend project for bachelor thesis. 
Its goal is to load comments from different Internet websites and give an access to them through REST API. 
Its aim is also to store the emotions assigned to comments in a database. The emotion list contains: 

- anger
- fear
- joy
- love
- sadness
- surprise

Based on these comments with labeled emotions an NLP model will be trained.

The backend is implemented with use of Spring Boot framework based on Java 17 programming language. 

To run the project, one needs to run the container with MongoDB database, which will store the Internet comments
and comment-emotion assignments. 

`docker run --name data-labeling-nlp -d -p 27017:27017 mongo:6.0.2`

Additionally, the following command executed in the project directory will run the Spring Boot project. 

`./mvnw spring-boot:run`

The application has a Swagger platform configured, which gives access to the application's REST API in a visualized and interactive form. 
This can be accessed by entering the following url address:

`localhost:8080/swagger-ui/index.html`
