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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for gdms_mapping_pop table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_mapping_pop")
public class MappingPop implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    private ParentElement parent;

    @Column(name = "population_size")
    private Integer populationSize;

    @Column(name = "population_type")
    private String populationType;

    @Column(name = "mapdata_desc")
    private String mapDataDescription;
    
    @Column(name = "scoring_scheme")
    private String scoringScheme;
    
    @Column(name = "map_id")
    private Integer mapId;

    public MappingPop() {
    }

    public MappingPop(Integer datasetId,
                    String mappingType,
                    Integer parentANId,
                    Integer parentBNId,
                    Integer populationSize,
                    String populationType,
                    String mapDataDescription,
                    String scoringScheme,
                    Integer mapId) {
        this.datasetId = datasetId;
        this.parent = new ParentElement(parentANId, parentBNId, mappingType);
        this.populationSize = populationSize;
        this.populationType = populationType;
        this.mapDataDescription = mapDataDescription;
        this.scoringScheme = scoringScheme;
        this.mapId = mapId;    
    }

    public Integer getDatasetId() {
        return datasetId;
    }
    
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    public ParentElement getParent() {
        return parent;
    }
    
    public void setParent(ParentElement parent) {
        this.parent = parent;
    }

    public String getMappingType() {
        return parent.getMappingType();
    }
    
    public void setMappingType(String mappingType) {
        parent.setMappingType(mappingType);
    }
    
    public Integer getParentANId() {
        return parent.getParentANId();
    }
    
    public void setParentANId(Integer parentANId) {
        parent.setParentANId(parentANId);
    }
    
    public Integer getParentBNId() {
        return parent.getParentBGId();
    }
    
    public void setParentBGId(Integer parentBGId) {
        parent.setParentBGId(parentBGId);
    }
    
    public Integer getPopulationSize() {
        return populationSize;
    }
    
    public void setPopulationSize(Integer populationSize) {
        this.populationSize = populationSize;
    }
    
    public String getPopulationType() {
        return populationType;
    }
    
    public void setPopulationType(String populationType) {
        this.populationType = populationType;
    }
    
    public String getMapDataDescription() {
        return mapDataDescription;
    }
    
    public void setMapDataDescription(String mapDataDescription) {
        this.mapDataDescription = mapDataDescription;
    }
    
    public String getScoringScheme() {
        return scoringScheme;
    }
    
    public void setScoringScheme(String scoringScheme) {
        this.scoringScheme = scoringScheme;
    }
    
    public Integer getMapId() {
        return mapId;
    }
    
    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MappingPop)) {
            return false;
        }

        MappingPop rhs = (MappingPop) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                                    .append(datasetId, rhs.datasetId)
                                    .append(parent, rhs.parent)
                                    .append(populationSize, rhs.populationSize)
                                    .append(populationType, rhs.populationType)
                                    .append(mapDataDescription, rhs.mapDataDescription)
                                    .append(scoringScheme, rhs.scoringScheme)
                                    .append(mapId, rhs.mapId).isEquals();    
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(97, 311).append(datasetId)
                                        .append(parent)
                                        .append(populationSize)
                                        .append(populationType)
                                        .append(mapDataDescription)
                                        .append(scoringScheme)
                                        .append(mapId).toHashCode();   
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MappingPop [datasetId=");
        builder.append(datasetId);
        builder.append(", parent=");
        builder.append(parent);
        builder.append(", populationSize=");
        builder.append(populationSize);
        builder.append(", populationType=");
        builder.append(populationType);
        builder.append(", mapDataDescription=");
        builder.append(mapDataDescription);
        builder.append(", scoringScheme=");
        builder.append(scoringScheme);
        builder.append(", mapId=");
        builder.append(mapId);
        builder.append("]");
        return builder.toString();
    }

}
