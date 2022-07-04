\c futurama

CREATE SCHEMA IF NOT EXISTS app1;

--ROLE FUTURAMA_app1
CALL create_role_if_not_exists('futurama_app1', 'WITH PASSWORD ''app1_p'' SUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION BYPASSRLS');
ALTER SCHEMA app1 OWNER TO futurama_app1;
ALTER ROLE futurama_app1 IN DATABASE futurama SET search_path to app1, public;

--ROLE FUTURAMA_app1_APP
CALL create_role_if_not_exists('futurama_app1_app', 'WITH PASSWORD ''app1_p'' NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS');
ALTER ROLE futurama_app1_app IN DATABASE futurama SET search_path to app1, public;
GRANT SELECT, UPDATE, INSERT, DELETE ON ALL TABLES    IN SCHEMA app1 TO futurama_app1_app;
GRANT SELECT, UPDATE, USAGE          ON ALL SEQUENCES IN SCHEMA app1 TO futurama_app1_app;
GRANT USAGE                          ON                  SCHEMA app1 TO futurama_app1_app;
-- limit privileges that will be applied to objects created in the future, e.g. new tables
ALTER DEFAULT PRIVILEGES FOR ROLE futurama_app1 IN SCHEMA app1 GRANT SELECT, UPDATE, INSERT, DELETE ON TABLES    TO futurama_app1_app;
ALTER DEFAULT PRIVILEGES FOR ROLE futurama_app1 IN SCHEMA app1 GRANT SELECT, UPDATE, USAGE          ON SEQUENCES TO futurama_app1_app;

--ROLE FUTURAMA_app1_MIG
CALL create_role_if_not_exists('futurama_app1_mig', 'WITH PASSWORD ''app1_p'' SUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION BYPASSRLS');
ALTER ROLE futurama_app1_mig IN DATABASE futurama SET search_path to app1, public;
GRANT futurama_app1 TO futurama_app1_mig;

--ROLE app1_READONLY
CALL create_role_if_not_exists('futurama_app1_readonly', 'WITH PASSWORD ''app1_p'' NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS');
ALTER ROLE futurama_app1_readonly IN DATABASE futurama SET search_path to app1, public;
GRANT SELECT ON ALL TABLES IN SCHEMA app1 TO futurama_app1_readonly;
GRANT USAGE  ON               SCHEMA app1 TO futurama_app1_readonly;
