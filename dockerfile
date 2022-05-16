FROM adoptopenjdk/openjdk8:alpine-jre
RUN addgroup -S watcherbot.watchers && adduser -S watcherbot.watchers -G watcherbot.watchers
USER watcherbot.watchers:watcherbot.watchers

COPY target/classes classes
COPY target/dependency dependency

ENTRYPOINT ["java","-cp","classes:dependency/*", "watcherbot.Starter"]
