/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
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


/**
 * POJO corresponding to QTL Genotyping data row used in setQTL. 
 *
 */
public class QtlDataRow{
    
    private Qtl qtl;
    
    private QtlDetails qtlDetails;

    
    public QtlDataRow() {
    }

    public QtlDataRow(Qtl qtl, QtlDetails qtlDetails) {
        this.qtl = qtl;
        this.qtlDetails = qtlDetails;
    }

    public Qtl getQtl() {
        return qtl;
    }
    
    public void setQtl(Qtl qtl) {
        this.qtl = qtl;
    }
    
    public QtlDetails getQtlDetails() {
        return qtlDetails;
    }
    
    public void setQtlDetails(QtlDetails qtlDetails) {
        this.qtlDetails = qtlDetails;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((qtl == null) ? 0 : qtl.hashCode());
        result = prime * result + ((qtlDetails == null) ? 0 : qtlDetails.hashCode());
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
        QtlDataRow other = (QtlDataRow) obj;
        if (qtl == null) {
            if (other.qtl != null)
                return false;
        } else if (!qtl.equals(other.qtl))
            return false;
        if (qtlDetails == null) {
            if (other.qtlDetails != null)
                return false;
        } else if (!qtlDetails.equals(other.qtlDetails))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QtlDataRow [qtl=");
        builder.append(qtl);
        builder.append(", qtlDetails=");
        builder.append(qtlDetails);
        builder.append("]");
        return builder.toString();
    }
    

}
