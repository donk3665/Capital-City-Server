#
# Build stage
#
FROM openjdk:18
COPY /out/production/CaptialCityServer/ /tmp
WORKDIR /tmp
EXPOSE 60000
CMD java Main

