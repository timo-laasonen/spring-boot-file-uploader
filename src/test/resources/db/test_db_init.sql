CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- own test database user
CREATE USER "file-uploader-application-test" WITH PASSWORD 'test';

CREATE ROLE "file-uploader-role";
GRANT "file-uploader-role" TO "file-uploader-application-test";
GRANT ALL PRIVILEGES ON DATABASE "file-uploader-test" TO "file-uploader-role";
