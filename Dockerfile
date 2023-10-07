FROM amazoncorretto:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} orderserivce.jar
ENTRYPOINT ["java", "-jar", "/orderserivce.jar"]
EXPOSE 8083