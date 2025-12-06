@echo off
cls
echo ================================================
echo   Building Portable AIS with JavaFX Modules
echo ================================================
echo.

REM Step 1: Build
echo [Step 1/3] Building JAR...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)

REM Step 2: Prepare runtime with JavaFX modules
echo [Step 2/3] Creating custom JRE with JavaFX...

REM Get JavaFX module path from Maven
set JAVAFX_MODS=%USERPROFILE%\.m2\repository\org\openjfx\javafx-base\21\javafx-base-21-win.jar
set JAVAFX_MODS=%JAVAFX_MODS%;%USERPROFILE%\.m2\repository\org\openjfx\javafx-graphics\21\javafx-graphics-21-win.jar
set JAVAFX_MODS=%JAVAFX_MODS%;%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\21\javafx-controls-21-win.jar

REM Create custom runtime with jlink
jlink --add-modules java.base,java.desktop,java.sql,java.naming,java.xml,javafx.controls,javafx.graphics,javafx.base,jdk.crypto.ec,jdk.unsupported ^
  --module-path "%JAVA_HOME%\jmods;%JAVAFX_MODS%" ^
  --output target\custom-runtime ^
  --strip-debug ^
  --no-header-files ^
  --no-man-pages ^
  --compress=2

if %errorlevel% neq 0 (
    echo [ERROR] jlink failed!
    pause
    exit /b 1
)

REM Step 3: Package with custom runtime
echo [Step 3/3] Creating portable application...

if exist "target\jpackage-input" rd /s /q target\jpackage-input
mkdir target\jpackage-input
copy /Y target\academic-information-system-1.0.jar target\jpackage-input\
copy /Y target\libs\*.jar target\jpackage-input\

jpackage ^
  --type app-image ^
  --name "AIS" ^
  --app-version 1.0.0 ^
  --input target\jpackage-input ^
  --main-jar academic-information-system-1.0.jar ^
  --main-class ais.Main ^
  --runtime-image target\custom-runtime ^
  --dest target\dist ^
  --java-options "-Xmx1024m"

if %errorlevel% neq 0 (
    echo [ERROR] jpackage failed!
    pause
    exit /b 1
)

echo.
echo ================================================
echo   SUCCESS!
echo ================================================
echo.
start "" target\dist\AIS\AIS.exe
pause
