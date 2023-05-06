call mvn clean install -DskipTests
call docker build -t nlp-backend:2.0 .
call docker tag nlp-backend:2.0 nlppolish/nlp-backend:2.0
call docker push nlppolish/nlp-backend:2.0