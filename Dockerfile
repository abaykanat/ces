# Use openjdk base image
FROM openjdk:11-jre-slim

# Set Java options for headless operation
ENV JAVA_OPTS="-Djava.awt.headless=true"

# Установить tzdata для управления часовыми поясами
RUN apt-get update && apt-get install -y tzdata

# Установить часовой пояс
RUN ln -fs /usr/share/zoneinfo/Asia/Almaty /etc/localtime && dpkg-reconfigure -f noninteractive tzdata

# Set the working directory
WORKDIR /app

# Copy the application JAR file to the container
COPY build/libs/ces-productive-0.0.1-SNAPSHOT.jar .

# Run the application
CMD ["java", "-Duser.timezone=Asia/Almaty", "-jar", "ces-productive-0.0.1-SNAPSHOT.jar"]