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

package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.util.Debug;


/**
 * Contains the primary details of a study - id, name, description.
 * 
 * @author Joyce Avestro
 *
 */
public class StudyReference extends Reference {
	
	public StudyReference(Integer id, String name) {
		super.setId(id);
		super.setName(name);
	}

	public StudyReference(Integer id, String name, String description) {
		super.setId(id);
		super.setName(name);
		super.setDescription(description);
	}
	
	public void print(int indent) {
		Debug.println(indent, "STUDY:[id=" + getId() + ", name=" + getName() + ", description=" + getDescription() + "]");
	}
}
