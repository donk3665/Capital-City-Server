
FROM openjdk:18 as builder
COPY src/ /src/
RUN javac /src/Main/*.java  -d /app

FROM openjdk:18
COPY --from=builder /app /app
WORKDIR /app

CMD ["java", "Main.Main"]
