
-- for WorkbenchDataManager.getMethodsByProjectId()
-- ---- Assumption: There is a workbench project with id = 1
INSERT INTO workbench_project_method(project_id, method_id)
VALUES('1', '1');
INSERT INTO workbench_project_method(project_id, method_id)
VALUES('1', '2');


-- for WorkbenchDataManager.addProjectUsers()
INSERT INTO workbench.users ( userid, instalid, ustatus, uaccess, utype, uname, upswd, personid, adate, cdate ) 
VALUES (1,0,0,0,0,'workbench','workbench',0,0,0);



-- for CrossStudyDataManager.testGetEnvironmentsForGermplasmPairs() - for local database testing

USE ibdbv2_rice_local;

-- ADD germplasm entries to stock table for gid = -1, -2
INSERT IGNORE INTO stock(stock_id, dbxref_id, organism_id, name, uniquename, value, description, type_id, is_obsolete)
VALUES (-20, -1, NULL, 'name', 'unique name', 2000, NULL, 8300, 0), (-21, -2, NULL, 'name', 'unique name', 2000, NULL, 8300, 0);

-- ADD nd_experiment_stock entries to link gid = -1, -2 to nd_experiment with nd_experiment_id = -2, -3
INSERT IGNORE INTO nd_experiment_stock (nd_experiment_stock_id, nd_experiment_id, stock_id, type_id)
VALUES (-70, -2, -20, 1000), (-71, -3, -21, 1000);

-- ADD nd_geolocationprop entry for the nd_geolocation_id associated with nd_experiment_id = -2, -3
INSERT IGNORE INTO nd_geolocationprop (nd_geolocationprop_id, nd_geolocation_id, type_id, value, rank)
VALUES (-50, -1, 8190, -2, 0)  -- location.locid = -2
;

-- testGetUserByName
DELETE FROM workbench.users WHERE userid = 10;

INSERT INTO workbench.users (userid, instalid, ustatus,uaccess, utype, uname, upswd, personid, adate, cdate)
VALUES (10, 0, 0, 0, 0, 'workbench_test', '123456', 1, 0, 0);

-- testAddToolConfiguration
ALTER TABLE workbench_tool_config DROP FOREIGN KEY fk_tool_config_1; 

-- testGetWorkflowTemplateByName
DELETE FROM workbench_workflow_template WHERE template_id=5;
INSERT INTO workbench_workflow_template (template_id, name, user_defined)
VALUES
(5, 'Manager', 0);

-- testGetProjectLocationMapByProjectId
INSERT INTO workbench_project_loc_map (id, project_id, location_id)
VALUES (1, 1, 1);

-- testGetLocalIbdbUserId
DELETE FROM workbench.workbench_ibdb_user_map WHERE workbench_user_id = 1 AND project_id = 3;
INSERT INTO workbench.workbench_ibdb_user_map (workbench_user_id, project_id, ibdb_user_id)
VALUES
(1, 3, -1);
