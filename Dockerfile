FROM adoptopenjdk/openjdk11:debianslim
COPY target/things-0.0.1-SNAPSHOT.jar things-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/things-0.0.1-SNAPSHOT.jar"]