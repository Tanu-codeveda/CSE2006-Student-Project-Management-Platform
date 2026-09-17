@echo off
setlocal

if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot" (
        set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
    )
)

if not "%JAVA_HOME%"=="" (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

if exist "C:\Users\tanug\maven\apache-maven-3.9.6\bin\mvn.cmd" (
    call "C:\Users\tanug\maven\apache-maven-3.9.6\bin\mvn.cmd" %*
    goto :end
)

mvn %*
:end
exit /b %ERRORLEVEL%
