FROM openjdk
ARG JAR_FILE=target/*.jar
COPY ./target/Receipt-Processor-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]