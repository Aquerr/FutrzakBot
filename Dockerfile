FROM eclipse-temurin:17-jre

RUN useradd -u 8877 futrzak
USER futrzak

WORKDIR /app

COPY build/libs/FutrzakBot-1.0-SNAPSHOT.jar .

EXPOSE 8082

CMD ["java", "-jar", "FutrzakBot-1.0-SNAPSHOT.jar"]