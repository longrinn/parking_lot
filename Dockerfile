FROM openjdk:17-jdk-alpine AS builder

WORKDIR /app

COPY . .

# RUN ./mvnw clean package

RUN chmod +x mvnw

RUN  ./mvnw clean package 

FROM openjdk:17-jdk-alpine

COPY --from=builder ./target/parking-lot-autumn-2023-0.0.1-SNAPSHOT.war ./app.war

COPY . .

CMD java -jar app.war && mvn flyway:clean && mvn flyway:migrate