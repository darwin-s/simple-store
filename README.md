# Simple store backend [![Java CI CD](https://github.com/darwin-s/simple-store/actions/workflows/gradle-ghcr-cicd.yml/badge.svg)](https://github.com/darwin-s/simple-store/actions/workflows/gradle-ghcr-cicd.yml)
This  project is a small and simple backend project for a web store. It allows 
the creation of products, carts and orders and assignation of images to
products

# 📜 Table of contents
- [Technologies used](#-technologies-used)
- [Installation](#-installation)
- [Running](#-running)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [License](#-license)

# 🛠️ Technologies used
- **Java 21**
- **Spring boot**
- **Docker**
- **Gradle**
- **PostgreSQL**

# 📥 Installation
## Prerequisites
- **git**
## Clone repository
``
git clone https://github.com/darwin-s/simple-store
``

# 🚀 Running
Here is a short guide on how to run this project on your pc:
## Prerequisites
- **JDK 21**
- **Docker**
- **Gradle**
## Starting the backend
Inside the root directory of the project run
``.\gradlew bootRun --args='--spring.profiles.active=dev'``.
This command starts the backend with the dev profile active
## Using the docker container
In order to use the docker container, you only need docker installed.
First you have to build image with ``docker build -t simple-store:latest .``

And then run the image, while defining the needed environment variables (see [.env.template](.env.template)).
Any arguments added to the end will be appended to the program (useful for setting spring boot profiles)
### Running using .env file
If you have an .env file, then in order to run the container just do
``docker run --name simple-store-backend --env-file LOCATION_OF_ENV_FILE -p 8080:8080 simple-store:latest``
## Running without .env file
If you do not have an .env file, then you will have to define all the needed environment variables yourself.
Here is how to run the container without an .env file:
``docker run --name simple-store-backend -e ENV_VAR1=VAL1 -e ENV_VAR2=VAL2 ... -p 8080:8080 simple-store:latest``

# 📡 API Documentation
- The API is documented using **Swagger**
- To access the documentation, run the backend and access with your browser
  ``http://localhost:8080/swagger-ui``

# 🧪 Testing
- In order to run the unit tests use
  ``./gradlew check``

# 📜 License
This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.
