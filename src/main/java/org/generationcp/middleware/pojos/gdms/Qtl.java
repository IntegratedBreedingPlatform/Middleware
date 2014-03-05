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
 * POJO for gdms_qtl table.
 * 
 * @author Joyce Avestro
 * 
 */
@Entity
@Table(name = "gdms_qtl")
public class Qtl implements Serializable{
   
    public static final String GET_MAP_IDS_BY_QTL_NAME = 
            "SELECT map_id "
            + "FROM gdms_qtl gq "
            + "INNER JOIN gdms_qtl_details gqd on gq.qtl_id = gqd.qtl_id "
            + "WHERE gq.qtl_name=:qtl_name " 
            + "ORDER BY gq.qtl_id";
        
    public static final String COUNT_MAP_IDS_BY_QTL_NAME = 
            "SELECT COUNT(map_id) "
            + "FROM gdms_qtl gq "
            + "INNER JOIN gdms_qtl_details gqd on gq.qtl_id = gqd.qtl_id "
            + "WHERE gq.qtl_name=:qtl_name";
    
    private static final String GET_QTL_DETAILS_SELECT = 
        "SELECT gq.qtl_id " 
                + ",CONCAT(gq.qtl_name,'') " 
                + ",gm.map_id " 
                + ",CONCAT(gm.map_name,'') "
                + ",gqd.linkage_group "  // chromosome
                + ",gqd.min_position "
                + ",gqd.max_position "
                + ",gqd.tid " 
                + ",CONCAT(gqd.experiment,'') " 
                + ",gqd.left_flanking_marker "
                + ",gqd.right_flanking_marker "
                + ",gqd.effect "
                + ",gqd.score_value " 
                + ",gqd.r_square "
                + ",gqd.interactions " 
                + ",gqd.position " 
                + ",gqd.clen " 
                + ",gqd.se_additive " 
                + ",gqd.hv_parent " 
                + ",gqd.hv_allele " 
                + ",gqd. lv_parent " 
                + ",gqd.lv_allele  " 
                + ",cvt.name " // trname
                + ",cvtprop.value " // ontology
                ;

    private static final String GET_QTL_DETAILS_FROM =                 
            "FROM gdms_qtl_details gqd "
                + "INNER JOIN gdms_qtl gq ON gq.qtl_id = gqd.qtl_id "
                + "INNER JOIN gdms_map gm ON gm.map_id = gqd.map_id "
                + "INNER JOIN cvterm cvt ON gqd.tid = cvt.cvterm_id "
                + "LEFT JOIN cvtermprop cvtprop ON cvt.cvterm_id = cvtprop.cvterm_id "
        ;
    
    public static final String GET_QTL_AND_QTL_DETAILS_BY_QTL_IDS = 
            GET_QTL_DETAILS_SELECT 
            + GET_QTL_DETAILS_FROM
            + "WHERE gq.qtl_id in(:qtl_id_list) "
            + "ORDER BY gq.qtl_id";

    public static final String COUNT_QTL_AND_QTL_DETAILS_BY_QTL_IDS = 
            "SELECT COUNT(*) " 
            + GET_QTL_DETAILS_FROM
            + "WHERE gq.qtl_id in(:qtl_id_list)"; 

    public static final String GET_QTL_AND_QTL_DETAILS_BY_NAME = 
            GET_QTL_DETAILS_SELECT 
            + GET_QTL_DETAILS_FROM
            + "WHERE   gq.qtl_name LIKE LOWER(:qtlName) "
            + "ORDER BY gq.qtl_id "
            ;

    public static final String COUNT_QTL_AND_QTL_DETAILS_BY_NAME = 
            "SELECT  COUNT(*) " 
            + GET_QTL_DETAILS_FROM
            + "WHERE   gq.qtl_name LIKE LOWER(:qtlName) "
            ;
    
    public static final String GET_QTL_ID_BY_NAME = 
            GET_QTL_DETAILS_SELECT 
            + GET_QTL_DETAILS_FROM
            + "FROM gdms_qtl "
            + "WHERE qtl_name LIKE LOWER(:qtlName) "
            + "ORDER BY qtl_id";
    
    public static final String COUNT_QTL_ID_BY_NAME = 
            "SELECT COUNT(*) "
            + "FROM gdms_qtl "
            + "WHERE qtl_name LIKE LOWER(:qtlName) ";
    		
    public static final String GET_QTL_BY_TRAIT = 
            "SELECT qtl_id " 
            + "FROM gdms_qtl_details " 
            + "WHERE tid = :qtlTrait " 
            + "ORDER BY qtl_id "
            ;
        
    public static final String COUNT_QTL_BY_TRAIT = 
            "SELECT COUNT(qtl_id) " 
            + "FROM gdms_qtl_details " 
            + "WHERE tid = :qtlTrait " 
            ;
    
    public static final String GET_QTL_IDS_BY_DATASET_IDS =
            "SELECT qtl_id "
            + "FROM gdms_qtl "
            + "WHERE dataset_id in (:datasetIds) ";
    
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "qtl_id")
    private Integer qtlId;

    @Basic(optional = false)
    @Column(name = "qtl_name")
    private String qtlName;

    @Column(name = "dataset_id")
    private Integer datasetId;

    public Qtl() {
    }

    public Qtl(Integer qtlId,
            String qtlName,
            Integer datasetId) {
        
        this.qtlId = qtlId;
        this.qtlName = qtlName;
        this.datasetId = datasetId;
    }


    public Integer getQtlId() {
        return qtlId;
    }

    public void setQtlId(Integer qtlId) {
        this.qtlId = qtlId;
    }

    public String getQtlName() {
        return qtlName;
    }

    public void setQtlName(String qtlName) {
        this.qtlName = qtlName;
    }

    public Integer getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Qtl)) {
            return false;
        }

        Qtl rhs = (Qtl) obj;
        return new EqualsBuilder().append(qtlId, rhs.qtlId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(37, 53).append(qtlId).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Qtl [qtlId=");
        builder.append(qtlId);
        builder.append(", qtlName=");
        builder.append(qtlName);
        builder.append(", datasetId=");
        builder.append(datasetId);
        builder.append("]");
        return builder.toString();
    }
}
