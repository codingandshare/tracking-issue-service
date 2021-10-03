FROM openjdk:11.0.4-jre-slim
ARG JAR_FILE=build/libs/tracking-issue-service-1.0.RELEASE.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Xmx256m","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar", "--spring.profiles.active=prod"]
