/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.util.Debug;

/**
 * Contains the primary details of a study - id, name, description.
 *
 * @author Joyce Avestro
 *
 */
public class StudyReference extends Reference {

	private Integer numberOfEnvironments;
	
	private StudyTypeDto studyType;

	public StudyReference(final Integer id, final String name) {
		super.setId(id);
		super.setName(name);
	}

	public StudyReference(final Integer id, final String name, final String description) {
		this(id, name);
		super.setDescription(description);
	}

	public StudyReference(final Integer id, final String name, final String description, final String programUUID) {
		this(id, name, description);
		super.setProgramUUID(programUUID);
	}
	
	public StudyReference(final Integer id, final String name, final String description, final String programUUID, final StudyTypeDto studyType) {
		this(id, name, description, programUUID);
		this.studyType = studyType;
	}

	public StudyReference(final Integer id, final String name, final String description, final Integer numberOfEnvironments) {
		this(id, name, description);
		this.setNumberOfEnvironments(numberOfEnvironments);
	}

	public Integer getNumberOfEnvironments() {
		return this.numberOfEnvironments;
	}

	public void setNumberOfEnvironments(final Integer numberOfEnvironments) {
		this.numberOfEnvironments = numberOfEnvironments;
	}
	
	public StudyTypeDto getStudyType() {
		return studyType;
	}

	public void setStudyType(final StudyTypeDto studyType) {
		this.studyType = studyType;
	}

	@Override
	public void print(final int indent) {
		Debug.println(indent, "STUDY:[id=" + this.getId() + ", name=" + this.getName() + ", description=" + this.getDescription()
				+ ", noOfEnv=" + this.numberOfEnvironments + "]");
	}
}
