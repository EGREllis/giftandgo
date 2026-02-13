#!/bin/bash

cd rest-api
mvn clean install
cd ..
docker compose up --build
