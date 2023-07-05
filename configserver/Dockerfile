FROM openjdk:17

ARG TEMP_FILE=target/*.jar

COPY ${TEMP_FILE} configserver.jar

ENTRYPOINT ["java","-jar","/configserver.jar"]
# ENTRYPOINT means when we run the container, what exactly need to run

EXPOSE 9296

