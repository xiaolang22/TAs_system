@echo off
echo ========================================
echo Deploying TA Recruitment System to Tomcat
echo ========================================

rem 获取项目根目录
set PROJECT_ROOT=%~dp0..
set WEB_DIR=%PROJECT_ROOT%\web
set DATA_DIR=%PROJECT_ROOT%\data
set TOMCAT_APP_DIR=%CATALINA_HOME%\webapps\TA_Recruitment

rem 检查 Tomcat 目录是否存在
if not exist "%CATALINA_HOME%" (
    echo [ERROR] CATALINA_HOME is not set or Tomcat not found at %CATALINA_HOME%
    pause
    exit /b 1
)

rem 先编译
call "%~dp0compile.bat"
if errorlevel 1 (
    echo [ERROR] Compilation failed, deployment aborted.
    pause
    exit /b 1
)

rem 删除旧的部署目录（可选，避免残留）
if exist "%TOMCAT_APP_DIR%" (
    echo Removing old deployment...
    rmdir /s /q "%TOMCAT_APP_DIR%"
)

rem 创建新的应用目录
mkdir "%TOMCAT_APP_DIR%"

echo Copying web content...
xcopy "%WEB_DIR%\*" "%TOMCAT_APP_DIR%\" /E /I /Y /Q

echo Copying data directory...
xcopy "%DATA_DIR%" "%TOMCAT_APP_DIR%\data\" /E /I /Y /Q

rem 确保 uploads 目录存在（用于存放上传的 CV）
if not exist "%TOMCAT_APP_DIR%\uploads" (
    mkdir "%TOMCAT_APP_DIR%\uploads"
)

rem 删除本地的 classes 文件夹（避免提交到 Git 或残留）
set LOCAL_CLASSES_DIR=%WEB_DIR%\WEB-INF\classes
if exist "%LOCAL_CLASSES_DIR%" (
    echo Removing local classes folder: %LOCAL_CLASSES_DIR%
    rmdir /s /q "%LOCAL_CLASSES_DIR%"
)

echo [SUCCESS] Deployment finished.
echo You can now start Tomcat and visit: http://localhost:8080/TA_Recruitment/
pause