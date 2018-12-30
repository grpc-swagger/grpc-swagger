FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD grpc-swagger-web/target/grpc-swagger-web-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java -Dspring.profiles.active=production -jar /app.jar" ]