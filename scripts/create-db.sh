#!/bin/bash
CWD=`cd $(dirname $0) && pwd`
echo "CWD=$CWD"

# insecure but ok for devenv
if [ -z $PGPASSWORD ]; then
   echo "Using default postgres password"
   export PGPASSWORD=postgres
fi

echo "Drop test db"
psql -h localhost -p 5432 -U postgres postgres -f $CWD/drop-db.sql

echo "--- Create db"
psql -h localhost -p 5432 -U postgres postgres -f $CWD/create-db-local.sql

echo "--- DONE"
