@echo off
REM Gradle wrapper stub. Run `gradle wrapper --gradle-version 7.6` locally or install Gradle.
if "%GRADLE_HOME%"=="" (
  echo Gradle home not set. Please run 'gradle wrapper --gradle-version 7.6' or set GRADLE_HOME.
  exit /b 1
)
"%GRADLE_HOME%\bin\gradle" %*
