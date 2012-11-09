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
        @NamedQuery(name = "getStudyByNameUsingEqual", query = "SELECT s FROM Study s WHERE s.name = :name"),
        @NamedQuery(name = "getStudyBySDateUsingEqual", query = "SELECT s FROM Study s WHERE s.startDate = :startDate"),
        @NamedQuery(name = "getStudyByEDateUsingEqual", query = "SELECT s FROM Study s WHERE s.endDate = :endDate"),
        @NamedQuery(name = "getStudyByNameUsingLike", query = "SELECT s FROM Study s WHERE s.name LIKE :name"),
        @NamedQuery(name = "countStudyByNameUsingEqual", query = "SELECT COUNT(s) FROM Study s WHERE s.name = :name"),
        @NamedQuery(name = "countStudyBySDateUsingEqual", query = "SELECT COUNT(s) FROM Study s WHERE s.startDate = :startDate"),
        @NamedQuery(name = "countStudyByEDateUsingEqual", query = "SELECT COUNT(s) FROM Study s WHERE s.endDate = :endDate"),
        @NamedQuery(name = "countStudyByNameUsingLike", query = "SELECT COUNT(s) FROM Study s WHERE s.name LIKE :name")
})
@Entity
@Table(name = "study")
public class Study implements Serializable{

    private static final long serialVersionUID = -8809692556025457504L;

    public static final String GET_BY_NAME_USING_EQUAL = "getStudyByNameUsingEqual";
    public static final String GET_BY_SDATE_USING_EQUAL = "getStudyBySDateUsingEqual";
    public static final String GET_BY_EDATE_USING_EQUAL = "getStudyByEDateUsingEqual";
    public static final String GET_BY_NAME_USING_LIKE = "getStudyByNameUsingLike";
    public static final String COUNT_BY_NAME_USING_EQUAL = "countStudyByNameUsingEqual";
    public static final String COUNT_BY_NAME_USING_LIKE = "countStudyByNameUsingLike";
    public static final String COUNT_BY_SDATE_USING_EQUAL = "countStudyBySDateUsingEqual";
    public static final String COUNT_BY_EDATE_USING_EQUAL = "countStudyByEDateUsingEqual";

    public static final String GET_BY_COUNTRY_USING_EQUAL = 
            "SELECT DISTINCT {s.*} FROM Study s JOIN Locdes ld ON s.userid = ld.duid " 
            + "                       JOIN Location l ON ld.locid = l.locid " 
            + "                       JOIN Cntry c on l.cntryid = c.cntryid " 
            + "WHERE c.isofull = :country";
    
    public static final String GET_BY_COUNTRY_USING_LIKE = 
            "SELECT DISTINCT {s.*} FROM Study s JOIN Locdes ld ON s.userid = ld.duid " 
            + "                       JOIN Location l ON ld.locid = l.locid " 
            + "                       JOIN Cntry c on l.cntryid = c.cntryid " 
            + "WHERE c.isofull LIKE :country";
    
    public static final String COUNT_BY_COUNTRY_USING_EQUAL = 
            "SELECT COUNT(DISTINCT s.studyid) FROM Study s JOIN Locdes ld ON s.userid = ld.duid " 
            + "                       JOIN Location l ON ld.locid = l.locid " 
            + "                       JOIN Cntry c on l.cntryid = c.cntryid " 
            + "WHERE c.isofull = :country";
    
    public static final String COUNT_BY_COUNTRY_USING_LIKE = 
            "SELECT COUNT(DISTINCT s.studyid) FROM Study s JOIN Locdes ld ON s.userid = ld.duid " 
            + "                       JOIN Location l ON ld.locid = l.locid " 
            + "                       JOIN Cntry c on l.cntryid = c.cntryid " 
            + "WHERE c.isofull LIKE :country";
    
    private static final String BY_SEASON_FROM_CLAUSE =
            "FROM Study s INNER JOIN Factor f ON f.studyid = s.studyid "
            + "              INNER JOIN trait t ON t.traitid = f.traitid AND t.trname = 'CROP SEASON' " 
            + "              LEFT JOIN level_n ln ON ln.labelid = f.labelid " 
            + "              LEFT JOIN scaledis scd ON scd.value = ln.lvalue AND scd.scaleid = f.scaleid "
            ;
   
    public static final String GET_BY_SEASON = 
            "SELECT DISTINCT {s.*} " + BY_SEASON_FROM_CLAUSE; 
            
    public static final String COUNT_BY_SEASON = 
            "SELECT COUNT(DISTINCT s.studyid) " + BY_SEASON_FROM_CLAUSE; 
    
    public static final String DRY_SEASON_CONDITION = 
            " WHERE scd.valdesc = 'Dry' OR scd.valdesc = 'Dry' OR ln.lvalue = 0";

    public static final String WET_SEASON_CONDITION =    
            " WHERE scd.valdesc = 'Wet' OR scd.valdesc = 'Wet' OR ln.lvalue = 1";
    
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

    public Study(Integer id, String name, Integer projectKey, String title, String objective, Integer primaryInvestigator, String type,
            Integer startDate, Integer endDate, Integer user, Integer status, Integer hierarchy, Integer creationDate) {
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
        StringBuilder builder = new StringBuilder();
        builder.append("Study [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", projectKey=");
        builder.append(projectKey);
        builder.append(", title=");
        builder.append(title);
        builder.append(", objective=");
        builder.append(objective);
        builder.append(", primaryInvestigator=");
        builder.append(primaryInvestigator);
        builder.append(", type=");
        builder.append(type);
        builder.append(", startDate=");
        builder.append(startDate);
        builder.append(", endDate=");
        builder.append(endDate);
        builder.append(", user=");
        builder.append(user);
        builder.append(", status=");
        builder.append(status);
        builder.append(", hierarchy=");
        builder.append(hierarchy);
        builder.append(", creationDate=");
        builder.append(creationDate);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Study)) {
            return false;
        }

        Study rhs = (Study) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 27).append(id).toHashCode();
    }
}
