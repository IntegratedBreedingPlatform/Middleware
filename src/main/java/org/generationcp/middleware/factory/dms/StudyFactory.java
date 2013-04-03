package org.generationcp.middleware.factory.dms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

/**
 * Factory class used for creating the Study POJOs.
 * 
 * @author tippsgo
 *
 */
public class StudyFactory {
	
	private static final String CVT_STUDY_NAME = "STUDY - CONDUCTED (DBCV)";
	private static final String CVT_TITLE = "TITLE - ASSIGNED (TEXT)";
	private static final String CVT_PMKEY = "PROJECT MANAGEMENT KEY - ASSIGNED (TEXT)";
	private static final String CVT_OBJECTIVE = "OBJECTIVE - DESCRIBED (TEXT)";
	private static final String CVT_START_DATE = "START DATE - ASSIGNED (DATE)";
	private static final String CVT_END_DATE = "END DATE - ASSIGNED (DATE)";
	private static final String CVT_PRIMARY_INVESTIGATOR_ID = "PRINCIPAL INVESTIGATOR - ASSIGNED (DBID)";
	private static final String CVT_STUDY_TYPE = "STUDY - ASSIGNED (TYPE)";
	private static final String CVT_STATUS = "STUDY IP STATUS - ASSIGNED (TYPE)";

	/**
	 * Creates a Study POJO from a list of ProjectProperty POJOs.
	 * If there are more than one DmsProject associated with the ProjectProperty parameter,
	 * only one DmsProject will be created for, the rest will be ignored.
	 * Please use createStudies if more than one studies are expected.
	 * 
	 * @param properties
	 * @return
	 */
	public static Study createStudy(Collection<ProjectProperty> properties) { 
		
		List<Study> studies = createStudies(properties);
		
		//return the first element
		return studies != null && studies.size() > 0 ? studies.iterator().next() : null;
	}
	
	/**
	 * Creates a collection of Study POJOs given a Collection of ProjectProperty POJOs.
	 * 
	 * @param properties
	 * @return
	 */
	public static List<Study> createStudies(Collection<ProjectProperty> properties) {
		//map of <studyId, study>
		//this map contains the collection of studies created as we map them field by field.
		Map<Long, Study> studyMap = new HashMap<Long, Study>(); 
		
		if (properties != null && properties.size() > 0) {
			ProjectProperty property;
			for(Iterator<ProjectProperty> iterator = properties.iterator(); iterator.hasNext(); ) {
				property = iterator.next();
				
				//maps the ProjectProperty object to a field in the Study POJO.
				mapPropertyToStudyField(property, studyMap);
			}
		}
		
		return new ArrayList<Study>(studyMap.values());
	}
	
	/**
	 * Get the Study POJO from the Study Map.
	 * Also sets the Study.Id with the Id parameter.
	 * 
	 * @param studyMap
	 * @param id
	 * @return
	 */
	private static Study getStudyFromMap(Map<Long, Study> studyMap, Long id) {
		Study study = studyMap.get(id);
		if (study == null) {
			study = new Study();
			study.setId(Integer.valueOf(id.toString()));
			studyMap.put(id, study);
		}
		return study;
	}
	
	/**
	 * Maps the ProjectProperty POJO to a specific field in the Study POJO
	 * The field in the Study POJO is determined by the ProjectProperty.type field.
	 * The value in this field is taken from the ProjectProperty.value field.
	 * 
	 * @param property
	 * @param studyMap
	 */
	private static void mapPropertyToStudyField(ProjectProperty property, Map<Long, Study> studyMap) {
		if (property != null && property.getType() != null && property.getProject() != null) {
			
			String typeName = property.getType().getName();
			
			//Retrieves the Study from the Map using the ProjectProperty.Project.Id
			//And Sets Study.Id = ProjectProperty.Project.Id
			Study study = getStudyFromMap(studyMap, property.getProject().getDmsProjectId());
			
			if (CVT_STUDY_NAME.equals(typeName)) {
				study.setName(property.getValue());
				
			} else if (CVT_PMKEY.equals(typeName)) {
				study.setProjectKey(property.getValue() != null ? Integer.valueOf(property.getValue()) : null);
				
			} else if (CVT_TITLE.equals(typeName)) {
				study.setTitle(property.getValue());
				
			} else if (CVT_OBJECTIVE.equals(typeName)) {
				study.setObjective(property.getValue());
			
			} else if (CVT_PRIMARY_INVESTIGATOR_ID.equals(typeName)) {
				study.setPrimaryInvestigator(property.getValue() != null ? Integer.valueOf(property.getValue()) : null);
				
			} else if (CVT_STUDY_TYPE.equals(typeName)) {
				study.setType(property.getValue());
				
			} else if (CVT_START_DATE.equals(typeName)) {
				study.setStartDate(property.getValue() != null ? Integer.valueOf(property.getValue()) : null);
				
			} else if (CVT_END_DATE.equals(typeName)) {
				study.setEndDate(property.getValue() != null ? Integer.valueOf(property.getValue()) : null);
				
			} else if (CVT_STATUS.equals(typeName)) {
				study.setStatus(property.getValue() != null ? Integer.valueOf(property.getValue()) : null);
				
			}
		}
	}
	
}
