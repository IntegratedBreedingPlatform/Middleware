
-- for WorkbenchDataManager.getMethodsByProjectId()
-- ---- Assumption: There is a workbench project with id = 1
INSERT INTO workbench_project_method(project_id, method_id)
VALUES('1', '1');
INSERT INTO workbench_project_method(project_id, method_id)
VALUES('1', '2');


-- for WorkbenchDataManager.addProjectUsers()
INSERT INTO workbench.users ( userid, instalid, ustatus, uaccess, utype, uname, upswd, personid, adate, cdate ) 
VALUES (1,0,0,0,0,'workbench','workbench',0,0,0);

