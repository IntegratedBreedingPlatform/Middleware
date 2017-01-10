/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.report;

import java.io.Serializable;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Person;

/**
 * This POJO represents a row in reports about Transaction records. Note that different reports require different information and so some
 * fields in this class may be null. It is recommended to check if a field is available first before trying to access it. The documentation
 * for the methods generating the reports will provide the list of information available from instances of this object returned by the
 * methods.
 *
 * @author Kevin Manansala
 *
 */
public class TransactionReportRow implements Serializable {

	private static final long serialVersionUID = 4363149565820886638L;

	private Integer date;
	private Double quantity;
	private Term scaleOfLot;
	private Location locationOfLot;
	private String commentOfLot;
	private Integer entityIdOfLot;
	private Person person;
	private Integer lotId;
	private String listName;
	private String user;
	private Integer userId;
	private Integer listId;
	private Integer trnStatus;
	private String lotStatus;

	public TransactionReportRow() {

	}

	public Integer getDate() {
		return this.date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}

	public Double getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Term getScaleOfLot() {
		return this.scaleOfLot;
	}

	public void setScaleOfLot(Term scaleOfLot) {
		this.scaleOfLot = scaleOfLot;
	}

	public Location getLocationOfLot() {
		return this.locationOfLot;
	}

	public void setLocationOfLot(Location locationOfLot) {
		this.locationOfLot = locationOfLot;
	}

	public String getCommentOfLot() {
		return this.commentOfLot;
	}

	public void setCommentOfLot(String commentOfLot) {
		this.commentOfLot = commentOfLot;
	}

	public Integer getEntityIdOfLot() {
		return this.entityIdOfLot;
	}

	public void setEntityIdOfLot(Integer entityIdOfLot) {
		this.entityIdOfLot = entityIdOfLot;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Integer getLotId() {
		return lotId;
	}

	public void setLotId(Integer lotId) {
		this.lotId = lotId;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public void setTrnStatus(Integer trnStatus) {
		this.trnStatus = trnStatus;
	}

	public String getLotStatus() {
		return lotStatus;
	}

	public void setLotStatus(String lotStatus) {
		this.lotStatus = lotStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionReportRow [date=");
		builder.append(this.date);
		builder.append(", quantity=");
		builder.append(this.quantity);
		builder.append(", scaleOfLot=");
		builder.append(this.scaleOfLot);
		builder.append(", locationOfLot=");
		builder.append(this.locationOfLot);
		builder.append(", commentOfLot=");
		builder.append(this.commentOfLot);
		builder.append(", entityIdOfLot=");
		builder.append(this.entityIdOfLot);
		builder.append(", person=");
		builder.append(this.person);
		builder.append("]");
		return builder.toString();
	}

}
