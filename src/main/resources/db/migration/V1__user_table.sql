CREATE TABLE user_info
(
    id                  UUID PRIMARY KEY,
    first_name          TEXT,
    last_name           TEXT,
    username            TEXT NOT NULL UNIQUE,
    registration_number VARCHAR(128) NOT NULL UNIQUE,
    created_by          CHARACTER VARYING(128) NOT NULL DEFAULT 'system',
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    modified_by         CHARACTER VARYING(128) NOT NULL DEFAULT 'system',
    modified_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

