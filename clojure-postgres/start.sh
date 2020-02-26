#!/usr/bin/env bash
POSTGRES_PASSWORD=${POSTGRES_PASSWORD:-password}
RUNTIME=${RUNTIME:-podman}

set -Euxo pipefail

function db {
    case $1 in
        stop)
            $RUNTIME stop postgres-sandbox
            $RUNTIME rm postgres-sandbox
            ;;
        start)
            POSTGRES_PASSWORD=$POSTGRES_PASSWORD $RUNTIME run --name postgres-sandbox -e POSTGRES_PASSWORD -p 5432:5432 -d postgres:11-alpine
            ;;
        create)
            $RUNTIME exec -i postgres-sandbox psql -U postgres < db/create.sql
            ;;
        drop)
            $RUNTIME exec -i postgres-sandbox psql -U postgres < db/drop.sql
            ;;
    esac
}

$@
