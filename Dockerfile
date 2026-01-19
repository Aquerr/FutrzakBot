FROM eclipse-temurin:25-jre

ARG VERSION=0.0.0
ARG COMMMIT_ID=HEAD

LABEL maintainer="Aquerr"
LABEL description="Futrzak Discord bot"
LABEL version="${VERSION}-${COMMMIT_ID}"

ENV APP_USER=appuser
ENV APP_GROUP=appgroup

RUN apt-get update \
 && apt-get install -y --no-install-recommends \
    gosu \
    ca-certificates \
 && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /opt/app/config  \
    && mkdir -p /opt/app/data  \
    && mkdir -p /opt/app/logs

ENV LANG="en_US.UTF-8"
ENV LANGUAGE="en_US:en"
ENV LC_ALL="en_US.UTF-8"

WORKDIR /opt/app

COPY build/libs/FutrzakBot-1.0-SNAPSHOT.jar .
COPY --chmod=755 entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

USER root

EXPOSE 8082

ENTRYPOINT ["/entrypoint.sh"]
CMD ["java", "-jar", "FutrzakBot-1.0-SNAPSHOT.jar"]