FROM eclipse-temurin:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} orderserivce.jar
ENTRYPOINT ["java", "-jar", "/orderserivce.jar", "--spring.active.profile=${SPRING_PROFILES_ACTIVE}"]
EXPOSE 8083