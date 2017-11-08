/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for gdms_dataset_users table.
 *
 * @author Dennis Billano
 */
@Entity
@Table(name = "gdms_dataset_users")
public class DatasetUsers implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Integer datasetId;

	@JoinColumn(name = "dataset_id")
	@OneToOne
	@MapsId
	private Dataset dataset;

	@Basic(optional = false)
	@Column(name = "user_id")
	private Integer userId;

	public DatasetUsers() {
	}

	public DatasetUsers(Dataset dataset, Integer userId) {
		this.dataset = dataset;
		this.userId = userId;
	}

	public Dataset getDataset() {
		return this.dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DatasetUsers)) {
			return false;
		}

		DatasetUsers rhs = (DatasetUsers) obj;
		return new EqualsBuilder().append(this.dataset, rhs.dataset).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(61, 131).append(this.dataset).toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DatasetUser [dataset=");
		builder.append(this.dataset);
		builder.append(", userId=");
		builder.append(this.userId);
		builder.append("]");
		return builder.toString();
	}

}
