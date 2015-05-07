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
package org.generationcp.middleware.domain.ontology;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.util.Debug;

/**
 * Extends {@link OntologyTerm} to store Method object for ontology
 */
public class OntologyMethod extends OntologyTerm {

    public OntologyMethod() {
        this.setVocabularyId(CvId.METHODS.getId());
    }

    public OntologyMethod(Term term){
        super(term);
        this.setVocabularyId(CvId.METHODS.getId());
    }

    @Override
    public String toString() {
        return "OntologyMethod{} " + super.toString();
    }

    @Override
    public void print(int indent) {
        Debug.println(indent, "Method: ");
        super.print(indent + 3);
    }

}
