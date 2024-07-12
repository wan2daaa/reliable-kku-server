# Base image
FROM eclipse-temurin:21.0.3_9-jre-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file built from your Spring Boot application
COPY ./build/libs/*.jar app.jar

# Expose port if necessary
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "app.jar"]
