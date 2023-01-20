#!/bin/bash

# insecure but ok for devenv
if [ -z $PGPASSWORD ]; then
   echo "Using default postgres password"
   export PGPASSWORD=postgres
fi

docker stop file-uploader-postgres
echo "    Sleep 5s to let postgres stop"
sleep 5

# prints error if already exists. Note also that binds to localhost to limit external access.
docker run --name file-uploader-postgres -e POSTGRES_PASSWORD=${PGPASSWORD} -p 127.0.0.1:5432:5432/tcp -d postgres:14.2
docker start file-uploader-postgres

# let postgress start before executing other scripts
echo "    Sleep 5s to let postgres start"
sleep 5

echo "    Check log lines whether everything is ok"
docker logs -n 10 file-uploader-postgres
