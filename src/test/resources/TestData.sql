
-- for WorkbenchDataManager.getMethodsByProjectId()
-- ---- Assumption: There is a workbench project with id = 1

-- ---- If there are no entries in the Methods table:
INSERT INTO Methods
VALUES('1', 'GEN', 'S', 'UGM', 'UNKNOWN GENERATIVE METHOD SF', 'Unknown generative method for storing historic pedigrees for self fertilizing species.', '0', '0', '0', '0', '0', '1', '2', '19980610');

INSERT INTO Methods
VALUES('2', 'GEN', 'O', 'PGM', 'UNKNOWN GENERATIVE METHOD CF', 'Unknown generative method for storing historic pedigrees for cross fertilising species.', '0', '0', '0', '0', '0', '1', '0', '19980610');

INSERT INTO workbench_project_method(project_id, method_id)
VALUES('1', '1');
INSERT INTO workbench_project_method(project_id, method_id)
VALUES('1', '2');


-- for WorkbenchDataManager.addProjectUsers()
INSERT INTO workbench.users ( userid, instalid, ustatus, uaccess, utype, uname, upswd, personid, adate, cdate ) 
VALUES (1,0,0,0,0,'workbench','workbench',0,0,0);

