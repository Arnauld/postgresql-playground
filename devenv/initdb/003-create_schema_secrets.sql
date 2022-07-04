\c futurama

CREATE SCHEMA IF NOT EXISTS secrets;
CALL create_role_if_not_exists('futurama_secrets',          'WITH PASSWORD ''secrets_p''   SUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION   BYPASSRLS');
CALL create_role_if_not_exists('futurama_secrets_app',      'WITH PASSWORD ''secrets_p'' NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB   LOGIN NOREPLICATION NOBYPASSRLS');
CALL create_role_if_not_exists('futurama_secrets_mig',      'WITH PASSWORD ''secrets_p'' NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB   LOGIN NOREPLICATION   BYPASSRLS');
CALL create_role_if_not_exists('futurama_secrets_readonly', 'WITH PASSWORD ''secrets_p'' NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB   LOGIN NOREPLICATION NOBYPASSRLS');

--ROLE FUTURAMA_secrets
ALTER SCHEMA secrets OWNER TO futurama_secrets;
ALTER ROLE futurama_secrets IN DATABASE futurama SET search_path to secrets, public;

--ROLE FUTURAMA_secrets_APP
ALTER ROLE futurama_secrets_app IN DATABASE futurama SET search_path to secrets, public;
GRANT SELECT, UPDATE, INSERT, DELETE ON ALL TABLES    IN SCHEMA secrets TO futurama_secrets_app;
GRANT SELECT, UPDATE, USAGE          ON ALL SEQUENCES IN SCHEMA secrets TO futurama_secrets_app;
GRANT USAGE                          ON                  SCHEMA secrets TO futurama_secrets_app;
-- limit privileges that will be applied to objects created in the future, e.g. new tables
ALTER DEFAULT PRIVILEGES FOR ROLE futurama_secrets IN SCHEMA secrets GRANT SELECT, UPDATE, INSERT, DELETE ON TABLES    TO futurama_secrets_app;
ALTER DEFAULT PRIVILEGES FOR ROLE futurama_secrets IN SCHEMA secrets GRANT SELECT, UPDATE, USAGE          ON SEQUENCES TO futurama_secrets_app;

--ROLE FUTURAMA_secrets_MIG
ALTER ROLE futurama_secrets_mig IN DATABASE futurama SET search_path to secrets, public;
GRANT futurama_secrets TO futurama_secrets_mig;

--ROLE secrets_READONLY
ALTER ROLE futurama_secrets_readonly IN DATABASE futurama SET search_path to secrets, public;
GRANT SELECT ON ALL TABLES IN SCHEMA secrets TO futurama_secrets_readonly;
GRANT USAGE  ON               SCHEMA secrets TO futurama_secrets_readonly;
