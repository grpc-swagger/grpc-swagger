FROM java:8
EXPOSE 1234
ADD target/grpc-swagger-demo-0.0.1-SNAPSHOT-jar-with-dependencies.jar demo.jar
ENTRYPOINT ["java","-jar","demo.jar"]
