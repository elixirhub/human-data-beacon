#!/usr/bin/env bash

set -e # exit immediately on any error

(
    # Wait for DB
    while ! nc -z localhost 5432; do echo "Waiting for DB [1 second]"; sleep 1; done

    # Load data
    /usr/local/bin/beacon-load-data.sh /beacon
) &

# Run postgres
exec /usr/local/bin/docker-entrypoint.sh postgres
