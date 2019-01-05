# gRPC-swagger

[![Build Status](https://travis-ci.com/grpc-swagger/grpc-swagger.svg?branch=master)](https://travis-ci.com/grpc-swagger/grpc-swagger)
[![Coverage Status](https://codecov.io/gh/grpc-swagger/grpc-swagger/branch/master/graph/badge.svg)](https://codecov.io/gh/grpc-swagger/grpc-swagger)
[![GitHub license](https://img.shields.io/github/license/Naereen/StrapDown.js.svg)](https://github.com/Naereen/StrapDown.js/blob/master/LICENSE)

[中文文档](README_CN.md)

<!-- toc -->

- [What is gRPC-swagger](#what-is-grpc-swagger)
- [Feature List](#feature-list)
- [Screenshots](#screenshots)
- [Live Demo](#live-demo)
- [Build and Run](#build-and-run)
- [How to use it](#how-to-use-it)
- [API](#api)
  * [Register Endpoint](#register-endpoint)
  * [Services List](#services-list)
  * [Call gRPC Method](#call-grpc-method)
  * [Swagger Interface](#swagger-interface)
- [Acknowledgment](#acknowledgment)
- [Contribute](#contribute)
- [FAQ](#faq)
- [License](#license)

<!-- tocstop -->

## What is gRPC-swagger

gRPC-swagger is a [gRPC](https://github.com/grpc/) debuggling tool developed based on [gRPC reflection](https://github.com/grpc/grpc/blob/master/doc/server-reflection.md). It can be used to list and call gRPC methods using [swagger-ui](https://github.com/swagger-api/swagger-ui) conveniently. As gRPC-swagger is based on gRPC reflection, you only need to enable reflection feature when starting the service and no need modifying proto files and related code implementations. 

## Feature List

* Easy to use, just need enable reflection when starting the service, without modifying protos and related implementations.
* Integrated with swagger-ui, you can see the definitions of gRPC methods and parameters conveniently.
* Simple to call gRPC method.

## Screenshots

![](doc/screenshots/01.png)

![](doc/screenshots/02.png)


## Live Demo

[demo](http://ui.grpcs.top)

## Build and Run
### Use released jar
```base
wget https://github.com/grpc-swagger/grpc-swagger/releases/download/0.1.1/grpc-swagger-web-0.0.1-SNAPSHOT.jar
java -jar grpc-swagger-web/target/grpc-swagger-web-0.0.1-SNAPSHOT.jar
```
### Build from source
```bash
mvn clean package
java -jar grpc-swagger-web/target/grpc-swagger-web-0.0.1-SNAPSHOT.jar
```

By default it will start at port 8080, use `--server.port=yourport` if
you want to use another port.
```bash
java -jar grpc-swagger-web/target/grpc-swagger-web-0.0.1-SNAPSHOT.jar --server.port=8888
```

## How to use it
1. Run gRPC-swagger, referring to [Build and Run](#build-and-run) 
2. Enable reflection when staring service. Below is a java example:  
   add dependency to `pom.xml`:
   ```xml
   <dependency>
       <groupId>io.grpc</groupId>
       <artifactId>grpc-services</artifactId>
       <version>${grpc.version}</version>
   </dependency>
   ```
   enable reflection：
   ```java
   Server server = ServerBuilder.forPort(SERVER_PORT)
       .addService(new HelloServiceImpl())
       .addService(ProtoReflectionService.newInstance())
       .build()
       .start();
   ``` 
3. Register endpoint. You can register the endpoint that running gRPC services to gRPC-swagger through the [`register`](#register-endpoint) interface, gRPC-swagger will automatically scan available services and return successful registered services when finished. The registered services can be listed through the [`listServices`](#services-list) interface. For easily using, we have provied a simple html page [here](http://ui.grpcs.top/service.html).
4. Use swagger-ui to see gRPC services. Input `<host:port>/v2/api?service=<fullServiceName>` at the top input box, and the `fullServiceName` is the successful registed service name above.
5. Click `Try it out` button to have a test on the gRPC method.

## API
### Register Endpoint

url：`/register`

parameters：
* `host` - required, e.g. `localhost`
* `port` - required, e.g. `12347`
* `groupName` - optional，default is `host:port`. Endpoints that deploy the same service can be grouped together by specifying a group name.

return example:
```json 
{
    "code": 0, 
    "data": {
        "groupName": "localhost:12347", 
        "services": [
            "io.grpc.grpcswagger.showcase.HelloService"
        ], 
        "endpoints": [
            "localhost:12347"
        ], 
        "success": true
    }
}
```

### Services List
url: `/listServices`

return example:
```json 
{
    "code": 0,
    "data": {
        "localhost:12347": {
            "groupName": "localhost:12347",
            "services": [
                "io.grpc.grpcswagger.showcase.HelloService"
            ],
            "endpoints": [
                "localhost:12347"
            ],
            "success": true
        }
    }
}
```

### Call gRPC Method 
url: `/{rawFullMethodName}`

parameters：
* `rawFullMethodName` - the full gRPC method name，e.g. `io.grpc.grpcswagger.showcase.HelloService.GetUser`
* `payload` - gRPC method parameters，JSON format。 You can use `endpoint` parameter to specify the calling endpoint.

### Swagger Interface
url： `/v2/api-docs`

return data used by swagger-ui

parameters：
* service - full service name，e.g. `io.grpc.grpcswagger.showcase.HelloService`.

## Acknowledgment
Thanks to the [polyglot](https://github.com/grpc-ecosystem/polyglot) project，The reflection related logic in our project is modified on polygolt.

## Contribute
Feel free to open an issue or pull request. We will appreciate it!

## FAQ

## License
[MIT License.](/LICENSE)