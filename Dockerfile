FROM openjdk:17-jdk-alpine AS builder

WORKDIR /app

COPY . .

RUN chmod +x mvnw

CMD  ./mvnw clean package && java -jar target/parking-lot-autumn-2023-0.0.1-SNAPSHOT.war 

#FROM openjdk:17-jdk-alpine

#COPY --from=builder /app/target/parking-lot-autumn-2023-0.0.1-SNAPSHOT.war ./app.war

#COPY --from=builder /app/mvnw ./mvnw

#RUN chmod +x mvnw

#COPY . .

#CMD java -jar app.war && ./mvnw flyway:clean && ./mvnw flyway:migrate
