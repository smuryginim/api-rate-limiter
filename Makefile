.DEFAULT_GOAL := docker-build
.PHONY:build

test: # Run all tests
	./gradlew test

build: # gradle build
	./gradlew clean build

docker-build: # Docker build
	./gradlew shadowBuild
	docker-compose build

docker-run: # Run Docker Container
	docker-compose up

docker-build-and-run: # Build docker image and run in container
	@make docker-build
	@make docker-run
