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

import org.generationcp.middleware.util.Debug;

import java.util.*;

public class Property {
    
	private Term term;
    private final Set<String> classes = new HashSet<>();
    private String cropOntologyId;

    @Deprecated
    /**
     * @Deprecated Properties will have multiple classes associated to it. isA should not be used in Ontology Manager Redesign
     */
	private Term isA;

    public Set<String> getClasses() {
        return classes;
    }

    public void addClass(String className)
    {
        this.classes.add(className);
    }

    public Property() {
        this.term = new Term();
        this.term.setVocabularyId(CvId.PROPERTIES.getId());
	}

    public Property(Term term) {
        this.term = term;
    }

    public Property(Term term, Term isA) {
        this.term = term;
        this.isA = isA;
    }
    
    public Property(Term term, Term isA, String cropOntologyId) {
        this(term, isA);
        this.cropOntologyId = cropOntologyId;
    }

    @Deprecated
    public Term getIsA() {
		return isA;
	}

    @Deprecated
    public void setIsA(Term isA) {
		this.isA = isA;
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

    public void setCropOntologyId(String cropOntologyId) {
		this.cropOntologyId = cropOntologyId;
	}

	public String getCropOntologyId() {
        return this.cropOntologyId;
    }
	
	
    @Override
    public String toString() {

        if (term == null){
            return "";
        }

        return "Property [id=" + term.getId() + ", name=" + term.getName() + ", definition=" + term.getDefinition() + ", IsA=" + (isA == null ? "NULL" : isA) + ", Classes=" + (classes == null ? "NULL" : Arrays.toString(classes.toArray())) + "]";
	}

    public void print(int indent)
    {
        Debug.println(indent, "Property: ");
        Debug.println(indent + 3, "term: ");
        term.print(indent + 6);

        if(cropOntologyId != null)
        {
            Debug.print(indent + 6, "cropOntologyId: " + this.getCropOntologyId());
        }

        if (isA != null){
            Debug.println(indent + 3, "IsA: ");
            isA.print(indent + 6);
        }
        
        if(classes != null){
            Debug.println(indent + 3, "Classes: " + Arrays.toString(classes.toArray()));
        }
    }
}
