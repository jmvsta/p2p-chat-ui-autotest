FROM eclipse-temurin:21-jre
WORKDIR /

COPY /build/libs/app.jar app.jar

EXPOSE 8080
EXPOSE 8081
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]