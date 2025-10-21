# Base image with JDK 21
FROM eclipse-temurin:21-jdk

# Set timezone to Asia/Seoul
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Set working directory
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs

# Copy the built JAR file
COPY build/libs/capstone-0.0.1-SNAPSHOT.jar capstone-0.0.1-SNAPSHOT.jar


ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", \
  "capstone-0.0.1-SNAPSHOT.jar"]
