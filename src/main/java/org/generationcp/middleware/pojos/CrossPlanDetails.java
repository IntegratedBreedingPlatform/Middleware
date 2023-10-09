package org.generationcp.middleware.pojos;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cross_plan_details")
public class CrossPlanDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cross_plan_id", insertable = false, updatable = false)
    private CrossPlan crossPlan;

    @Basic(optional = false)
    @Column(name = "method")
    private String method;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Basic(optional = false)
    @Column(name = "make_reciprocal_crosses", columnDefinition = "TINYINT")
    private Boolean makeReciprocalCrosses;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Basic(optional = false)
    @Column(name = "exclude_selfs", columnDefinition = "TINYINT")
    private Boolean excludeSelfs;

    @Basic(optional = false)
    @Column(name = "female_parents")
    private String femaleParents;

    @Basic(optional = false)
    @Column(name = "male_parents")
    private String maleParents;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CrossPlan getCrossPlan() {
        return crossPlan;
    }

    public void setCrossPlan(CrossPlan crossPlan) {
        this.crossPlan = crossPlan;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getMakeReciprocalCrosses() {
        return makeReciprocalCrosses;
    }

    public void setMakeReciprocalCrosses(Boolean makeReciprocalCrosses) {
        this.makeReciprocalCrosses = makeReciprocalCrosses;
    }

    public Boolean getExcludeSelfs() {
        return excludeSelfs;
    }

    public void setExcludeSelfs(Boolean excludeSelfs) {
        this.excludeSelfs = excludeSelfs;
    }

    public String getFemaleParents() {
        return femaleParents;
    }

    public void setFemaleParents(String femaleParents) {
        this.femaleParents = femaleParents;
    }

    public String getMaleParents() {
        return maleParents;
    }

    public void setMaleParents(String maleParents) {
        this.maleParents = maleParents;
    }
}
