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
package org.generationcp.middleware.v2.factory;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.pojos.DmsProject;

/**
 * Creates Project entries given a Study
 * 
 * @author Joyce Avestro
 *
 */
public class ProjectFactory {
	
	private static final ProjectFactory instance = new ProjectFactory();
	
	public static ProjectFactory getInstance() {
		return instance;
	}
	
	public DmsProject createProject(Study study)  throws MiddlewareException{ 
		DmsProject project = null;

		if (study != null) {
			project = new DmsProject();
			mapStudytoProject(study.getId(), study.getName(), 
					study.getTitle(), project);
		}
		
		return project;
	}

	private void mapStudytoProject(Integer id, String name, String description, DmsProject project) throws MiddlewareException{
		String errorMessage = "";

		if (id != null){
			project.setProjectId(id);
		} else {
			errorMessage += "\nid is null";
		}
		
		if (name != null && !name.equals("")){
			project.setName(name);
		} else {
			errorMessage += "\nname is null";
		}
			
		if (description != null && !description.equals("")){
			project.setDescription(description);
		} else {
			errorMessage += "\nprojectKey is null";
		}
		
		if (!errorMessage.equals("")){
			throw new MiddlewareException(errorMessage);
		}


	}

}
