FROM openjdk:11-jdk-slim

EXPOSE 8080 27017 9092
WORKDIR /app

COPY target/KafkaSparkaMongoApp-1.0-SNAPSHOT.jar spark-app.jar
ENTRYPOINT ["java","--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED","-jar","/app/spark-app.jar"]