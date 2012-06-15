/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@NamedQueries({
	@NamedQuery(name = "findStudyByNameUsingEqual", query = "SELECT s FROM Study s WHERE s.name = :name"),
	@NamedQuery(name = "findStudyByNameUsingLike", query = "SELECT s FROM Study s WHERE s.name like :name"),
	@NamedQuery(name = "countStudyByNameUsingEqual", query = "SELECT COUNT(s) FROM Study s WHERE s.name = :name"),
	@NamedQuery(name = "countStudyByNameUsingLike", query = "SELECT COUNT(s) FROM Study s WHERE s.name like :name")

})
@Entity
@Table(name = "study")
public class Study implements Serializable {
    private static final long serialVersionUID = -8809692556025457504L;

    public static final String FIND_BY_NAME_USING_EQUAL = "findStudyByNameUsingEqual";
    public static final String FIND_BY_NAME_USING_LIKE = "findStudyByNameUsingLike";
    public static final String COUNT_BY_NAME_USING_EQUAL = "countStudyByNameUsingEqual";
    public static final String COUNT_BY_NAME_USING_LIKE = "countStudyByNameUsingLike";

    @Id
    @Basic(optional = false)
    @Column(name = "studyid")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "sname")
    private String name;

    @Basic(optional = false)
    @Column(name = "pmkey")
    private Integer projectKey;

    @Basic(optional = false)
    @Column(name = "title")
    private String title;

    @Column(name = "objectiv")
    private String objective;

    @Basic(optional = false)
    @Column(name = "investid")
    private Integer primaryInvestigator;

    @Column(name = "stype")
    private String type;

    @Column(name = "sdate")
    private Integer startDate;

    @Column(name = "edate")
    private Integer endDate;

    @Column(name = "userid")
    private Integer user;

    @Basic(optional = false)
    @Column(name = "sstatus")
    private Integer status;

    @Basic(optional = false)
    @Column(name = "shierarchy")
    private Integer hierarchy;

    @Column(name = "studydate")
    private Integer creationDate;

    public Study() {
    }

    public Study(Integer id, String name, Integer projectKey, String title,
	    String objective, Integer primaryInvestigator, String type,
	    Integer startDate, Integer endDate, Integer user, Integer status,
	    Integer hierarchy, Integer creationDate) {
	super();
	this.id = id;
	this.name = name;
	this.projectKey = projectKey;
	this.title = title;
	this.objective = objective;
	this.primaryInvestigator = primaryInvestigator;
	this.type = type;
	this.startDate = startDate;
	this.endDate = endDate;
	this.user = user;
	this.status = status;
	this.hierarchy = hierarchy;
	this.creationDate = creationDate;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Integer getProjectKey() {
	return projectKey;
    }

    public void setProjectKey(Integer projectKey) {
	this.projectKey = projectKey;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getObjective() {
	return objective;
    }

    public void setObjective(String objective) {
	this.objective = objective;
    }

    public Integer getPrimaryInvestigator() {
	return primaryInvestigator;
    }

    public void setPrimaryInvestigator(Integer primaryInvestigator) {
	this.primaryInvestigator = primaryInvestigator;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public Integer getStartDate() {
	return startDate;
    }

    public void setStartDate(Integer startDate) {
	this.startDate = startDate;
    }

    public Integer getEndDate() {
	return endDate;
    }

    public void setEndDate(Integer endDate) {
	this.endDate = endDate;
    }

    public Integer getUser() {
	return user;
    }

    public void setUser(Integer user) {
	this.user = user;
    }

    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    public Integer getHierarchy() {
	return hierarchy;
    }

    public void setHierarchy(Integer hierarchy) {
	this.hierarchy = hierarchy;
    }

    public Integer getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(Integer creationDate) {
	this.creationDate = creationDate;
    }

    @Override
    public String toString() {
	return "Study [id=" + id + ", name=" + name + ", projectKey="
		+ projectKey + ", title=" + title + ", objective=" + objective
		+ ", primaryInvestigator=" + primaryInvestigator + ", type="
		+ type + ", startDate=" + startDate + ", endDate=" + endDate
		+ ", user=" + user + ", status=" + status + ", hierarchy="
		+ hierarchy + ", creationDate=" + creationDate + "]";
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if (obj == this)
	    return true;
	if (!(obj instanceof Study))
	    return false;

	Study rhs = (Study) obj;
	return new EqualsBuilder().appendSuper(super.equals(obj))
		.append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(19, 27).append(id).toHashCode();
    }
}
