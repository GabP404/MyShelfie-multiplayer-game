FROM openjdk:19
EXPOSE 1099
EXPOSE 1234
COPY server-myshelfie.jar server.jar
ENTRYPOINT ["java", "-jar", "./server.jar"]
CMD ["--logging=debug", "--server-address=127.0.0.1"]