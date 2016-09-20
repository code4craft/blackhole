SET PR_PATH=%CD%

SET PR_EXE=prunsrv.exe

SET PR_SERVICE_NAME=blackHoleDNS


REM %PR_PATH%\%PR_EXE% //IS//%PR_SERVICE_NAME% --DisplayName="%PR_DISPLAY_NAME%" --Description="%PR_DESCRIPTION%" --Install="%PR_PATH%\%PR_EXE%" --JvmMs=2000 --JvmMx=2000 --JvmSs=1024 ++DependsOn=MSSQLSERVER --Jvm=auto --Startup=auto --StartMode=jvm --StartClass=%START_CLASS% --StartMethod=%START_METHOD% --StopMode=jvm --StopClass=%STOP_CLASS% --StopMethod=%STOP_METHOD% ++StopParams=%STOP_PARAMS% --Classpath="%PR_PATH%\%PR_JAR%"
%PR_PATH%\%PR_EXE% run  %PR_SERVICE_NAME% 

