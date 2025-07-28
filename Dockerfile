FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/patient-management-service-*-all.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]