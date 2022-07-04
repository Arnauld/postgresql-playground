CALL create_role_if_not_exists('futurama_r', 'WITH PASSWORD ''futurama_p'' LOGIN BYPASSRLS');

CREATE DATABASE futurama WITH TEMPLATE = template0
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    CONNECTION LIMIT = 255;

ALTER DATABASE futurama OWNER TO futurama_r;
GRANT CONNECT ON DATABASE futurama TO futurama_r;
