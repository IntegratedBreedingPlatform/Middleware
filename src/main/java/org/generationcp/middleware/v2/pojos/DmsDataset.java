/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.v2.pojos;

import java.io.Serializable;
import java.util.List;

public class DmsDataset extends DmsProject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public DmsDataset() {
	}

	public DmsDataset(Integer projectId, String name, String description,
			List<ProjectProperty> properties,
			List<ProjectRelationship> relatedTos,
			List<ProjectRelationship> relatedBys) {
		super(projectId, name, description, properties, relatedTos, relatedBys);
	}

	@Override
	public String getEntityName(){
		return "DmsDataset";
	}


}
