@echo off
set PATH=C:\Users\Shubh\apache-maven-3.9.6\bin;%PATH%
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot
set DB_URL=jdbc:postgresql://localhost:5432/expensedb
set DB_USERNAME=postgres
set DB_PASSWORD=password
set JWT_SECRET=your-secure-jwt-secret-minimum-32-characters-for-testing
cd C:\Users\Shubh\Cloud_project
mvn spring-boot:run
