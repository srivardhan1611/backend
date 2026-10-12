@echo off
REM Starts the backend. Edit DB_USER / DB_PASSWORD to match your MySQL.
set DB_USER=root
set DB_PASSWORD=system
mvn spring-boot:run
