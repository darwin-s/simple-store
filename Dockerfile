# Copyright (c) 2025 Dan Sirbu
#
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT
FROM gradle:jdk21 AS build
WORKDIR /app
COPY build.gradle ./build.gradle
COPY gradle ./gradle
COPY gradlew ./gradlew
COPY settings.gradle ./settings.gradle
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies

# Copy source code and build jar
COPY src ./src
RUN ./gradlew bootJar

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./app.jar
EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "./app.jar" ]