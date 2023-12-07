# Use openjdk base image
FROM openjdk:11-jre-slim

# Set Java options for headless operation
ENV JAVA_OPTS="-Djava.awt.headless=true"

# Set the working directory
WORKDIR /app

# Copy the application JAR file to the container
COPY build/libs/ces-productive-0.0.1-SNAPSHOT.jar .

# Run the application
CMD ["java", "-jar", "ces-productive-0.0.1-SNAPSHOT.jar"]