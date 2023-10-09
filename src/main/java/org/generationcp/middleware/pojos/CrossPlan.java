package org.generationcp.middleware.pojos;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "cross_plan")
public class CrossPlan implements Serializable {

    private static final String FOLDER_TYPE = "FOLDER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @Column(name = "description")
    private String description;

    @Column(name = "notes")
    private String notes;

    @Basic(optional = false)
    @Column(name = "created_date")
    private Date createdDate;

    @Basic(optional = false)
    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name="type")
    private String type;

    @Column(name = "program_uuid")
    private String programUUID;

    @ManyToOne(targetEntity = CrossPlan.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "cross_plan_parent_id")
    private CrossPlan parent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer crossPlanId) {
        this.id = crossPlanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String crossPlanName) {
        this.name = crossPlanName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProgramUUID() {
        return programUUID;
    }

    public void setProgramUUID(String programUUID) {
        this.programUUID = programUUID;
    }

    public CrossPlan getParent() {
        return parent;
    }

    public void setParent(CrossPlan children) {
        this.parent = children;
    }

    public boolean isFolder() {
        return this.getType() != null && this.getType().equalsIgnoreCase(CrossPlan.FOLDER_TYPE) ? true : false;
    }
}
