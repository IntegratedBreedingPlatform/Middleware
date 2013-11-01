
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

-- workbench test data
INSERT INTO `workbench_crop` VALUES ('rice01','ibdbv2_rice_central');
INSERT INTO `workbench_dataset` VALUES (1,'Test Dataset','Test Dataset Description','2013-10-23',1,NULL);
INSERT INTO `workbench_ibdb_user_map` VALUES (1,1,3,-1);
INSERT INTO `workbench_project` VALUES (41,1,'project_rice','2013-10-23',1,0,'rice','ibdbv2_rice_1_local','ibdbv2_rice_central','2013-10-23 14:27:47');
INSERT INTO `workbench_project_activity` VALUES (1,1,'FieldBook','Launched FieldBook',1,'2013-10-23 14:28:16');
INSERT INTO `workbench_project_backup` VALUES (1,1,'target/resource','2013-10-23 16:10:41');
INSERT INTO `workbench_project_loc_map` VALUES (1,1,1);
INSERT INTO `workbench_project_method` VALUES (1,41,5);
INSERT INTO `workbench_project_user_info` VALUES (1,1,1,'2013-10-23 14:27:47');
INSERT INTO `workbench_user_info` VALUES (1,2);
INSERT INTO `workbench_ibdb_user_map` VALUES (1, 1, 3, -1);
INSERT INTO `workbench_security_question` VALUES (1,1,'What is your first pet\'s name?','petname');
