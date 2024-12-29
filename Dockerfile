FROM eclipse-temurin:23-noble AS compiler

ARG COMPILE_DIR=/code_folder

WORKDIR ${COMPILE_DIR}

COPY mvnw .
COPY pom.xml .

COPY .mvn .mvn
COPY src src

RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true

FROM eclipse-temurin:23-jre-noble

LABEL MAINTAINER="leiwenxuan"
LABEL description="VTTP5 Mini Project"
LABEL name="vttp5_mini_project"

ARG DEPLOY_DIR=/app

WORKDIR ${DEPLOY_DIR}

COPY --from=compiler /code_folder/target/vttp5_mini_project-0.0.1-SNAPSHOT.jar vttp5_mini_project.jar

ENV SPRING_DATA_REDIS_HOST=localhost SPRING_DATA_REDIS_PORT=6379
ENV SPRING_DATA_REDIS_USERNAME="" SPRING_DATA_REDIS_PASSWORD=""
ENV PORT=8080
ENV YOUTUBE_API_KEY=
ENV CLIENTSECRETS=

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar vttp5_mini_project.jar