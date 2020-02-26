Demonstrate basic postgres access using podman and clojure, with database, table, role, and user creation.

* Running

POSTGRES_PASSWORD=password ./start.sh db start
./start.sh db create
./start.sh db drop

(cd app; clj -m app.db DB_NAME sandbox DB_HOST localhost DB_USER app_user DB_PASSWORD password)

./start.sh db stop
