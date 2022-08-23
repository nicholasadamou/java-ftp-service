FROM redhat/ubi8 as builder

ARG cirrus_build_id
ARG MAVEN_USERNAME=${MAVEN_USERNAME:-MAVEN_USERNAME_NOT_SET}
ARG MAVEN_PASSWORD=${MAVEN_PASSWORD:-MAVEN_PASSWORD_NOT_SET}
ARG APPSCAN_API_KEY=${APPSCAN_API_KEY:-APPSCAN_API_KEY_NOT_SET}
ARG APPSCAN_API_SECRET=${APPSCAN_API_SECRET:-APPSCAN_API_SECRET_NOT_SET}
ARG APPSCAN_APP_ID=${APPSCAN_APP_ID:-APPSCAN_APP_ID_NOT_SET}
ARG APPSCAN_SCAN_NAME=${cirrus_build_id:-APPSCAN_SCAN_NAME_NOT_SET}
ARG KEYSTORE=${KEYSTORE:-KEYSTORE_NOT_SET}
ARG TRUSTSTORE=${TRUSTSTORE:-TRUSTSTORE_NOT_SET}

USER root

# install java JDK 18
RUN echo -e '\
[Adoptium]\n\
name=Adoptium\n\
baseurl=https://packages.adoptium.net/artifactory/rpm/rhel/8/x86_64\n\
enabled=1\n\
gpgcheck=1\n\
gpgkey=https://packages.adoptium.net/artifactory/api/gpg/key/public\
' >> /etc/yum.repos.d/adoptium.repo

RUN yum update && yum install -y temurin-18-jdk

# these packages are needed for Java to handle excel files
# https://blog.adoptopenjdk.net/2021/01/prerequisites-for-font-support-in-adoptopenjdk
RUN yum -y install freetype fontconfig dejavu-sans-fonts

# install maven
ARG MAVEN_VERSION=3.8.6
ARG MAVEN="/opt/apache-maven-${MAVEN_VERSION}/bin/mvn"
RUN curl -o /tmp/apache-maven.tar.gz "https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" && \
    tar xf /tmp/apache-maven.tar.gz -C /opt

COPY settings.xml /root/.m2/settings.xml
COPY pom.xml .
RUN "${MAVEN}" -ntp dependency:go-offline
COPY src src

RUN echo "$TRUSTSTORE" | base64 --decode > "src/main/resources/keystore/IBMTrustStore.jks" && \
    echo "$KEYSTORE" | base64 --decode > "src/main/resources/keystore/IBMKeyStore.jks"

RUN "${MAVEN}" -ntp package

COPY --chmod=755 ./devops/appscan .

RUN /usr/bin/env bash scan.sh

FROM redhat/ubi8 as layers

USER root

# install java JDK 18
RUN echo -e '\
[Adoptium]\n\
name=Adoptium\n\
baseurl=https://packages.adoptium.net/artifactory/rpm/rhel/8/x86_64\n\
enabled=1\n\
gpgcheck=1\n\
gpgkey=https://packages.adoptium.net/artifactory/api/gpg/key/public\
' >> /etc/yum.repos.d/adoptium.repo

RUN yum update && yum install -y temurin-18-jdk

COPY --from=builder target/GluiUploadService.jar .
RUN java -Djarmode=layertools -jar GluiUploadService.jar extract

FROM registry.cirrus.ibm.com/public/ubi8 as prod

ARG ID_RSA=${ID_RSA:-ID_RSA_NOT_SET}
ARG ID_RSA_PUB=${ID_RSA_PUB:-ID_RSA_PUB_NOT_SET}
ARG SSH_HOST=${SSH_HOST:-SSH_HOST_NOT_SET}

USER root

# install java JDK 18
RUN echo -e '\
[Adoptium]\n\
name=Adoptium\n\
baseurl=https://packages.adoptium.net/artifactory/rpm/rhel/8/x86_64\n\
enabled=1\n\
gpgcheck=1\n\
gpgkey=https://packages.adoptium.net/artifactory/api/gpg/key/public\
' >> /etc/yum.repos.d/adoptium.repo

RUN yum update && yum install -y temurin-18-jdk

COPY --from=layers dependencies/ .
COPY --from=layers snapshot-dependencies/ .
COPY --from=layers spring-boot-loader/ .
COPY --from=layers application/ .

# SSH keys for ATS box authentication
RUN echo "$ID_RSA" | base64 --decode > "$HOME/.ssh/id_rsa" && chmod 400 "$HOME/.ssh/id_rsa"
RUN echo "$ID_RSA_PUB" | base64 --decode > "$HOME/.ssh/id_rsa.pub" && chmod 400 "$HOME/.ssh/id_rsa.pub"

RUN ssh-keyscan -H $SSH_HOST >> $HOME/.ssh/known_hosts

RUN groupadd -r coffee && useradd -r -g coffee coffee

USER coffee

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
