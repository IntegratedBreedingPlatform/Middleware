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
package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * Embeddable primary key of QtlDetails
 * 
 * @author Joyce Avestro
 * 
 */
@Embeddable
public class QtlDetailsPK implements Serializable{

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "qtl_id")
    private Integer qtlId;

    @Basic(optional = false)
    @Column(name = "map_id")
    private Integer mapId;

    public QtlDetailsPK() {
    }


    public QtlDetailsPK(Integer qtlId, Integer mapId) {
        super();
        this.qtlId = qtlId;
        this.mapId = mapId;
    }

    
        public Integer getQtlId() {
        return qtlId;
    }

    
    public void setQtlId(Integer qtlId) {
        this.qtlId = qtlId;
    }

    
    public Integer getMapId() {
        return mapId;
    }

    
    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mapId == null) ? 0 : mapId.hashCode());
        result = prime * result + ((qtlId == null) ? 0 : qtlId.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QtlDetailsPK other = (QtlDetailsPK) obj;
        if (mapId == null) {
            if (other.mapId != null)
                return false;
        } else if (!mapId.equals(other.mapId))
            return false;
        if (qtlId == null) {
            if (other.qtlId != null)
                return false;
        } else if (!qtlId.equals(other.qtlId))
            return false;
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QtlDetailsPK [qtlId=");
        builder.append(qtlId);
        builder.append(", mapId=");
        builder.append(mapId);
        builder.append("]");
        return builder.toString();
    }
    
    

}
