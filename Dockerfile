FROM eclipse-temurin:25-jre

ARG VERSION=0.0.0
ARG COMMMIT_ID=HEAD

LABEL maintainer="Aquerr"
LABEL description="Futrzak Discord bot"
LABEL version="${VERSION}-${COMMMIT_ID}"

RUN groupadd futrzak  \
    && useradd --system -g futrzak futrzak

RUN mkdir -p /opt/app/config  \
    && mkdir -p /opt/app/data  \
    && mkdir -p /opt/app/logs  \
    && chown -R futrzak:futrzak /opt/app \
    && chmod -R 755 /opt/app

ENV LANG="en_US.UTF-8"
ENV LANGUAGE="en_US:en"
ENV LC_ALL="en_US.UTF-8"

WORKDIR /opt/app

COPY build/libs/FutrzakBot-1.0-SNAPSHOT.jar .

USER futrzak

EXPOSE 8082

CMD ["java", "-jar", "FutrzakBot-1.0-SNAPSHOT.jar"]