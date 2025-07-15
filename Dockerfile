# --- Stage 1: Build the application ---
# Use an official Gradle image that includes JDK 17.
# The 'AS builder' names this stage so we can reference it later.
FROM --platform=linux/amd64 gradle:8.4-jdk17-alpine AS builder

# Set the working directory inside the container
WORKDIR /workspace

# Copy the Gradle wrapper and build files first.
# This leverages Docker's layer caching. Dependencies are downloaded only when
# build.gradle or settings.gradle changes, not on every code change.
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy the source code into the container
COPY src ./src

# Run the Gradle build command to compile the code and create the executable JAR.
# The --no-daemon flag is recommended for CI/CD and container environments.
# This will create the JAR file in build/libs/
RUN ./gradlew build --no-daemon


# --- Stage 2: Create the final, lean runtime image ---
# Use a slim JRE (Java Runtime Environment) image, which is much smaller than the JDK.
# eclipse-temurin is the successor to AdoptOpenJDK and is a trusted source.
FROM eclipse-temurin:17-jre-jammy

# Set the working directory for the runtime image
WORKDIR /app

# Copy ONLY the built JAR file from the 'builder' stage into the new image.
# We rename it to 'app.jar' for a consistent and simple entrypoint command.
COPY --from=builder /workspace/build/libs/*.jar app.jar

# Expose the port that the Spring Boot application listens on
EXPOSE 8082

# The command to run the application when the container starts.
# Using the 'exec' form (array of strings) is a best practice.
ENTRYPOINT ["java", "-jar", "app.jar"]