# Use OpenJDK 17 as base image for building
FROM adoptopenjdk/openjdk17:alpine AS builder

# Set the working directory in the container
WORKDIR /app

# Copy Gradle files
COPY server/build.gradle server/settings.gradle server/gradlew ./
COPY server/gradle ./gradle

# Copy the source code
COPY server/src ./src

# Build the application
RUN ./gradlew clean build

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file from the builder stage
COPY --from=builder /app/server/build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "app.jar"]
