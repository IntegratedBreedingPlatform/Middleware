:: Example setup
set USER=root
set PASSWORD=09C42tUc1Z
set PORT=43306
set MYSQL_CONNECTOR="C:\BMS4\tools\mysql-connector-java-5.1.44\mysql-connector-java-5.1.44-bin.jar"
:: No spaces, no quotes. use mklink /J if necessary
set MYSQL_PATH=C:/BMS4/infrastructure/mysql/bin/mysql
set LiquibaseJar="C:/BMS4/jre/lib/liquibase-core-3.5.3.jar"

set PASSCOMMAND=-p%PASSWORD%
if "%PASSWORD%"=="" set PASSCOMMAND=""



:: crops

for /F "delims=" %%G in (
        '%MYSQL_PATH% -N -u %USER% %PASSCOMMAND% --port=%PORT% -e "SELECT db_name FROM workbench.workbench_crop"'
    ) do (
        java -jar %LiquibaseJar%^
            --classpath=%MYSQL_CONNECTOR%^
            --driver=com.mysql.jdbc.Driver^
            --changeLogFile=../liquibase/crop_master.xml^
            --url="jdbc:mysql://localhost:%PORT%/%%G"^
            --logLevel=debug^
            --username=%USER%^
            --password=%PASSWORD%^
            update
    )

