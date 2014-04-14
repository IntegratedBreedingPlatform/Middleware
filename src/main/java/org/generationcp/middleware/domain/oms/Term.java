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

import java.io.Serializable;
import java.util.List;

import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.util.Debug;

/** 
 * Contains the details of a Term - id, vocabularyId, name, definition, nameSynonyms, obsolete.
 */
public class Term implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;
	
	private int vocabularyId;
	
	private String name;
	
	private String definition;
	
	private List<NameSynonym> nameSynonyms;

	private Boolean obsolete;
	
	private List<TermProperty> properties;
	
	public Term() { }
	
	public Term(int id, String name, String definition) {
		this.id = id;
		this.name = name;
		this.definition = definition;
	}

	public Term(int id, String name, String definition, List<NameSynonym> nameSynonyms) {
		this.id = id;
		this.name = name;
		this.definition = definition;
		this.nameSynonyms = nameSynonyms;
	}

    public Term(int id, String name, String definition, List<NameSynonym> nameSynonyms, List<TermProperty> properties) {
        this.id = id;
        this.name = name;
        this.definition = definition;
        this.nameSynonyms = nameSynonyms;
        this.properties = properties;
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
	
	public List<NameSynonym> getNameSynonyms() {
		return nameSynonyms;
	}

	public void setNameSynonyms(List<NameSynonym> nameSynonyms) {
		this.nameSynonyms = nameSynonyms;
	}
	
	public List<TermProperty> getProperties() {
        return properties;
    }
    
    public void setProperties(List<TermProperty> properties) {
        this.properties = properties;
    }

    public void print(int indent) {
		Debug.println(indent, "Id: " + getId());
		Debug.println(indent, "Vocabulary: " + getVocabularyId());
		Debug.println(indent, "Name: " + getName());
	    Debug.println(indent, "Definition: " + getDefinition());
        if (nameSynonyms != null) {
            Debug.println(indent, "NameSynonyms: " + nameSynonyms);
        }
        if (properties != null) {
            Debug.println(indent, "TermProperties: " + properties);
        }
	    Debug.println(indent, "Obsolete: " + obsolete);
	}
	
	@Override
	public int hashCode() {
		return getId();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Term)) return false;
		Term other = (Term) obj;
		return getId() == other.getId();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Term [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", definition=");
		builder.append(definition);
		if (nameSynonyms != null) {
			builder.append(", nameSynonyms=");
			builder.append(nameSynonyms);
		}
		builder.append("]");
		return builder.toString();
	}

	public void setObsolete(Boolean obsolete) {
		this.obsolete = obsolete;
	}
	
	public boolean isObsolete() {
		return this.obsolete == null ? false : this.obsolete;
	}
}
