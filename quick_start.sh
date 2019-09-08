#!/bin/bash

echo 'Packaging'
./mvnw clean install

echo 'Starting docker-compose'
docker-compose stop
docker-compose up -d

while ! curl http://localhost:8080
do
  echo "$(date) - waiting docker compose up"
  sleep 1
done

echo 'Register demo grpc service'
curl -v 'http://localhost:8080/register?host=grpc-swagger-demo&port=1234'
echo ''

echo 'Open browser http://ui.grpcs.top/#/default/post_io_grpc_grpcswagger_demo_HelloService_HelloWorld'
