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
package org.generationcp.middleware.pojos.report;

import java.io.Serializable;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Person;

/**
 * This POJO represents a row in reports about Transaction records. Note that
 * different reports require different information and so some fields in this
 * class may be null. It is recommended to check if a field is available first
 * before trying to access it. The documentation for the methods generating the
 * reports will provide the list of information available from instances of this
 * object returned by the methods.
 * 
 * @author Kevin Manansala
 * 
 */
public class TransactionReportRow implements Serializable{

    private static final long serialVersionUID = 4363149565820886638L;

    private Integer date;
    private Double quantity;
    private Term scaleOfLot;
    private Location locationOfLot;
    private String commentOfLot;
    private Integer entityIdOfLot;
    private Person person;

    public TransactionReportRow() {

    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Term getScaleOfLot() {
        return scaleOfLot;
    }

    public void setScaleOfLot(Term scaleOfLot) {
        this.scaleOfLot = scaleOfLot;
    }

    public Location getLocationOfLot() {
        return locationOfLot;
    }

    public void setLocationOfLot(Location locationOfLot) {
        this.locationOfLot = locationOfLot;
    }

    public String getCommentOfLot() {
        return commentOfLot;
    }

    public void setCommentOfLot(String commentOfLot) {
        this.commentOfLot = commentOfLot;
    }

    public Integer getEntityIdOfLot() {
        return entityIdOfLot;
    }

    public void setEntityIdOfLot(Integer entityIdOfLot) {
        this.entityIdOfLot = entityIdOfLot;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TransactionReportRow [date=");
        builder.append(date);
        builder.append(", quantity=");
        builder.append(quantity);
        builder.append(", scaleOfLot=");
        builder.append(scaleOfLot);
        builder.append(", locationOfLot=");
        builder.append(locationOfLot);
        builder.append(", commentOfLot=");
        builder.append(commentOfLot);
        builder.append(", entityIdOfLot=");
        builder.append(entityIdOfLot);
        builder.append(", person=");
        builder.append(person);
        builder.append("]");
        return builder.toString();
    }

}
