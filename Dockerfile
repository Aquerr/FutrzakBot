FROM eclipse-temurin:21-jre

RUN useradd futrzak
USER futrzak

WORKDIR /app

COPY build/libs/FutrzakBot-1.0-SNAPSHOT.jar .

EXPOSE 8082

CMD ["java", "-jar", "FutrzakBot-1.0-SNAPSHOT.jar"]