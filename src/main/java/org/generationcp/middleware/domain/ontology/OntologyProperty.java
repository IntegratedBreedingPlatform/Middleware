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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/**
 * Extends {@link OntologyTerm} to store Property object for ontology
 */
public class OntologyProperty extends OntologyTerm {

    private final Set<String> classes = new HashSet<>();
    private String cropOntologyId;

    public OntologyProperty() {
        this.setVocabularyId(CvId.PROPERTIES.getId());
    }

    public OntologyProperty(Term term) {
        super(term);
        this.setVocabularyId(CvId.PROPERTIES.getId());
    }

    public Set<String> getClasses() {
        return classes;
    }

    public void addClass(String className)
    {
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
        return "OntologyProperty{" +
                "classes=" + classes +
                ", cropOntologyId='" + cropOntologyId + '\'' +
                "} " + super.toString();
    }

    @Override
    public void print(int indent) {
        Debug.println(indent, "Property: ");
        super.print(indent + 3);
        if(cropOntologyId != null)
        {
            Debug.print(indent + 6, "cropOntologyId: " + this.getCropOntologyId());
        }

        if(classes != null){
            Debug.println(indent + 3, "Classes: " + Arrays.toString(classes.toArray()));
        }
    }
}
