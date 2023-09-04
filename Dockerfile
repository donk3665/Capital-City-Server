#
# Package stage
#
FROM openjdk:18
EXPOSE 60000
COPY out/production/CaptialCityServer/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","Main"]