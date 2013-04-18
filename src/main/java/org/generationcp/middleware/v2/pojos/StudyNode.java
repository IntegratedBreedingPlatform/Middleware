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

/**
 * This class is used to display Study nodes
 * 
 * @author Joyce Avestro
 *
 */
public class StudyNode extends AbstractNode {
	
	public StudyNode(Integer id, String name){
		super.setId(id);
		super.setName(name);
	}

	@Override
	protected String getEntityName() {
		return "StudyNode";
	}

}
