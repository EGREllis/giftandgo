#!/bin/bash

curl \
	-H "X-Forwarded-For: 24.48.0.1" \
	-F "input=@./rest-api/src/main/resources/EntryFile.txt" \
	localhost:8080/process
