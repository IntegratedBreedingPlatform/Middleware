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

public class Method {
    
    private Term term;
    
    public Method() {
        this.term = new Term();
    }

    public Method(Term term) {
        this.term = term;
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
    

	@Override
	public String toString() {
	    
        if (term == null) {
            return "";
        }

        return "Method [id=" + term.getId() + ", name=" + term.getName() + ", definition=" + term.getDefinition() + "]";
	}

    public void print(int indent) {
        Debug.println(indent, "Method: ");
        term.print(indent + 3);
    }
	
}
