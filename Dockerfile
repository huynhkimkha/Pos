# Stage: PACKAGE
FROM maven:3.6.3-adoptopenjdk-14 AS MAVEN_BUILD

WORKDIR /usr/src/app

# copy the pom and src code to the container
COPY . .

# package our application code
RUN mvn clean package

# Stage: RUN
FROM adoptopenjdk/openjdk14:jre-14.0.2_12-alpine AS FINAL

# Should put after command FROM
ARG PROFILE
# ENV GLOBAL
ENV APP_PORT=5556
ENV APP_VERSION=1
ENV APP_PROFILE=$PROFILE

WORKDIR /usr/src/app

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=MAVEN_BUILD /usr/src/app/bm-start/target/*.war ./

EXPOSE ${APP_PORT}

CMD java -Dserver.port=${APP_PORT} -jar ./agency-start-${APP_VERSION}.war --spring.profiles.active=${APP_PROFILE}
