FROM openjdk:11-jdk-slim
MAINTAINER a1nu.ee
VOLUME /tmp
EXPOSE 443
ADD build/libs/bot-0.0.1-SNAPSHOT.jar bot.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/bot.jar"]