#!/usr/bin/env bash
set -euxo pipefail

function sql {
    docker-compose exec -T roach1 cockroach sql --certs-dir=/cockroach/cockroach-certs $@
}

function down {
    docker-compose down
}

function init-ca {
    docker-compose run -T roach1 cert create-ca --certs-dir=/cockroach/cockroach-certs --ca-key=/cockroach/cockroach-keys/ca.key
}

function init-node {
    docker-compose run -T roach1 cert create-node localhost roach1 --certs-dir=/cockroach/cockroach-certs --ca-key=/cockroach/cockroach-keys/ca.key
}

function init-client {
    docker-compose run -T roach1 cert create-client $1 --certs-dir=/cockroach/cockroach-certs --ca-key=/cockroach/cockroach-keys/ca.key --also-generate-pkcs8-key
}

function permissions {
    if [ -e $NODE1_DIR ]; then
        sudo chown -R $USER:$USER $NODE1_DIR
    fi
}

function start {
    export NODE1_DIR=${1:-roach1}
    export DB_USER=${2:-maxroach}
    export DB=${3:-bank}

    down

    permissions

    rm -fr $NODE1_DIR

    mkdir -p $NODE1_DIR/{data,certs,keys}

    init-ca
    init-node
    init-client root
    init-client $DB_USER

    docker-compose up -d

    echo "CREATE USER IF NOT EXISTS $DB_USER;" | sql
    echo "CREATE DATABASE $DB;" | sql
    echo "GRANT ALL ON DATABASE $DB TO $DB_USER;" | sql

    permissions
}

$@
