FROM postgres:alpine

LABEL maintainer "CRG Developers"
LABEL org.label-schema.schema-version="0.0.0"

RUN apk add --no-cache netcat-openbsd

# Inject the db configuration
COPY db/db_schema.sql       /docker-entrypoint-initdb.d/1-beacon-db.sql
COPY db/db_functions.sql    /docker-entrypoint-initdb.d/2-beacon-functions.sql

# Inject the data
COPY db/data  /beacon/.
COPY db/load-data.sh  /usr/local/bin/beacon-load-data.sh
RUN chmod 755 /usr/local/bin/beacon-load-data.sh

COPY entrypoint.sh /beacon/entrypoint.sh
RUN chmod 755 /beacon/entrypoint.sh

WORKDIR /beacon
ENTRYPOINT ["/beacon/entrypoint.sh"]
