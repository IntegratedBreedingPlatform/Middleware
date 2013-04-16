package org.generationcp.middleware.v2.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.v2.pojos.CVTermId;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;


public class CVTermRelationshipHelper {

	//in Map<key, value> --> the <key> has a property of <value>
	private Map<Integer, Integer> propertyMap = new HashMap<Integer, Integer>();
	//in Map<key, value> --> the <key> has a method of <value>
	private Map<Integer, Integer> methodMap = new HashMap<Integer, Integer>();
	//in Map<key, value> --> the <key> has a scale of <value>
	private Map<Integer, Integer> scaleMap = new HashMap<Integer, Integer>();
	//in Map<key, value> --> the <key> has a datatype of <value>
	private Map<Integer, String> dataTypeMap = new HashMap<Integer, String>();
	
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
		for (CVTermRelationship relationship : relationships) {
			if (CVTermId.HAS_PROPERTY.getId().equals(relationship.getTypeId())) {
				propertyMap.put(relationship.getSubjectId().intValue(), relationship.getObjectId().intValue());
				
			} else if (CVTermId.HAS_METHOD.getId().equals(relationship.getTypeId())) {
				methodMap.put(relationship.getSubjectId().intValue(), relationship.getObjectId().intValue());
			
			} else if (CVTermId.HAS_SCALE.getId().equals(relationship.getTypeId())) {
				scaleMap.put(relationship.getSubjectId().intValue(), relationship.getObjectId().intValue());
			
			} else if (CVTermId.HAS_TYPE.getId().equals(relationship.getTypeId())) {
				if (NUMERIC_FIELDS.contains(relationship.getObjectId())) {
					dataTypeMap.put(relationship.getSubjectId().intValue(), "N");
				
				} else if (CHARACTER_FIELDS.contains(relationship.getObjectId())) {
					dataTypeMap.put(relationship.getSubjectId().intValue(), "C");
					
				}
			}
		}
	}
	
	public Integer getProperty(Integer varId) {
		return propertyMap.get(varId);
	}
	
	public Integer getMethod(Integer varId) {
		return methodMap.get(varId);
	}
	
	public Integer getScale(Integer varId) {
		return scaleMap.get(varId);
	}
	
	public String getDataType(Integer varId) {
		return dataTypeMap.get(varId);
	}
	
}
