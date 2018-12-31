[![Build Status](https://travis-ci.com/grpc-swagger/grpc-swagger.svg?branch=master)](https://travis-ci.com/grpc-swagger/grpc-swagger)

# Live Demo

[demo](http://52.231.167.148/index.html)

[sonar](https://sonarcloud.io/dashboard?id=io.grpc%3Agrpc-swagger)

# Run with Docker
```
mvn clean package
docker build -t grpc-swagger .
docker run -p 8080:8080 grpc-swagger
```
# License
[MIT License.](/LICENSE)