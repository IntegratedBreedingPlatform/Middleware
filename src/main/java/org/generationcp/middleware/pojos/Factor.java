package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@Entity
@Table(name = "factor")
public class Factor implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final String GET_GID_FROM_NUMERIC_LEVELS_GIVEN_OBSERVATION_UNIT_IDS = "SELECT DISTINCT ln.lvalue " +
			"FROM factor f INNER JOIN oindex ou ON f.factorid = ou.factorid " +
			"AND ou.ounitid IN (:ounitids) " +
			"INNER JOIN level_n ln ON ln.factorid = f.factorid AND ln.labelid = f.labelid AND ou.levelno = ln.levelno " +
			"WHERE f.traitid = 251 AND f.fname = 'GID'";
	
	public static final String GET_GID_FROM_CHARACTER_LEVELS_GIVEN_OBSERVATION_UNIT_IDS = "SELECT DISTINCT lc.lvalue " +
			"FROM factor f INNER JOIN oindex ou ON f.factorid = ou.factorid " +
			"AND ou.ounitid IN (:ounitids) " +
			"INNER JOIN level_c lc ON lc.factorid = f.factorid AND lc.labelid = f.labelid AND ou.levelno = lc.levelno " +
			"WHERE f.traitid = 251 AND f.fname = 'GID'";

	@Id
	@Basic(optional = false)
	@Column(name = "labelid")
	private Integer id;
	
	@Basic(optional = false)
	@Column(name = "factorid")
	private Integer factorId;
	
	@Basic(optional = false)
	@Column(name = "studyid")
	private Integer studyId;
	
	@Basic(optional = false)
	@Column(name = "fname")
	private String name;
	
	@Basic(optional = false)
	@Column(name = "traitid")
	private Integer traitId;
	
	@Basic(optional = false)
	@Column(name = "scaleid")
	private Integer scaleId;
	
	@Basic(optional = false)
	@Column(name = "tmethid")
	private Integer methodId;
	
	@Basic(optional = false)
	@Column(name = "ltype")
	private String dataType;
	
	@Basic(optional = false)
	@Column(name = "tid")
	private Integer tid;
	
	public Factor()
	{
	}

	public Factor(Integer id)
	{
		super();
		this.id = id;
	}

	public Factor(Integer id, Integer factorId, Integer studyId, String name,
			Integer traitId, Integer scaleId, Integer methodId, String dataType)
	{
		super();
		this.id = id;
		this.factorId = factorId;
		this.studyId = studyId;
		this.name = name;
		this.traitId = traitId;
		this.scaleId = scaleId;
		this.methodId = methodId;
		this.dataType = dataType;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getFactorId()
	{
		return factorId;
	}

	public void setFactorId(Integer factorId)
	{
		this.factorId = factorId;
	}

	public Integer getStudyId()
	{
		return studyId;
	}

	public void setStudyId(Integer studyId)
	{
		this.studyId = studyId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Integer getTraitId()
	{
		return traitId;
	}

	public void setTraitId(Integer traitId)
	{
		this.traitId = traitId;
	}

	public Integer getScaleId()
	{
		return scaleId;
	}

	public void setScaleId(Integer scaleId)
	{
		this.scaleId = scaleId;
	}

	public Integer getMethodId()
	{
		return methodId;
	}

	public void setMethodId(Integer methodId)
	{
		this.methodId = methodId;
	}

	public String getDataType()
	{
		return dataType;
	}

	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}
	
	public Integer getTid() 
	{
		return tid;
	}

	public void setTid(Integer tid) 
	{
		this.tid = tid;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Factor other = (Factor) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Factor [id=" + id + ", factorId=" + factorId + ", studyId="
				+ studyId + ", name=" + name + ", traitId=" + traitId
				+ ", scaleId=" + scaleId + ", methodId=" + methodId
				+ ", dataType=" + dataType + "]";
	}
	
}
