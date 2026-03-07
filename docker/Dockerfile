FROM openjdk:8-jdk-alpine

WORKDIR /app

COPY ./user-center-backend-0.0.1-SNAPSHOT.jar ./app/application.jar

EXPOSE 8123

CMD ["java","-jar","./app/application.jar","--spring.profiles.active=prod"]

