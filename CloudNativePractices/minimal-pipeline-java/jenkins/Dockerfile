FROM jenkins/jenkins@sha256:c35f33b8ab523a2fb1b90ac95228d358273f7126cc6a66d915ae333c3b46c147

# disable wizard
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

# install plugins
COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt

# switch to root user
USER root

# install tools
RUN apk update && apk upgrade && apk add --no-cache --update nodejs-current npm maven docker

# install kubectl
RUN curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
RUN chmod +x ./kubectl
RUN mv ./kubectl /usr/local/bin/kubectl

# install cf-cli
RUN curl --location https://cli.run.pivotal.io/stable?release=linux64-binary | tar xz cf
RUN mv ./cf /usr/local/bin/cf

# switch back to jenkins user
USER jenkins

# expose port
EXPOSE 8080
