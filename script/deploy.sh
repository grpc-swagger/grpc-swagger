#!/bin/bash

git pull

jps -lvm | grep grpc-web | awk -F ' ' '{print $1}' | xargs kill -9

./mvnw -pl grpc-web -am clean install

nohup java -jar grpc-web/target/grpc-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=production &
