package org.generationcp.middleware.v2.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.CVTermId;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;


public class CVTermRelationshipHelper {

	private Map<Integer, String> propertyMap = new HashMap<Integer, String>();
	private Map<Integer, String> methodMap = new HashMap<Integer, String>();
	private Map<Integer, String> scaleMap = new HashMap<Integer, String>();
	private Map<Integer, String> dataTypeMap = new HashMap<Integer, String>();
	private Map<Integer, Integer> storedInMap = new HashMap<Integer, Integer>();
	
	private CVTermHelper termsHelper;
	
	private List<Integer> NUMERIC_FIELDS = Arrays.asList(
			CVTermId.NUMERIC_VARIABLE.getId(), CVTermId.NUMERIC_DBID_VARIABLE.getId(), CVTermId.DATE_VARIABLE.getId()
	);
	
	private List<Integer> CHARACTER_FIELDS = Arrays.asList(
			CVTermId.CHARACTER_DBID_VARIABLE.getId(), CVTermId.CHARACTER_VARIABLE.getId()
	);
	
	public CVTermRelationshipHelper(Collection<CVTermRelationship> relationships, Collection<CVTerm> terms) {
		termsHelper = new CVTermHelper(terms);
		translateRelationshipsToMaps(relationships);
	}
	
	private void translateRelationshipsToMaps(Collection<CVTermRelationship> relationships) {
		if (relationships != null) {
			for (CVTermRelationship relationship : relationships) {
				if (CVTermId.HAS_PROPERTY.getId().equals(relationship.getTypeId())) {
					propertyMap.put(relationship.getSubjectId(), termsHelper.getName(relationship.getObjectId()));
					
				} else if (CVTermId.HAS_METHOD.getId().equals(relationship.getTypeId())) {
					methodMap.put(relationship.getSubjectId(), termsHelper.getName(relationship.getObjectId()));
				
				} else if (CVTermId.HAS_SCALE.getId().equals(relationship.getTypeId())) {
					scaleMap.put(relationship.getSubjectId(), termsHelper.getName(relationship.getObjectId()));
				
				} else if (CVTermId.HAS_TYPE.getId().equals(relationship.getTypeId())) {
					if (NUMERIC_FIELDS.contains(relationship.getObjectId())) {
						dataTypeMap.put(relationship.getSubjectId(), "N");
					
					} else if (CHARACTER_FIELDS.contains(relationship.getObjectId())) {
						dataTypeMap.put(relationship.getSubjectId(), "C");
						
					}
					
				} else if (CVTermId.STORED_IN.getId().equals(relationship.getTypeId())) {
					storedInMap.put(relationship.getSubjectId(), relationship.getObjectId());
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
