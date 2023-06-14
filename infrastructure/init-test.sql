CREATE DATABASE ScheduleManagerTest;
DO
    $do$
    BEGIN
        IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'schedule_manager_test') THEN
            RAISE NOTICE 'Role "schedule_manager_test" already exists. Skipping.';
        ELSE
            CREATE USER schedule_manager_test with encrypted password 'Qwerty$4';
        END IF;
    END
$do$;
GRANT ALL PRIVILEGES ON DATABASE ScheduleManagerTest TO schedule_manager_test;