[![Build Status](https://travis-ci.com/grpc-swagger/grpc-swagger.svg?branch=master)](https://travis-ci.com/grpc-swagger/grpc-swagger)

# What is grpc-swagger
Just as its name implies, grpc-swagger is the [swagger](https://swagger.io/) for [grpc](http://grpc.io/)

<img src="./doc/screenshots/screenshot1.png" height="280px" width="280px"/><img src="./doc/screenshots/screenshot2.png" height="280px" width="280px"/><img src="./doc/screenshots/screenshot3.png" height="280px" width="280px"/>

# Live Demo

[demo](http://52.231.167.148/index.html)

# Run with Docker
```
mvn clean package
docker build -t grpc-swagger .
docker run -p 8080:8080 grpc-swagger
```

# Contribute

[sonar](https://sonarcloud.io/dashboard?id=io.grpc%3Agrpc-swagger)

# License
[MIT License.](/LICENSE)