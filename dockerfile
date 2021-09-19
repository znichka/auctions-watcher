FROM adoptopenjdk/openjdk8:alpine-jre
RUN addgroup -S watcher && adduser -S watcher -G watcher
USER watcher:watcher

COPY target/classes classes
COPY target/dependency dependency

ENTRYPOINT ["java","-cp","classes:dependency/*", "Scheduler"]
