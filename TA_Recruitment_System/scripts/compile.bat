@echo off
echo ========================================
echo Compiling TA Recruitment System
echo ========================================

rem 设置项目根目录（脚本所在目录的上一级）
set PROJECT_ROOT=%~dp0..
set SRC_DIR=%PROJECT_ROOT%\src
set WEB_DIR=%PROJECT_ROOT%\web
set CLASSES_DIR=%WEB_DIR%\WEB-INF\classes
set LIB_DIR=%WEB_DIR%\WEB-INF\lib

rem 清理并重建 classes 目录
if exist "%CLASSES_DIR%" rmdir /s /q "%CLASSES_DIR%"
mkdir "%CLASSES_DIR%"

rem 设置 classpath：包含第三方 jar 和 servlet-api.jar
set CLASSPATH=%LIB_DIR%\gson-2.10.1.jar;%CATALINA_HOME%\lib\servlet-api.jar

rem 收集所有需要编译的 Java 文件（递归查找 src 下所有 .java）
dir /s /b "%SRC_DIR%\*.java" > "%TEMP%\sources.txt"

echo Compiling...
javac -cp "%CLASSPATH%" -d "%CLASSES_DIR%" -encoding UTF-8 @"%TEMP%\sources.txt"

if errorlevel 1 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
) else (
    echo [SUCCESS] Compiled successfully.
    del "%TEMP%\sources.txt"
)

pause