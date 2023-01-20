REASSIGN OWNED BY "file-uploader-application" TO postgres;

REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM "file-uploader-application";

DROP DATABASE IF EXISTS file-uploader;

DROP ROLE IF EXISTS "file-uploader-role";

DROP USER IF EXISTS "file-uploader-application";
