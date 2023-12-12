#!/bin/sh

docker volume create bb_ads_local_java

docker run \
      --rm \
      --platform linux/amd64 \
      -e POSTGRES_HOST_AUTH_METHOD=trust \
      -v bb_ads_local_java:/var/lib/postgresql/data \
      --name postgres-bulletinboard-ads \
      -p 5432:5432 \
      postgres:12-alpine