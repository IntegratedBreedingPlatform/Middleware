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

import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.util.Debug;


/**
 * Contains the primary details of a trait class - id, name, description, plus the list of properties 
 * 
 * @author Joyce Avestro
 *
 */
public class TraitClass extends Reference implements Comparable<TraitClass>{
    
    private Term term;
    
    private Term isA; // Either TermId.ONTOLOGY_TRAIT_CLASS or TermId.ONTOLOGY_RESEARCH_CLASS
    
    public TraitClass(Term term, Term isA) {
        this.term = term;
        this.isA = isA;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }
    
    public Term getIsA() {
        return isA;
    }
    
    public void setIsA(Term isA) {
        this.isA = isA;
    }

    @Override
    public String toString() {

        if (term == null){
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("TraitClass [id=");
        builder.append(term.getId());
        builder.append(", name=");
        builder.append(term.getName());
        builder.append(", definition=");
        builder.append(term.getDefinition());
        builder.append(", isA=");
        builder.append(isA);
        builder.append("]");
        return builder.toString();
    }

    public void print(int indent) {
        Debug.println(indent, "TraitClass: ");
        Debug.println(indent + 3, "term: ");
        term.print(indent + 6);
        if (isA != null){
            Debug.println(indent + 3, "isA: ");
            isA.print(indent + 6);
        }
    }

    /* (non-Javadoc)
     * Sort in ascending order by trait group name
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(TraitClass compareValue) {
        String compareName = ((TraitClass) compareValue).getName(); 
        return getName().compareToIgnoreCase(compareName);
    }

}
