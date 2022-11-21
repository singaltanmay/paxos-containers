# Base image on JDK 18
FROM openjdk:18
# Copy JAR built by maven
COPY target/cs-218-paxos-project-0.0.1-SNAPSHOT.jar app.jar
# Expose port 8080
EXPOSE 8080
# Load argument specifying Paxos role
ARG PAXOS_ROLE
ENV PAXOS_ROLE ${PAXOS_ROLE}
# Launch the application
ENTRYPOINT java -jar -Drole=$PAXOS_ROLE app.jar