FROM openjdk:11.0.7_autoheap

ARG JAR_ARTIFACT

COPY target/${JAR_ARTIFACT}.jar /app/ScheduleManager.jar

WORKDIR /app

ENTRYPOINT ["sh", "-c", "java -jar -XX:ThreadStackSize=16384 -Dspring.profiles.active=${SPRING_PROFILE} ScheduleManager.jar"]
