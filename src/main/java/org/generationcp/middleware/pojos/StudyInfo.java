/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Kevin L. Manansala
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.pojos;

import java.io.Serializable;

/**
 * The POJO which holds study information for germplasms
 * @author Kevin L. Manansala
 *
 */
public class StudyInfo implements Serializable{

    private static final long serialVersionUID = 3890829738084300785L;
    
    private Integer id;
    private String name;
    private String title;
    private String objective;
    private Integer rowCount;
    
    public StudyInfo(Integer id, String name, String title, String objective, Integer rowCount) {
        super();
        this.id = id;
        this.name = name;
        this.title = title;
        this.objective = objective;
        this.rowCount = rowCount;
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
    
    public Integer getRowCount() {
        return rowCount;
    }
    
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StudyInfo [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", title=");
        builder.append(title);
        builder.append(", objective=");
        builder.append(objective);
        builder.append(", rowCount=");
        builder.append(rowCount);
        builder.append("]");
        return builder.toString();
    }
    
}
