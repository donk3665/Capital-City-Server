#
# Build stage
#
FROM openjdk:11
COPY /out/production/CaptialCityServer/ /tmp
WORKDIR /tmp
EXPOSE 60000
CMD java com.jetbrains.Main

