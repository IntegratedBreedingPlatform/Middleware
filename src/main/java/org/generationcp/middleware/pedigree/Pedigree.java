package org.generationcp.middleware.pedigree;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "pedigree")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "pedigree")
public class Pedigree implements Serializable {

	/**
	 * Default serial version uid.
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name = "pedigreeIdStringGenerator", table = "sequence", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
	pkColumnValue = "pedigree_id", allocationSize = 500)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pedigreeIdStringGenerator")
	@Basic(optional = false)
	@Column(name = "id")
	public Integer id;

	@Column(name = "pedigree_string")
	public String pedigreeString;

	@Column(name = "algorithm_used")
	public String algorithmUsed;

	@Column(name = "level_used")
	public int levels;

  	@Column(name = "invalidate")
	public int invalidate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getPedigreeString() {
		return pedigreeString;
	}
	
	public void setPedigreeString(String pedigreeString) {
		this.pedigreeString = pedigreeString;
	}
	
	public String getAlgorithmUsed() {
		return algorithmUsed;
	}
	
	public void setAlgorithmUsed(String algorithmUsed) {
		this.algorithmUsed = algorithmUsed;
	}
	
	public int getLevels() {
		return levels;
	}
	
	public void setLevels(int levels) {
		this.levels = levels;
	}

  	public int getInvalidate() {
		return invalidate;
	}

  	public void setInvalidate(int invalidate) {
		this.invalidate = invalidate;
  	}
}
