echo off
REM a batch script to start the pia on win95/NT in a dos box using Java
REM if the name of the program to run java is not java (e.g. jre) change
REM the last line of this script

REM This script will attempt to set PIA_DIR properly if possible
REM Java must be on your path (or add it below)
REM Classpath is NOT set, we cd to the correct directory for finding the
REM class files (which means command line arguments with relative paths
REM won't work)

REM where is the PIA home??  If current dir is pia/bin then
if  "%PIA_DIR%"==""  goto  NOPIAHOME
set PIAHOME="%PIA_DIR%"
set PIACLASSES=%PIA_DIR%\src\java
goto ENDPIAHOME
REM
REM case where pia_dir is not set
REM
:NOPIAHOME
if  exist  .\pia.bat goto PIAHOMEFOUND
echo Can not find the PIA home directory
echo Please set the environment variable PIA_DIR to the directory
echo where the PIA is installed (e.g. set PIA_DIR=c:\PIA)
exit
goto ENDPIAHOME
REM
REM we are at the pia\bin dir
REM move up one
REM
:PIAHOMEFOUND 
set PIACLASSES=..\src\java
REM after changing directories PIAHOME is:
set PIAHOME=..\..


goto ENDPIAHOME
REM
:ENDPIAHOME

REM echo PIAHOME is set to %PIAHOME%


REM where should the users files go?? 
if  "%USR_DIR%"==""  goto  NOUSRDIR
set UHOME="%USR_DIR%"
goto ENDUSRDIR
:NOUSRDIR

REM USR_DIR not set default is the user's home\.pia -- 
REM  check for a home variable
if  "%HOME%"==""  goto  NOHOME
SET UHOME="%HOME%\.pia"
goto ENDHOME
:NOHOME
REM No Home variable use default of c:\PIAUSERS
SET UHOME=c:\PIAUSERS
if exists %UHOME% goto UEXISTS
mkdir %UHOME%
:UEXISTS
REM If USERNAME exists use that as next dir
if  "%USERNAME%"==""  goto  NOUSERNAME
SET UHOME="%UHOME%\%USERNAME%"
goto ENDHOME
REM else use username of nobody
SET UHOME="%UHOME%\nobody"
:ENDHOME
echo using %UHOME% as user directory for data files
echo (set environment variable %USR_DIR% to override)

:ENDUSRDIR

if exist "%UHOME%" goto UHEXISTS
echo Making data directory %UHOME%
mkdir "%UHOME%"
:UHEXISTS

REM make sure java on is on the path 
REM example: path=%path%;%JDKHOME%\bin

REM cd to PIA\src\java directory as there is no explicit CLASSPATH

echo Changing directories to %PIACLASSES%
cd %PIACLASSES%

echo Running java org.risource.pia.Pia -root %PIAHOME% -u %UHOME%
REM may need to change java to jre
java org.risource.pia.Pia -root %PIAHOME% -u %UHOME%

