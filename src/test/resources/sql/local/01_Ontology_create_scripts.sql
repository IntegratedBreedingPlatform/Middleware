DROP TABLE IF EXISTS `cv`;

CREATE TABLE `cv` (
  `cv_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `definition` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cv_id`),
  UNIQUE KEY `cv_idx1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `cvterm` */

DROP TABLE IF EXISTS `cvterm`;

CREATE TABLE `cvterm` (
  `cvterm_id` int(11) NOT NULL,
  `cv_id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `definition` varchar(255) DEFAULT NULL,
  `dbxref_id` int(11) DEFAULT NULL,
  `is_obsolete` int(11) NOT NULL DEFAULT '0',
  `is_relationshiptype` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`cvterm_id`),
  UNIQUE KEY `cvterm_idx1` (`name`,`cv_id`,`is_obsolete`),
  UNIQUE KEY `cvterm_idx2` (`dbxref_id`),
  KEY `cvterm_idx3` (`cv_id`),
  KEY `cvterm_idx4` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `cvterm_relationship` */

DROP TABLE IF EXISTS `cvterm_relationship`;

CREATE TABLE `cvterm_relationship` (
  `cvterm_relationship_id` int(11) NOT NULL AUTO_INCREMENT,
  `type_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `object_id` int(11) NOT NULL,
  PRIMARY KEY (`cvterm_relationship_id`),
  UNIQUE KEY `cvterm_relationship_c1` (`subject_id`,`object_id`,`type_id`),
  KEY `cvterm_relationship_idx1` (`type_id`),
  KEY `cvterm_relationship_idx2` (`subject_id`),
  KEY `cvterm_relationship_idx3` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19241 DEFAULT CHARSET=utf8;


/*Table structure for table `cvtermprop` */

DROP TABLE IF EXISTS `cvtermprop`;

CREATE TABLE `cvtermprop` (
  `cvtermprop_id` int(11) NOT NULL AUTO_INCREMENT,
  `cvterm_id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  `value` varchar(200) NOT NULL DEFAULT '',
  `rank` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`cvtermprop_id`),
  UNIQUE KEY `cvtermprop_c1` (`cvterm_id`,`type_id`,`value`,`rank`),
  KEY `cvtermprop_idx1` (`cvterm_id`),
  KEY `cvtermprop_idx2` (`type_id`),
  CONSTRAINT `cvtermprop_ibfk_1` FOREIGN KEY (`cvterm_id`) REFERENCES `cvterm` (`cvterm_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8011 DEFAULT CHARSET=utf8;

/*Table structure for table `cvtermsynonym` */

DROP TABLE IF EXISTS `cvtermsynonym`;

CREATE TABLE `cvtermsynonym` (
  `cvtermsynonym_id` int(11) NOT NULL AUTO_INCREMENT,
  `cvterm_id` int(11) NOT NULL,
  `synonym` varchar(200) NOT NULL,
  `type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`cvtermsynonym_id`),
  UNIQUE KEY `cvtermsynonym_c1` (`cvterm_id`,`synonym`),
  KEY `cvtermsynonym_idx1` (`type_id`),
  KEY `cvtermsynonym_idx2` (`cvterm_id`),
  CONSTRAINT `cvtermsynonym_fk1` FOREIGN KEY (`cvterm_id`) REFERENCES `cvterm` (`cvterm_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2721 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `project`;

CREATE TABLE `project` (
  `project_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `project_idx1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT IGNORE INTO `project`(`project_id`,`name`,`description`) VALUES (1, 'STUDIES', 'Root study folder');


DROP TABLE IF EXISTS `projectprop`;

CREATE TABLE `projectprop` (
  `projectprop_id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `rank` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`projectprop_id`),
  UNIQUE KEY `projectprop_idx1` (`project_id`,`type_id`,`rank`),
  KEY `projectprop_idx2` (`type_id`),
  CONSTRAINT `projectprop_fk1` FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* Convenience view for efficient reading of StandardVariable summary information */
drop view if exists standard_variable_summary;
create view standard_variable_summary as

	select  cvt.cvterm_id as 'id', 
		cvt.name as 'name', 			
		cvt.definition as 'definition',

		group_concat(if(cvtr.type_id=1200, cvtr.object_id, null)) as 'property_id',
		group_concat(if(cvtr.type_id=1200, prop.name, null)) as 'property_name',
		group_concat(if(cvtr.type_id=1200, prop.definition, null)) as 'property_def',

		group_concat(if(cvtr.type_id=1210, cvtr.object_id, null)) as 'method_id',
		group_concat(if(cvtr.type_id=1210, method.name, null)) as 'method_name',
		group_concat(if(cvtr.type_id=1210, method.definition, null)) as 'method_def',

		group_concat(if(cvtr.type_id=1220, cvtr.object_id, null)) as 'scale_id',
		group_concat(if(cvtr.type_id=1220, scale.name, null)) as 'scale_name',
		group_concat(if(cvtr.type_id=1220, scale.definition, null)) as 'scale_def',

		group_concat(if(cvtr.type_id=1225, cvtr.object_id, null)) as 'is_a_id',			
		group_concat(if(cvtr.type_id=1225, isa.name, null)) as 'is_a_name',
		group_concat(if(cvtr.type_id=1225, isa.definition, null)) as 'is_a_def',

		group_concat(if(cvtr.type_id=1044, cvtr.object_id, null)) as 'stored_in_id',			
		group_concat(if(cvtr.type_id=1044, storedin.name, null)) as 'stored_in_name',
		group_concat(if(cvtr.type_id=1044, storedin.definition, null)) as 'stored_in_def',

		group_concat(if(cvtr.type_id=1105, cvtr.object_id, null)) as 'data_type_id',			
		group_concat(if(cvtr.type_id=1105, datatype.name, null)) as 'data_type_name',
		group_concat(if(cvtr.type_id=1105, datatype.definition, null)) as 'data_type_def',

		group_concat(
		case 
			when cvtr.type_id=1044 and cvtr.object_id in (1010,1011,1012) then 'STUDY'
			when cvtr.type_id=1044 and cvtr.object_id in (1015,1016,1017) then 'DATASET'
			when cvtr.type_id=1044 and cvtr.object_id in (1020,1021,1022,1023,1024,1025) then 'TRIAL_ENVIRONMENT'
			when cvtr.type_id=1044 and cvtr.object_id = 1030 then 'TRIAL_DESIGN'
			when cvtr.type_id=1044 and cvtr.object_id in (1040,1041,1042,1046,1047) then 'GERMPLASM'
			when cvtr.type_id=1044 and cvtr.object_id in (1043, 1048) then 'VARIATE'				
		end) as 'phenotypic_type',

		group_concat(
		case
			when cvtr.type_id=1105 and cvtr.object_id in (1120,1125,1128,1130) then 'C'
			when cvtr.type_id=1105 and cvtr.object_id in (1110,1117,1118) then 'N'
			else null
	    end) as 'data_type_abbrev'

from cvterm cvt
	left join cvterm_relationship cvtr on cvtr.subject_id = cvt.cvterm_id
	left join cvterm prop on prop.cvterm_id = cvtr.object_id
	left join cvterm method on method.cvterm_id = cvtr.object_id
	left join cvterm scale on scale.cvterm_id = cvtr.object_id
	left join cvterm isa on isa.cvterm_id = cvtr.object_id
	left join cvterm storedin on storedin.cvterm_id = cvtr.object_id
	left join cvterm datatype on datatype.cvterm_id = cvtr.object_id		

where cvt.cv_id = 1040
	group by cvt.cvterm_id, cvt.name
	order by id;


