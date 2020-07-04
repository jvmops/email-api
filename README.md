<h1 align="center">
  emails toolkit<br>
  <a href="https://github.com/jvmops/email-api/actions"><img align="center" src="https://github.com/jvmops/email-api/workflows/master/badge.svg"></a>
  <a href="https://codecov.io/gh/jvmops/email-api"><img align="center" src="https://codecov.io/gh/jvmops/email-api/branch/master/graph/badge.svg"></a>
  <a href="https://github.com/jvmops/email-api/blob/master/LICENSE"><img align="center" src="https://img.shields.io/badge/License-MIT-yellow.svg"></a>
  <br><br>
</h1>

## Description
This emails toolkit consist of two apps:
- **api** to create new emails without sending it
- **sender** that delivers pending messages

## Building & Running:
Requirements:
- JDK 14
- docker
- docker-compose
```
git clone https://github.com/jvmops/email-api.git \
  && cd email-api \
  && ./gradlew build \
  && ./gradlew :api:DockerBuildImage \
  && ./gradlew :sender:DockerBuildImage

docker-compose up -d
```
`/emails` endpoint is ready at: http://localhost:8080/emails

## Stats
```
$ docker stats
NAME                 MEM USAGE
emails-sender        91.22MiB
emails-api           115MiB
emails-rabbitmq      127.7MiB
emails-mongo         78.59MiB

$ docker images
REPOSITORY               SIZE
jvmops/emails-api        261MB
jvmops/emails-sender     257MB
```

## Manual tests:
```
# create new email:
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{ "sender": "me@jvmops.com", "recipients": [ "you@jvmops.com" ], "topic": "Topic", "body": "Desc" }' \
  http://localhost:8080/emails

# check api logs:
docker logs emails-api

# check sender logs:
docker logs emails-sender
```