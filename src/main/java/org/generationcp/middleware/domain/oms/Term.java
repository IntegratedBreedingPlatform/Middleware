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
package org.generationcp.middleware.domain.oms;

import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.Debug;

import java.io.Serializable;

/** 
 * Contains the details of a Term - id, vocabularyId, name, definition, nameSynonyms, obsolete.
 */
public class Term implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;
	
	private int vocabularyId;
	
	private String name;
	
	private String definition;

	private Boolean obsolete;
	
	public Term() { }
	
	public Term(int id, String name, String definition) {
		this.id = id;
		this.name = name;
		this.definition = definition;
	}

    public Term(int id, String name, String definition, int vocabularyId, Boolean obsolete) {
        this.id = id;
        this.name = name;
        this.definition = definition;
        this.vocabularyId = vocabularyId;
        this.obsolete = obsolete;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(int vocabularyId) {
		this.vocabularyId = vocabularyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

    public static Term fromCVTerm(CVTerm cvTerm){
        Term term = new Term();
        term.setId(cvTerm.getCvTermId());
        term.setName(cvTerm.getName());
        term.setDefinition(cvTerm.getDefinition());
        term.setVocabularyId(cvTerm.getCv());
        term.setObsolete(cvTerm.isObsolete());
        return term;
    }

    public CVTerm toCVTerm(){
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCv(this.getVocabularyId());
        cvTerm.setCvTermId(this.getId());
        cvTerm.setName(this.getName());
        cvTerm.setDefinition(this.getDefinition());
        cvTerm.setIsObsolete(this.isObsolete());
        return cvTerm;
    }
    
    public void print(int indent) {
		Debug.println(indent, "Id: " + getId());
		Debug.println(indent, "Vocabulary: " + getVocabularyId());
		Debug.println(indent, "Name: " + getName());
	    Debug.println(indent, "Definition: " + getDefinition());
	    Debug.println(indent, "Obsolete: " + obsolete);
	}
	
	@Override
	public int hashCode() {
		return getId();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
            return false;
        }
		if (!(obj instanceof Term)) {
            return false;
        }
		Term other = (Term) obj;
		return getId() == other.getId();
	}

	@Override
	public String toString() {
        return "Term [id=" + id + ", name=" + name + ", definition=" + definition + ", vocabularyId=" + vocabularyId + ", obsolete=" + obsolete + "]";
	}

	public void setObsolete(Boolean obsolete) {
		this.obsolete = obsolete;
	}
	
	public boolean isObsolete() {
		return this.obsolete == null ? false : this.obsolete;
	}
}
