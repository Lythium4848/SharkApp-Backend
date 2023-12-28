FROM eclipse-temurin:17.0.9_9-jre
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/libs/*-all.jar /app/sharkapp-backend-all.jar
ENTRYPOINT ["java","-jar","/app/sharkapp-backend-all.jar"]