\c futurama
SET ROLE futurama_secrets;
SET search_path TO secrets, public;

CREATE TYPE SecretType AS ENUM ('FF3', 'KEY_PAIR');
CREATE TABLE secrets (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name TEXT NOT NULL,
    rotation_started_at TIMESTAMPTZ NOT NULL,
    rotation_period TEXT NOT NULL,
    rotation_timezone TEXT NOT NULL,
    mode SecretType NOT NULL,
    settings JSONB
);
CREATE UNIQUE INDEX idx_secrets_name_uniqueness ON secrets(name);

CREATE TABLE dated_settings (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    secret_id BIGINT REFERENCES secrets(id),
    tz_range TSTZRANGE NOT NULL,
    settings JSONB
);
ALTER TABLE dated_settings
    ADD CONSTRAINT dated_settings_tz_range
    EXCLUDE  USING gist (secret_id WITH =, tz_range WITH &&);