@echo off
rem ============================================================
rem  Build project va dong goi lai thu muc run\
rem  -> run\CarConsignment.exe (kem san Java runtime, bam dup la chay)
rem  Chay lai file nay moi khi sua code.
rem ============================================================
setlocal
cd /d "%~dp0"

set "JPACKAGE=jpackage"
if defined JAVA_HOME set "JPACKAGE=%JAVA_HOME%\bin\jpackage.exe"

echo [1/3] Build bang Maven...
call "%~dp0mvnw.cmd" -q -B clean package
if errorlevel 1 goto :fail

echo [2/3] Dong goi bang jpackage...
"%JPACKAGE%" --type app-image --name CarConsignment --app-version 1.0.0 ^
    --dest target\jpackage ^
    --module-path "target\car-consignment.jar;target\libs" ^
    --module car.consignment/com.group6.consignment.MainApp
if errorlevel 1 goto :fail

echo [3/3] Cap nhat thu muc run\ ...
if exist run rmdir /s /q run
if exist run (
    echo Khong xoa duoc run\ - hay tat CarConsignment.exe dang mo roi chay lai file nay.
    goto :fail
)
move target\jpackage\CarConsignment run >nul
if errorlevel 1 goto :fail

echo.
echo Xong! Mo run\CarConsignment.exe de chay app.
pause
exit /b 0

:fail
echo.
echo Build that bai, xem loi o tren.
pause
exit /b 1
