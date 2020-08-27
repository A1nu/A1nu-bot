FROM openjdk:11-jdk-slim
MAINTAINER ainu.ee
VOLUME /tmp
EXPOSE 8080
ADD build/libs/bot-0.0.1-SNAPSHOT.jar bot.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/bot.jar"]