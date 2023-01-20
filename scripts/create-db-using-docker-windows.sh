#!/bin/bash
CWD=`cd $(dirname $0) && pwd`
echo "CWD=$CWD"

echo "Create dev db"
docker cp $CWD/create-db.sh file-uploader-postgres:/
docker cp $CWD/drop-db.sql file-uploader-postgres:/
docker cp $CWD/create-db-local.sql file-uploader-postgres:/
docker exec file-uploader-postgres sh -c ./create-db.sh



