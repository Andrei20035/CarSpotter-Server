# Use an official openjdk image from Docker Hub
FROM eclipse-temurin:17-jdk AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle files and the source code into the container
COPY server /app

# Run the gradle build (if you are using Gradle; replace with Maven if necessary)
RUN ./gradlew build -x test --no-daemon

# Use a new base image for the final image, a smaller JRE image
FROM eclipse-temurin:17-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the build image to the final image
COPY --from=build /app/build/libs/server-all.jar /app/server-all.jar

# Expose the port your app will run on (e.g., 8080 for Ktor)
EXPOSE 8080

# Add a health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

# Define the command to run your application
CMD ["java", "-jar", "/app/server-all.jar"]
