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

import javax.persistence.*;
import java.io.Serializable;

/**
 * POJO for progntrs table.
 * 
 * @author klmanansala
 */
@Entity
@Table(name = "progntrs")
public class Progenitor implements Serializable{

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected ProgenitorPK progntrsPK;

    @Basic(optional = false)
    @Column(name = "pid")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer pid;

    public Progenitor() {
    }

    public Progenitor(ProgenitorPK progntrsPK) {
        this.progntrsPK = progntrsPK;
    }

    public Progenitor(ProgenitorPK progntrsPK, Integer pid) {
        this.progntrsPK = progntrsPK;
        this.pid = pid;
    }

    public Progenitor(Germplasm germplasm, Integer pno) {
        this.progntrsPK = new ProgenitorPK(germplasm.getGid(), pno);
    }

    public ProgenitorPK getProgntrsPK() {
        return progntrsPK;
    }

    public void setProgntrsPK(ProgenitorPK progntrsPK) {
        this.progntrsPK = progntrsPK;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    @Override
    public int hashCode() {
        return this.getProgntrsPK().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Progenitor) {
            Progenitor param = (Progenitor) obj;
            if (this.getProgntrsPK().equals(param.getProgntrsPK())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Progenitor [progntrsPK=");
        builder.append(progntrsPK);
        builder.append(", pid=");
        builder.append(pid);
        builder.append("]");
        return builder.toString();
    }

}
