#!/bin/bash

curl \
	-F "input=@./rest-api/src/main/resources/EntryFile.txt" \
	localhost:8080/process
