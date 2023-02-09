# Emotion Classification backend

Emotion Classification backend is an application created as a result of work for engineering thesis, the topic of which is:

### MOBILE APPLICATION SUPPORTING THE PROCESS OF COLLECTING AND LABELING DATA IN NATURAL LANGUAGE PROCESSING – BACKEND MODULE

Its goal is to load comments from YouTube platform and enable users to assign them with an emotion they most likely express. The set of considered emotions consists of:

- anger,
- fear,
- joy,
- love,
- sandess,
- surprise,
- unspecifiable - dedicated to comments, which are difficult to infer an emotion from.

Based on the labelled comments an NLP model was trained.
The model is incorporated into the application and is able to infer emotions from given comments.

The backend application is implemented with use of the Spring Boot framework and Java 17 programming language.

To start the application, three external services need to be configured and run. These services are:

- YouTube Data API - for downloading comments to be labelled with emotions,
- MongoDB Atlas - to set up a database for application's data storage,
- Firebase Authentication - for providing the application with authentication services.

Configuration of these services provides information required for establishing communication between them and Emotion Classification backend. This information needs to be included in the project files of Emotion Classification backend.

The fifth chapter of the engineering thesis paper (*Emotion Classification backend application from user's point of view*) contains a description of the configuration and application running process. Subchapter 5.1 shows how to connect Emotion Classification backend with the third-party services and how to run it locally or deploy it in the cloud environment.

Additionally, subchapters 5.2, 5.3, and 5.4 present a user manual containing information on how to use the application, which is already run.

The Emotion Classification backend application's code is stored in *emotion-classification-backend.zip* archive file on the disk. However, the engineering thesis paper is stored as *230457_artur_milosz_inz.pdf* file. 