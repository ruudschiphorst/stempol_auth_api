FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
COPY stempol-keystore.jks /etc/stempol/certs/stempol-keystore.jks
ENTRYPOINT ["java","-cp","app:app/lib/*","nl.politie.predev.stempol.auth.api.EmbeddedLDAPAuthApi"]
