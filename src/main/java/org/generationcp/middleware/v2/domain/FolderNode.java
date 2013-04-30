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

package org.generationcp.middleware.v2.domain;


/**
 * This class is used to display root or top-level folders of Studies
 * 
 * @author Darla Ani
 *
 */
public class FolderNode extends AbstractNode {
	
	public FolderNode(Integer id, String name){
		super.setId(id);
		super.setName(name);
	}

	public FolderNode(Integer id, String name, String description){
		super.setId(id);
		super.setName(name);
		super.setDescription(description);
	}

	@Override
	protected String getEntityName() {
		return "FolderNode";
	}

}
