CREATE DATABASE "file-uploader";
CREATE ROLE "file-uploader-role";
CREATE USER "file-uploader-application" WITH PASSWORD 'postgres';
GRANT "file-uploader-role" TO "file-uploader-application", "postgres";
GRANT ALL PRIVILEGES ON DATABASE "file-uploader" TO "file-uploader-role";
