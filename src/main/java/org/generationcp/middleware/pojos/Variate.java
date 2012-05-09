package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries
({
	@NamedQuery  
	(
			name = "getVariatesByStudyID",
			query = "FROM Variate v WHERE v.studyId = :studyId"
	)
})

@Entity
@Table(name = "variate")
public class Variate implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	//string contants for name of queries
    public static final String GET_VARIATES_BY_STUDYID = "getVariatesByStudyID";
    
    public static final String GET_BY_REPRESENTATION_ID = "select distinct {v.*} " +
    	"from variate v join data_n dn on v.variatid = dn.variatid " +
    	"join oindex oi on oi.ounitid = dn.ounitid " +
    	"where oi.represno = :representationId " +
    	"union " +
    	"select distinct {v.*} " +
    	"from variate v join data_c dn on v.variatid = dn.variatid " +
    	"join oindex oi on oi.ounitid = dn.ounitid " +
    	"where oi.represno = :representationId";

	@Id
	@Basic(optional = false)
	@Column(name = "variatid")
	private Integer id;
	
	@Basic(optional = false)
	@Column(name = "studyid")
	private Integer studyId;
	
	@Basic(optional = false)
	@Column(name = "vname")
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
	@Column(name = "dtype")
	private String dataType;
	
	@Basic(optional = false)
	@Column(name = "vtype")
	private String type;
	
	@Basic(optional = false)
	@Column(name = "tid")
	private Integer tid;
	
	public Variate()
	{
	}

	
	public Variate(Integer id)
	{
		super();
		this.id = id;
	}

	public Variate(Integer id, Integer studyId, String name, Integer traitId,
			Integer scaleId, Integer methodId, String dataType, String type)
	{
		super();
		this.id = id;
		this.studyId = studyId;
		this.name = name;
		this.traitId = traitId;
		this.scaleId = scaleId;
		this.methodId = methodId;
		this.dataType = dataType;
		this.type = type;
	}


	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
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

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
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
		Variate other = (Variate) obj;
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
		return "Variate [id=" + id + ", studyId=" + studyId + ", name=" + name
				+ ", traitId=" + traitId + ", scaleId=" + scaleId
				+ ", methodId=" + methodId + ", dataType=" + dataType
				+ ", type=" + type + "]";
	}
	
}
