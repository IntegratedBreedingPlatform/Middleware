package org.generationcp.middleware.v2.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.v2.pojos.CVTermId;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;


public class CVTermRelationshipHelper {

	private Map<Integer, String> propertyMap = new HashMap<Integer, String>();
	private Map<Integer, String> methodMap = new HashMap<Integer, String>();
	private Map<Integer, String> scaleMap = new HashMap<Integer, String>();
	private Map<Integer, String> dataTypeMap = new HashMap<Integer, String>();
	private Map<Integer, Integer> storedInMap = new HashMap<Integer, Integer>();
	
	private List<Integer> NUMERIC_FIELDS = Arrays.asList(
			CVTermId.NUMERIC_VARIABLE.getId(), CVTermId.NUMERIC_DBID_VARIABLE.getId(), CVTermId.DATE_VARIABLE.getId()
	);
	
	private List<Integer> CHARACTER_FIELDS = Arrays.asList(
			CVTermId.CHARACTER_DBID_VARIABLE.getId(), CVTermId.CHARACTER_VARIABLE.getId()
	);
	
	public CVTermRelationshipHelper(List<CVTermRelationship> relationships) {
		translateRelationshipsToMaps(relationships);
	}
	
	private void translateRelationshipsToMaps(List<CVTermRelationship> relationships) {
		if (relationships != null) {
			//TODO query cvterm for the names..
			for (CVTermRelationship relationship : relationships) {
				if (CVTermId.HAS_PROPERTY.getId().equals(relationship.getTypeId())) {
					propertyMap.put(relationship.getSubjectId().intValue(), relationship.getObjectId().toString());
					
				} else if (CVTermId.HAS_METHOD.getId().equals(relationship.getTypeId())) {
					methodMap.put(relationship.getSubjectId().intValue(), relationship.getObjectId().toString());
				
				} else if (CVTermId.HAS_SCALE.getId().equals(relationship.getTypeId())) {
					scaleMap.put(relationship.getSubjectId().intValue(), relationship.getObjectId().toString());
				
				} else if (CVTermId.HAS_TYPE.getId().equals(relationship.getTypeId())) {
					if (NUMERIC_FIELDS.contains(relationship.getObjectId())) {
						dataTypeMap.put(relationship.getSubjectId().intValue(), "N");
					
					} else if (CHARACTER_FIELDS.contains(relationship.getObjectId())) {
						dataTypeMap.put(relationship.getSubjectId().intValue(), "C");
						
					}
					
				} else if (CVTermId.STORED_IN.getId().equals(relationship.getTypeId())) {
					storedInMap.put(relationship.getSubjectId().intValue(), relationship.getObjectId());
				}
			}
		}
	}
	
	public String getProperty(Integer varId) {
		return propertyMap.get(varId);
	}
	
	public String getMethod(Integer varId) {
		return methodMap.get(varId);
	}
	
	public String getScale(Integer varId) {
		return scaleMap.get(varId);
	}
	
	public String getDataType(Integer varId) {
		return dataTypeMap.get(varId);
	}
	
	public Integer getStoredIn(Integer varId) {
		return storedInMap.get(varId);
	}
	
}
