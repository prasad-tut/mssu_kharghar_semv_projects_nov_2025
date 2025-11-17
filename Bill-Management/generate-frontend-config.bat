@echo off
REM Generate frontend env-config.js from .env file

if not exist .env (
    echo Error: .env file not found
    exit /b 1
)

REM Read .env file and set variables
for /f "tokens=1,2 delims==" %%a in (.env) do (
    if not "%%a"=="" if not "%%a:~0,1%"=="#" (
        set %%a=%%b
    )
)

REM Generate env-config.js
(
echo // Environment configuration for frontend
echo // Auto-generated from .env file - DO NOT EDIT MANUALLY
echo.
echo window.ENV = {
echo     API_BASE_URL: '%API_BASE_URL%',
echo     ENVIRONMENT: '%SPRING_PROFILE%'
echo };
) > frontend\env-config.js

echo Generated frontend\env-config.js from .env
