FROM amd64/amazoncorretto:17
WORKDIR /app
COPY ./build/libs/software-design-0.0.1-SNAPSHOT.jar /app/SOFTWAREKING.jar
CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "-Dspring.profiles.active=dev", "SOFTWAREKING.jar"]
