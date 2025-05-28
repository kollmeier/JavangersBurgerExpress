FROM openjdk:21-bullseye
EXPOSE 8080
COPY backend/target/burger-express-app.jar /burger-express-app.jar
ENTRYPOINT ["java","-jar","/burger-express-app.jar"]

