#!/bin/sh
export DB_USER=${DB_USER:-root}
export DB_PASSWORD=${DB_PASSWORD:-system}
mvn spring-boot:run
