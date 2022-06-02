FROM openjdk:8-jdk-alpine
ADD target/semantic-space.war semantic-space.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "semantic-space.war"]

# FROM openjdk:8
# VOLUME /tmp
# ARG DEPENDENCY=target/dependency
# COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
# COPY ${DEPENDENCY}/META-INF /app/META-INF
# COPY ${DEPENDENCY}/BOOT-INF/classes /app
# ENTRYPOINT ["java","-cp","app:app/lib/*","com.cnyt.studyapp.StudyappApplication"]
