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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "scaledis")
public class ScaleDiscrete implements Serializable{

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected ScaleDiscretePK id;

    @Basic(optional = false)
    @Column(name = "valdesc")
    private String valueDescription;

    public ScaleDiscrete() {
    }

    public ScaleDiscrete(ScaleDiscretePK id) {
        super();
        this.id = id;
    }

    public ScaleDiscrete(ScaleDiscretePK id, String valueDescription) {
        super();
        this.id = id;
        this.valueDescription = valueDescription;
    }

    public ScaleDiscretePK getId() {
        return id;
    }

    public void setId(ScaleDiscretePK id) {
        this.id = id;
    }

    public String getValueDescription() {
        return valueDescription;
    }

    public void setValueDescription(String valueDescription) {
        this.valueDescription = valueDescription;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ScaleDiscrete other = (ScaleDiscrete) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ScaleDiscrete [id=");
        builder.append(id);
        builder.append(", valueDescription=");
        builder.append(valueDescription);
        builder.append("]");
        return builder.toString();
    }

}
