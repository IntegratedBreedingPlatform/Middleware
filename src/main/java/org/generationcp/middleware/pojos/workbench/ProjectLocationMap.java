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

package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * <b>Description</b>: POJO for workbench_project_loc_map table.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Aug 9, 2012
 */
@Entity
@Table(name = "workbench_project_loc_map")
public class ProjectLocationMap implements Serializable{

    private static final long serialVersionUID = 6831592558776267678L;
    
    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @OneToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @Basic(optional = false)
    @Column(name = "location_id")
    private Long locationId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProjectLocationMap [id=");
        builder.append(id);
        builder.append(", project=");
        builder.append(project);
        builder.append(", locationId=");
        builder.append(locationId);
        builder.append("]");
        return builder.toString();
    }
}
