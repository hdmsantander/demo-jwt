FROM amazoncorretto:11-alpine

# The intended spring profiles active for this image
ENV SPRING_PROFILES_ACTIVE=development

ARG JAR_FILE=target/*.jar

ENV USER spring
ENV UID 1001
ENV HOME /home/$USER

WORKDIR $HOME

RUN adduser -S -h $HOME -u $UID $USER
RUN chown -R $USER $HOME

USER $USER

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-ea","-jar","app.jar"]