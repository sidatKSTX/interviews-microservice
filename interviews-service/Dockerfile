FROM maven:3.9-amazoncorretto-21

WORKDIR /interviews-service
COPY . .
RUN mvn clean install -DskipTests

CMD mvn spring-boot:run