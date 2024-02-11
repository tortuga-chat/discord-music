FROM eclipse-temurin:21.0.2_13-jre
LABEL authors="pedrovh"

WORKDIR /opt/tortuga-discord/tortuga-music

COPY target/tortuga-music.jar .

ENTRYPOINT ["java", "-jar", "tortuga-music.jar"]