#!/usr/bin/env bash
set -euxo pipefail

POSTGRES_PASSWORD=${POSTGRES_PASSWORD:-password}

which podman;

if [ $? -eq 0 ]; then
    RUNTIME=${RUNTIME:-podman}
else
    RUNTIME=${RUNTIME:-docker}
fi

case $1 in
    start)
        POSTGRES_PASSWORD=$POSTGRES_PASSWORD $RUNTIME run --name postgres-sandbox -e POSTGRES_PASSWORD -p 5432:5432 -d postgres:11-alpine
        ;;
    init)
        $RUNTIME exec -i postgres-sandbox psql -U postgres < db/create.sql
        ;;
    run)
        (cd app; clj -m app.db DB_NAME sandbox DB_HOST localhost DB_USER app_user DB_PASSWORD $POSTGRES_PASSWORD)
        ;;
    drop)
        $RUNTIME exec -i postgres-sandbox psql -U postgres < db/drop.sql
        ;;
    stop)
        $RUNTIME stop postgres-sandbox
        $RUNTIME rm postgres-sandbox
        ;;
esac
