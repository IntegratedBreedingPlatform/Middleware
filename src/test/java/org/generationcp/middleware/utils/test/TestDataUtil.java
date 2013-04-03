package org.generationcp.middleware.utils.test;

import java.util.HashMap;
import java.util.Map;

import org.generationcp.middleware.pojos.dms.CV;
import org.generationcp.middleware.pojos.dms.CVTerm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class TestDataUtil {
	
	private static Map<String, CV> cvMap;
	private static Map<String, CVTerm> cvTermMap;
	
	static {
		initializeCvMap();
		initializeCvTermMap();
	}

	//================= CREATE REFERENTIAL DATA MAPS ===============================
	private static void initializeCvMap() {
		cvMap = new HashMap<String, CV>();
		
		//Create CV data (CvId, Name, Definition)
		createCv(1L, "IBDB TERMS", "TYPES USED IN THE IBDB DATA MODEL", true);
		createCv(5L, "OMS STDVARS", "STANDARD VARIABLES USED IN IBP PHENOTYPING DATA", true);
	}
	
	private static void initializeCvTermMap() {
		cvTermMap = new HashMap<String, CVTerm>();
		
		//Create CVTerm data (CvTermId, CvId, Name, Definition, DbxRefId, IsObsolete, IsRelationship)
		createCvTerm(2L, "OMS STDVARS", "TITLE - ASSIGNED (TEXT)", null, null, false, false, true);
		createCvTerm(3L, "OMS STDVARS", "PROJECT MANAGEMENT KEY - ASSIGNED (TEXT)", null, null, false, false, true);
		createCvTerm(4L, "OMS STDVARS", "OBJECTIVE - DESCRIBED (TEXT)", null, null, false, false, true);
		createCvTerm(5L, "OMS STDVARS", "START DATE - ASSIGNED (DATE)", null, null, false, false, true);
		createCvTerm(6L, "OMS STDVARS", "END DATE - ASSIGNED (DATE)", null, null, false, false, true);
		createCvTerm(7L, "OMS STDVARS", "STUDY - ASSIGNED (TYPE)", null, null, false, false, true);
		createCvTerm(8L, "OMS STDVARS", "STUDY - CONDUCTED (DBCV)", null, null, false, false, true);
		createCvTerm(12L, "OMS STDVARS", "STUDY INSTITUTE - CONDUCTED (DBCV)", null, null, false, false, true);
		createCvTerm(13L, "OMS STDVARS", "STUDY INSTITUTE ID - CONDUCTED (DBID)", null, null, false, false, true);
		createCvTerm(14L, "OMS STDVARS", "PRINCIPAL INVESTIGATOR - ASSIGNED (DBCV)", null, null, false, false, true);
		createCvTerm(15L, "OMS STDVARS", "PRINCIPAL INVESTIGATOR - ASSIGNED (DBID)", null, null, false, false, true);
		createCvTerm(16L, "OMS STDVARS", "STUDY IP STATUS - ASSIGNED (TYPE)", null, null, false, false, true);
		createCvTerm(17L, "OMS STDVARS", "STUDY RELEASE - ASSIGNED (DATE)", null, null, false, false, true);
		createCvTerm(1000L, "IBDB TERMS", "STUDY CONDITION", null, null, false, false, true);
		createCvTerm(1001L, "IBDB TERMS", "VARIABLE DESCRIPTION", null, null, false, false, true);
		createCvTerm(1002L, "IBDB TERMS", "STANDARDVARIABLEID", null, null, false, false, true);
	}

	
	//======================= UTILITY METHODS ======================================
	public static ProjectProperty createProjectProperty(Long id, DmsProject project, String type, String value, Long rank) {
		ProjectProperty property = new ProjectProperty();
		property.setProjectPropertyId(id);
		property.setProject(project);
		property.setType(cvTermMap.get(type));
		property.setValue(value);
		property.setRank(rank);
		return property;
	}
	
	public static DmsProject createProject(Long id, String name, String description) {
		DmsProject project = new DmsProject();
		project.setDmsProjectId(id);
		project.setName(name);
		project.setDescription(description);
		return project;
	}
	
	public static CVTerm createCvTerm(Long id, String cvName, String name, String definition, Long dbxRefId, Boolean isObsolete, Boolean isRelationshipType) {
		return createCvTerm(id, cvName, name, definition, dbxRefId, isObsolete, isRelationshipType, false);
	}
	
	public static CVTerm createCvTerm(Long id, String cvName, String name, String definition, Long dbxRefId, Boolean isObsolete, Boolean isRelationshipType, boolean insertToMap) {
		CVTerm term = new CVTerm();
		term.setCvTermId(id);
		term.setCv(cvMap.get(cvName));
		term.setName(name);
		term.setDefinition(definition);
		term.setDbxRefId(dbxRefId);
		term.setIsObsolete(isObsolete);
		term.setIsRelationshipType(isRelationshipType);
		if (insertToMap) {
			cvTermMap.put(name, term);
		}
		return term;
	}
	
	public static CV createCv(Long id, String name, String definition) {
		return createCv(id, name, definition, false);
	}
	
	private static CV createCv(Long id, String name, String definition, boolean insertToMap) {
		CV cv = new CV();
		cv.setCvId(id);
		cv.setName(name);
		cv.setDefinition(definition);
		if (insertToMap) {
			cvMap.put(name, cv);
		}
		return cv;
	}

	//===============   GETTERS ====================================
	
	public static Map<String, CV> getCvMap() {
		return cvMap;
	}

	public static Map<String, CVTerm> getCvTermMap() {
		return cvTermMap;
	}
	
}