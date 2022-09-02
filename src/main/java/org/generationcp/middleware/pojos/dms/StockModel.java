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

package org.generationcp.middleware.pojos.dms;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
import org.generationcp.middleware.util.CrossExpansionUtil;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_stock
 * <p>
 * Any stock can be globally identified by the combination of organism, uniquename and stock type. A stock is the physical entities, either
 * living or preserved, held by col
 * lections. Stocks belong to a collection; they have IDs, type, organism, description and may have a
 * genotype.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "stock")
public class StockModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "stock_id")
	private Integer stockId;

	@ManyToOne(targetEntity = Germplasm.class)
	@JoinColumn(name = "dbxref_id", nullable = true)
	private Germplasm germplasm;

	/**
	 * The organism_id is the organism to which the stock belongs. This column is mandatory.
	 */
	@Column(name = "organism_id")
	private Integer organismId;

	@Basic(optional = false)
	@Column(name = "uniquename")
	private String uniqueName;

	/**
	 * The description is the genetic description provided in the stock list.
	 */
	@Column(name = "description")
	private String description;

	@Basic(optional = false)
	@Column(name = "is_obsolete")
	private Boolean isObsolete;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private DmsProject project;

	@Column(name = "cross_value")
	private String cross;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "stockModel")
	@BatchSize(size = 5000)
	private Set<StockProperty> properties;

	public StockModel() {
	}

	public StockModel(final Integer stockId, final Integer organismId, final String uniqueName,
		final String description, final Boolean isObsolete) {
		super();
		this.stockId = stockId;
		this.organismId = organismId;
		this.uniqueName = uniqueName;
		this.description = description;
		this.isObsolete = isObsolete;
	}

	public StockModel(final Integer studyId, final StudyEntryDto studyEntryDto) {
		this.setProject(new DmsProject(studyId));
		this.setGermplasm(new Germplasm(Integer.valueOf(studyEntryDto.getGid())));
		this.setUniqueName(studyEntryDto.getEntryNumber().toString());
		this.setIsObsolete(false);
		this.setCross(studyEntryDto.getCross());

		final Set<StockProperty> stockProperties = new HashSet<>();
		final Iterator<Map.Entry<Integer, StudyEntryPropertyData>> iterator = studyEntryDto.getProperties().entrySet().iterator();
		while (iterator.hasNext()) {
			final StudyEntryPropertyData studyEntryPropertyData = iterator.next().getValue();
			final StockProperty stockProperty = new StockProperty(this, studyEntryPropertyData.getVariableId(),
				studyEntryPropertyData.getValue(), studyEntryPropertyData.getCategoricalValueId());
			stockProperties.add(stockProperty);
		}
		this.setProperties(stockProperties);
	}

	public Integer getStockId() {
		return this.stockId;
	}

	public void setStockId(Integer stockId) {
		this.stockId = stockId;
	}

	public Germplasm getGermplasm() {
		return germplasm;
	}

	public void setGermplasm(Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	public Integer getOrganismId() {
		return this.organismId;
	}

	public void setOrganismId(Integer organismId) {
		this.organismId = organismId;
	}

	public String getUniqueName() {
		return this.uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsObsolete() {
		return this.isObsolete;
	}

	public void setIsObsolete(Boolean isObsolete) {
		this.isObsolete = isObsolete;
	}

	public String getCross() {
		return cross;
	}

	public void setCross(final String cross) {
		this.cross = cross;
	}

	public Set<StockProperty> getProperties() {
		return this.properties;
	}

	public void setProperties(Set<StockProperty> properties) {
		this.properties = properties;
	}

	public DmsProject getProject() {
		return project;
	}

	public void setProject(final DmsProject project) {
		this.project = project;
	}

	public void truncateCrossValueIfNeeded() {
		String cross = CrossExpansionUtil.truncateCrossValueIfNeeded(this.getCross());
		this.setCross(cross);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.germplasm == null ? 0 : this.germplasm.hashCode());
		result = prime * result + (this.description == null ? 0 : this.description.hashCode());
		result = prime * result + (this.isObsolete == null ? 0 : this.isObsolete.hashCode());
		result = prime * result + (this.organismId == null ? 0 : this.organismId.hashCode());
		result = prime * result + (this.stockId == null ? 0 : this.stockId.hashCode());
		result = prime * result + (this.uniqueName == null ? 0 : this.uniqueName.hashCode());
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		StockModel other = (StockModel) obj;
		if (this.germplasm == null) {
			if (other.germplasm != null) {
				return false;
			}
		} else if (!this.germplasm.equals(other.germplasm)) {
			return false;
		}
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.isObsolete == null) {
			if (other.isObsolete != null) {
				return false;
			}
		} else if (!this.isObsolete.equals(other.isObsolete)) {
			return false;
		}
		if (this.organismId == null) {
			if (other.organismId != null) {
				return false;
			}
		} else if (!this.organismId.equals(other.organismId)) {
			return false;
		}
		if (this.stockId == null) {
			if (other.stockId != null) {
				return false;
			}
		} else if (!this.stockId.equals(other.stockId)) {
			return false;
		}
		if (this.uniqueName == null) {
			if (other.uniqueName != null) {
				return false;
			}
		} else if (!this.uniqueName.equals(other.uniqueName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Stock [stockId=");
		builder.append(this.stockId);
		builder.append(", germplasm=");
		builder.append(this.germplasm);
		builder.append(", organismId=");
		builder.append(this.organismId);
		builder.append(", uniqueName=");
		builder.append(this.uniqueName);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", isObsolete=");
		builder.append(this.isObsolete);
		builder.append(", project=");
		builder.append(this.project);
		builder.append("]");
		return builder.toString();
	}

}
