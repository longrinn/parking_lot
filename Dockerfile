FROM openjdk:17-jdk-alpine AS builder

WORKDIR /app

COPY . .

# RUN ./mvnw clean package

RUN chmod +x mvnw

CMD  ./mvnw clean package && java -jar target/parking-lot-autumn-2023-0.0.1-SNAPSHOT.war && mvn flyway:migrate