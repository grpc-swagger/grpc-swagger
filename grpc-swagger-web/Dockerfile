FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/grpc-swagger.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
