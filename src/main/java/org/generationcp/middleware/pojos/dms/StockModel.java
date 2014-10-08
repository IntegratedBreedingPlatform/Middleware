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
package org.generationcp.middleware.pojos.dms;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_stock
 * 
 * Any stock can be globally identified by the combination of organism, uniquename and stock type. 
 * A stock is the physical entities, either living or preserved, held by collections. 
 * Stocks belong to a collection; they have IDs, type, organism, description and may have a genotype.
 * 
 * @author Joyce Avestro
 *
 */
@Entity
@Table(	name = "stock", 
		uniqueConstraints = {
			@UniqueConstraint(columnNames = { "organism_id", "uniquename", "type_id" })
		})
public class StockModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "stock_id")
	private Integer stockId;

	/**
	 * The dbxref_id is an optional primary stable identifier for this stock. 
	 * Secondary indentifiers and external dbxrefs go in table: stock_dbxref.
	 */
	@Column(name = "dbxref_id")
	private Integer dbxrefId;
	
	/**
	 * The organism_id is the organism to which the stock belongs. This column is mandatory.
	 */
	@Column(name = "organism_id")
	private Integer organismId;
	
	/**
	 * The name is a human-readable local name for a stock.
	 */
	@Column(name = "name")
	private String name;
	
	@Basic(optional = false)
	@Column(name = "uniquename")
	private String uniqueName;

	@Column(name = "value")
	private String value;
	
	/**
	 * The description is the genetic description provided in the stock list.
	 */
	@Column(name = "description")
	private String description;
	
	/**
	 * The type_id foreign key links to a controlled vocabulary of stock types. 
	 * The would include living stock, genomic DNA, preserved specimen. 
	 * Secondary cvterms for stocks would go in stock_cvterm.
	 * References cvterm
	 */
    @Column(name="type_id")
    private Integer typeId;

	@Basic(optional = false)
    @Column(name="is_obsolete")
    private Boolean isObsolete;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="stockModel")
	private Set<StockProperty> properties;
	
	public StockModel(){
	}
	
    public StockModel(Integer stockId, Integer dbxrefId, Integer organismId, String name, String uniqueName,
            String value, String description, Integer typeId, Boolean isObsolete) {
        super();
        this.stockId = stockId;
        this.dbxrefId = dbxrefId;
        this.organismId = organismId;
        this.name = name;
        this.uniqueName = uniqueName;
        this.value = value;
        this.description = description;
        this.typeId = typeId;
        this.isObsolete = isObsolete;
    }

	public Integer getStockId() {
		return stockId;
	}

	public void setStockId(Integer stockId) {
		this.stockId = stockId;
	}

	public Integer getDbxrefId() {
		return dbxrefId;
	}

	public void setDbxrefId(Integer dbxrefId) {
		this.dbxrefId = dbxrefId;
	}

	public Integer getOrganismId() {
		return organismId;
	}

	public void setOrganismId(Integer organismId) {
		this.organismId = organismId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Boolean getIsObsolete() {
		return isObsolete;
	}

	public void setIsObsolete(Boolean isObsolete) {
		this.isObsolete = isObsolete;
	}

	public Set<StockProperty> getProperties() {
		return properties;
	}

	public void setProperties(Set<StockProperty> properties) {
		this.properties = properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dbxrefId == null) ? 0 : dbxrefId.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((isObsolete == null) ? 0 : isObsolete.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((organismId == null) ? 0 : organismId.hashCode());
		result = prime * result + ((stockId == null) ? 0 : stockId.hashCode());
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
		result = prime * result
				+ ((uniqueName == null) ? 0 : uniqueName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StockModel other = (StockModel) obj;
		if (dbxrefId == null) {
			if (other.dbxrefId != null)
				return false;
		} else if (!dbxrefId.equals(other.dbxrefId))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (isObsolete == null) {
			if (other.isObsolete != null)
				return false;
		} else if (!isObsolete.equals(other.isObsolete))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (organismId == null) {
			if (other.organismId != null)
				return false;
		} else if (!organismId.equals(other.organismId))
			return false;
		if (stockId == null) {
			if (other.stockId != null)
				return false;
		} else if (!stockId.equals(other.stockId))
			return false;
		if (typeId == null) {
			if (other.typeId != null)
				return false;
		} else if (!typeId.equals(other.typeId))
			return false;
		if (uniqueName == null) {
			if (other.uniqueName != null)
				return false;
		} else if (!uniqueName.equals(other.uniqueName))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Stock [stockId=");
		builder.append(stockId);
		builder.append(", dbxrefId=");
		builder.append(dbxrefId);
		builder.append(", organismId=");
		builder.append(organismId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", uniqueName=");
		builder.append(uniqueName);
		builder.append(", value=");
		builder.append(value);
		builder.append(", description=");
		builder.append(description);
		builder.append(", typeId=");
		builder.append(typeId);
		builder.append(", isObsolete=");
		builder.append(isObsolete);
		builder.append("]");
		return builder.toString();
	}  
	
}