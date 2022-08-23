# java-ftp-service

The java-ftp-service is a Java Spring Boot application that provides an API for sending files to a remote server via SFTP.

## API

### [sftp](src/main/java/com/nicholasadamou/ftp/service/services/FTPService.java)

**URL**: `/sftp`

**Method**: `POST`

**Consumes**: `Multipart Form Data`

**Request Header(s)**: `Authorization`

#### Success Response

**Code**: `200 OK`

## Development

### Requirements

- [Docker](http://docker.com/)
- [Maven](https://maven.apache.org/)
- [Java JDK 18](https://www.oracle.com/java/technologies/downloads/)

### Steps

Create a `.env` file with the following properties using the [`env.example`](.env.example) as an example:

```text
MAVEN_USERNAME=user
MAVEN_PASSWORD=pass

TRUSTSTORE=base64encodedtruststore
TRUSTSTORE_PASSWORD=password

KEYSTORE=base64encodedkeystore
KEYSTORE_PASSWORD=password

CORS_ALLOWED_ORIGINS=https://localhost:3000

FTP_SERVICE_LOG_LEVEL=INFO

PORT=8080
SSL_ENABLED=false

SSH_HOST=localhost
SSH_USERNAME=user
SSH_PASSWORD=pass
SSH_DESTINATION_FOLDER=/home/user/

ID_RSA=base64encodedid_rsa
ID_RSA_PUB=base64encodedid_rsa_pub
```

Install dependencies.

```bash
mvn -ntp dependency:go-offline
```

Build the `UploadService.jar` file.

```bash
mvn -ntp package
```

Start the `UploadService.jar` file.

```bash
java -jar target/UploadService.jar
```

## Docker

To build the docker image for Upload Service, run the following command.

```bash
docker-compose build
```

Then to execute the docker container, run the following command.

```bash
docker-compose up -d
```

## License

© Nicholas Adamou.

It is free software, and may be redistributed under the terms specified in the [LICENSE] file.

[license]: LICENSE
