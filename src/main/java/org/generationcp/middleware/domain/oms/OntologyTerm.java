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
import java.util.Date;

/**
 * Extends {@link Term} to store additional DateCreated and DateLastModified data.
 */
public abstract class OntologyTerm extends Term {

    protected OntologyTerm() {

    }

    protected OntologyTerm(Term term) {
        this.setId(term.getId());
        this.setName(term.getName());
        this.setDefinition(term.getDefinition());
        this.setVocabularyId(term.getVocabularyId());
        this.setObsolete(term.isObsolete());
    }

    private Date dateCreated;
    private Date dateLastModified;

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(Date dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    @Override
    public String toString() {
        return "OntologyTerm{" +
                "dateCreated=" + dateCreated +
                ", dateLastModified=" + dateLastModified +
                "} " + super.toString();
    }

    @Override
    public void print(int indent) {
        super.print(indent + 3);
        if(dateCreated != null){
            Debug.println(indent + 3, "Date Created:" + dateCreated);
        }

        if(dateLastModified != null){
            Debug.println(indent + 3, "Date Last Modified:" + dateLastModified);
        }
    }
}
