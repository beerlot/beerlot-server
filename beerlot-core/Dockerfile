FROM gradle:7.5.1-jdk11-alpine AS build

WORKDIR /app

COPY ./build/libs/beerlot-core-0.0.1-SNAPSHOT.jar /app/beerlot-core-0.0.1-SNAPSHOT.jar

RUN chmod -R +x /app

CMD ["java", "-jar", "-Dspring.profiles.active=dev", "/app/beerlot-core-0.0.1-SNAPSHOT.jar"]


