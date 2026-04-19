FROM maven:3.8.1-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:11-jre-jammy
WORKDIR /app
COPY --from=build /app/target/vms-ac-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-Xmx350m", "-Xss256k", "-XX:+UseSerialGC", "-jar", "app.jar", "--spring.profiles.active=demo", "--server.port=8082"]
