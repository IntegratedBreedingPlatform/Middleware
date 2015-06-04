/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.ontology;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.util.Debug;

/**
 * Extends {@link Term} to store Property object for ontology
 */
public class Property extends Term {

	/**
	 *
	 */
	private static final long serialVersionUID = -8779519865829291289L;
	private final Set<String> classes = new HashSet<>();
	private String cropOntologyId;

	public Property() {
		this.setVocabularyId(CvId.PROPERTIES.getId());
	}

	public Property(org.generationcp.middleware.domain.oms.Term term) {
		super(term);
		this.setVocabularyId(CvId.PROPERTIES.getId());
	}

	public Set<String> getClasses() {
		return this.classes;
	}

	public void addClass(String className) {
		this.classes.add(className);
	}

	public String getCropOntologyId() {
		return this.cropOntologyId;
	}

	public void setCropOntologyId(String cropOntologyId) {
		this.cropOntologyId = cropOntologyId;
	}

	@Override
	public String toString() {
		return "Property{" + "classes=" + this.classes + ", cropOntologyId='" + this.cropOntologyId + '\'' + "} " + super.toString();
	}

	@Override
	public void print(int indent) {
		Debug.println(indent, "Property: ");
		super.print(indent + 3);
		if (this.cropOntologyId != null) {
			Debug.print(indent + 6, "cropOntologyId: " + this.getCropOntologyId());
		}

		if (this.classes != null) {
			Debug.println(indent + 3, "Classes: " + Arrays.toString(this.classes.toArray()));
		}
	}
}
