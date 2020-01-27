# fixture-db

This self-contained file contains the fixture-db database, a dump of workbench and ibdbv2_generic_merged databases.
The goal is to be work as data for both Middleware integration tests and API automated testing.

For the moment, it can be used also to run BMS. 
To log in use admin/admin.

You can load the db as of now using mysql import command.

## TODO

- [ ] Fix fixture-db changelog (mysql syntax errors)
- [ ] Finish maven fixture-db profile 
and run with
```bash
mvn -Pfixture-db \
    -Ddb.username=<username> \
    -Ddb.password=<password> \
    -Ddb.host=<host> \
    -Ddb.port=<port>
```
- [ ] Migrate Integration tests from maize to generic db?

