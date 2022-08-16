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

import java.util.Date;

import org.generationcp.middleware.util.Debug;

/**
 * Extends {@link org.generationcp.middleware.domain.oms.Term} to store additional DateCreated and DateLastModified data.
 */
public abstract class Term extends org.generationcp.middleware.domain.oms.Term {

	/**
	 *
	 */
	private static final long serialVersionUID = 796207481995779773L;

	protected Term() {

	}

	protected Term(org.generationcp.middleware.domain.oms.Term term) {
		this.setId(term.getId());
		this.setName(term.getName());
		this.setDefinition(term.getDefinition());
		this.setVocabularyId(term.getVocabularyId());
		this.setObsolete(term.isObsolete());
		this.setSystem(term.isSystem());
	}

	private Date dateCreated;
	private Date dateLastModified;

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateLastModified() {
		return this.dateLastModified;
	}

	public void setDateLastModified(Date dateLastModified) {
		this.dateLastModified = dateLastModified;
	}

	@Override
	public String toString() {
		return "Term{" + "dateCreated=" + this.dateCreated + ", dateLastModified=" + this.dateLastModified + "} " + super.toString();
	}

	@Override
	public void print(int indent) {
		super.print(indent + 3);
		if (this.dateCreated != null) {
			Debug.println(indent + 3, "Date Created:" + this.dateCreated);
		}

		if (this.dateLastModified != null) {
			Debug.println(indent + 3, "Date Last Modified:" + this.dateLastModified);
		}
	}
}
