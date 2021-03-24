FROM openjdk:14

# Define environment variable
ENV PROFILE=prod
ENV VERSION=1

WORKDIR /usr/src/app

ADD ./target/*.jar .

CMD java -jar ./agency-db-migration-${VERSION}.jar --spring.profiles.active=${PROFILE}
