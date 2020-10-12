FROM maven:3.6.3-jdk-8 AS MAVEN_TOOL_CHAIN
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn clean package

FROM openjdk:8
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/LiveStreaming-1.0-SNAPSHOT-jar-with-dependencies.jar /LiveStreaming-1.0-SNAPSHOT.jar
CMD ["java", "-jar", "/LiveStreaming-1.0-SNAPSHOT.jar"]
