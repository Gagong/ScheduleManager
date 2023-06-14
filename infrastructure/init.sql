CREATE DATABASE ScheduleManager;
DO
    $do$
    BEGIN
        IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'schedule_manager_app') THEN
            RAISE NOTICE 'Role "schedule_manager_app" already exists. Skipping.';
        ELSE
            CREATE USER schedule_manager_app with encrypted password 'Qwerty$4';
        END IF;
    END
$do$;
GRANT ALL PRIVILEGES ON DATABASE ScheduleManager TO schedule_manager_app;