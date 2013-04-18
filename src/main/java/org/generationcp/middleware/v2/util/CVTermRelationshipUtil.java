package org.generationcp.middleware.v2.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.v2.pojos.CVTermRelationship;

public class CVTermRelationshipUtil {

	
	public static Set<Integer> extractObjectTermIds(List<CVTermRelationship> relationships) {
		return extractObjectTermIds(relationships, null);
	}
	
	public static Set<Integer> extractLocalObjectTermIds(List<CVTermRelationship> relationships) {
		return extractObjectTermIds(relationships, true);
	}
	
	public static Set<Integer> extractCentralObjectTermIds(List<CVTermRelationship> relationships) {
		return extractObjectTermIds(relationships, false);
	}
	
	//if local is null, then get all
	//if local is true, then get only the negative Ids
	//if local is false, then get only the positive Ids
	public static Set<Integer> extractObjectTermIds(List<CVTermRelationship> relationships, Boolean local) {
		Set<Integer> objectIds = new HashSet<Integer>();
		
		if (relationships != null && relationships.size() > 0) {
			for (CVTermRelationship relationship : relationships) {
				if (local == null
				|| local == true && relationship.getObjectId() <= 0
				|| local == false && relationship.getObjectId() > 0 ) {
					
					objectIds.add(relationship.getObjectId());
				}
			}
		}
		
		return objectIds;
	}
}
