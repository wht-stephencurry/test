@echo off
setlocal enabledelayedexpansion

rem ============================================================
rem  本机 Maven 启动器（项目未安装独立 Maven，复用 IDEA 自带 Maven 3.9.9）
rem  用法与本机 mvn 完全一致，例如：mvn.cmd -q -DskipTests package
rem ============================================================

set "PROJECT_DIR=%~dp0"

if "%JAVA_HOME%"=="" set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"

set "MAVEN_HOME=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.1\plugins\maven\lib\maven3"

if not exist "%MAVEN_HOME%\boot\plexus-classworlds-2.8.0.jar" (
    where mvn >nul 2>nul
    if !errorlevel! equ 0 (
        mvn %*
        exit /b !errorlevel!
    )
    echo [mvn.cmd] Maven not found.
    echo [mvn.cmd] Install Maven and add it to PATH, or edit MAVEN_HOME in this file.
    exit /b 1
)

"%JAVA_HOME%\bin\java.exe" -classpath "%MAVEN_HOME%\boot\plexus-classworlds-2.8.0.jar" "-Dclassworlds.conf=%MAVEN_HOME%\bin\m2.conf" "-Dmaven.home=%MAVEN_HOME%" "-Dmaven.multiModuleProjectDirectory=%PROJECT_DIR%." org.codehaus.plexus.classworlds.launcher.Launcher %*
exit /b %errorlevel%
