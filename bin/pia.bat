echo off
REM a batch script to start the pia on win95 in a dos box using a JRE
REM This script will attempt to set PIA_DIR properly if possible
REM You may need to indicate the location of your Java Developer Kit 
REM below (JDKHOME) if you do not have a Java Runtime (JRE) in your path
REM (Currently JDKHOME defaults to a directory as an example of how to 
REM set it.  This should not cause any problems unless an old version of
REM the JDK exists in that directory.)

REM where is the PIA home??  If current dir is pia/bin then
if  "%PIA_DIR%"==""  goto  NOPIAHOME
set PIAHOME="%PIA_DIR%"
goto ENDPIAHOME
REM
REM case where pia_dir is not set
REM
:NOPIAHOME
if  exist  .\pia.bat goto PIAHOMEFOUND
echo Can not find the PIA home directory
echo Please set the environment variable PIA_DIR to the directory
echo where the PIA is installed
exit
goto ENDPIAHOME
REM
REM we are at the pia\bin dir
REM move up one
REM
:PIAHOMEFOUND
set PIAHOME=..
set JDKHOME=%PIAHOME%\..\java\windows\jdk1.1.7
goto ENDPIAHOME
REM
:ENDPIAHOME
echo PIAHOME is set to %PIAHOME%
echo JDKHOME is set to %JDKHOME%

REM where should the users files go??
REM default is the java home-- probably not write
REM this uses c:\PIAUSERS\  --  should check for a home variable
REM SET UHOME=c:\PIAUSERS\%USERNAME%
REM
set USERNAME=nobody
if  "%USR_DIR%"==""  goto  NOUSRHOME
set UHOME="%USR_DIR%"
goto ENDUHOME
:NOUSRHOME
echo Can not find the user directory to write data.
echo Please set the environment variable USR_DIR to the directory
echo to where you want the PIAs' agents to write data.
echo
echo "Currently data is writen to c:\PIAUSERS\nobody"
mkdir c:\PIAUSERS
mkdir c:\PIAUSERS\%USERNAME%
set UHOME=c:\PIAUSERS\%USERNAME%
goto ENDUHOME
:ENDUHOME
echo UHOME is set to %UHOME%

REM tell where to find jre
path=%path%;%JDKHOME%\bin


REM set the path for the JIGSAW and REGEXP libraries
REM PIA uses a very small amount of code from these
SET REGEXP=%PIAHOME%\lib\java\regexp.jar
SET JIGSAW=%PIAHOME%\lib\java\jigsaw_s.jar

REM the classpath
SET CPATH=%JIGSAW%;%REGEXP%;%PIAHOME%\lib\java\pia.zip;%PIAHOME%\src\java
jre -cp %CPATH% crc.pia.Pia -root %PIAHOME% -u %UHOME%
