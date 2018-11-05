SET PR_PATH=%CD%

SET PR_EXE=prunsrv.exe

SET PR_SERVICE_NAME=blackHoleDNS

SET PR_DISPLAY_NAME=blackHoleDNS

SET PR_DESCRIPTION="blackHoleDNS for umu360.com"

SET PR_JAR=blackhole-1.2.2.jar

SET START_PATH=%CD%

SET START_CLASS=us.codecraft.blackhole.BlackHole
SET START_METHOD=main
SET STOP_CLASS=java.lang.System
SET STOP_METHOD=exit

SET STOP_PARAMS=0



REM %PR_PATH%\%PR_EXE% //IS//%PR_SERVICE_NAME% --DisplayName="%PR_DISPLAY_NAME%" --Description="%PR_DESCRIPTION%" --Install="%PR_PATH%\%PR_EXE%" --JvmMs=2000 --JvmMx=2000 --JvmSs=1024 ++DependsOn=MSSQLSERVER --Jvm=auto --Startup=auto --StartMode=jvm --StartClass=%START_CLASS% --StartMethod=%START_METHOD% --StopMode=jvm --StopClass=%STOP_CLASS% --StopMethod=%STOP_METHOD% ++StopParams=%STOP_PARAMS% --Classpath="%PR_PATH%\%PR_JAR%"
%PR_PATH%\%PR_EXE% //IS//%PR_SERVICE_NAME% --DisplayName="%PR_DISPLAY_NAME%" --Description=%PR_DESCRIPTION% --Install="%PR_PATH%\%PR_EXE%" --JvmMs=2000 --JvmMx=2000 --JvmSs=1024  --Jvm=auto --Startup=auto --StartMode=jvm --StartClass=%START_CLASS% --StartMethod=%START_METHOD% --StopMode=jvm --StopClass=%STOP_CLASS% --StopMethod=%STOP_METHOD% ++StopParams=%STOP_PARAMS% --Classpath="%PR_PATH%\%PR_JAR%" ++JvmOptions=-Djava.io.tmpdir=%PR_PATH% ++JvmOptions=-Dd=%PR_PATH% --StartPath=%START_PATH% ++StartParams=-d%START_PATH%
%PR_PATH%\%PR_EXE% //ES//%PR_SERVICE_NAME%
