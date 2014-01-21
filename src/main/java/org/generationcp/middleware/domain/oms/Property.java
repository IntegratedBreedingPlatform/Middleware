/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
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

import java.util.ArrayList;

import org.generationcp.middleware.util.Debug;

public class Property {
    
	Term term;
	
	Term IsA;
	
	
	public Property() {
	}

    public Property(Term term) {
        this.term = term;
    }

    public Property(Term term, Term isA) {
        this.term = term;
        this.IsA = isA;
    }
    
    public Property(Term term, Term isA, String cropOntologyId) {
        this(term, isA);
        TermProperty prop = new TermProperty(term.getId(), TermId.CROP_ONTOLOGY_ID.getId(), cropOntologyId, 0);
        term.setProperties(new ArrayList<TermProperty>());
        term.getProperties().add(prop);
    }

    public Term getIsA() {
		return IsA;
	}

	public void setIsA(Term isA) {
		IsA = isA;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}
	
    public int getId() {
        return term.getId();
    }

    public void setId(int id) {
        term.setId(id);
    }
	

    public String getName() {
        return term.getName();
    }

    public void setName(String name) {
        term.setName(name);
    }

    public String getDefinition() {
       return term.getDefinition();
    }
    
    public void setDefinition(String definition) {
        term.setDefinition(definition);
    }
    
    public int getIsAId() {
        if(IsA != null){
            return IsA.getId();
        }else{
            return -1;
        }
        
    }
    
    public String getCropOntologyId() {
        if (term.getProperties() != null && !term.getProperties().isEmpty()) {
            for (TermProperty prop : term.getProperties()) {
                if (prop.getTypeId().equals(TermId.CROP_ONTOLOGY_ID.getId()) && prop.getValue() != null && !"".equals(prop.getValue())) {
                    return prop.getValue();
                }
            }
        }
        return "";
    }
	
	
    @Override
    public String toString() {

        if (term == null){
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Property [id=");
        builder.append(term.getId());
        builder.append(", name=");
        builder.append(term.getName());
        builder.append(", definition=");
        builder.append(term.getDefinition());
        builder.append(", IsA=");
        builder.append(IsA);
        builder.append("]");
		return builder.toString();
	}

    public void print(int indent) {
        Debug.println(indent, "Property: ");
        Debug.println(indent + 3, "term: ");
        term.print(indent + 6);
        if (IsA != null){
            Debug.println(indent + 3, "IsA: ");
            IsA.print(indent + 6);
        }
    }
	
}
