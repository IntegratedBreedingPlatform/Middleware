package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "data_c")
public class CharacterData implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final String GET_BY_OUNIT_ID_LIST = "select dc.ounitid, dc.variatid, v.vname, dc.dvalue " +
			"from data_c dc join variate v on dc.variatid = v.variatid " +
			"where dc.ounitid in (:ounitIdList)";

	@EmbeddedId
	protected CharacterDataPK id;
	
	@Column(name = "dvalue")
	private String value;
	
	@ManyToOne(targetEntity = Variate.class)
	@JoinColumn(name = "variatid", nullable = false, insertable=false, updatable=false)
	private Variate variate;
	
	public CharacterData()
	{
	}

	public CharacterData(CharacterDataPK id, String value)
	{
		super();
		this.id = id;
		this.value = value;
	}

	public CharacterDataPK getId()
	{
		return id;
	}

	public void setId(CharacterDataPK id)
	{
		this.id = id;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public Variate getVariate()
	{
		return variate;
	}

	public void setVariate(Variate variate)
	{
		this.variate = variate;
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
		CharacterData other = (CharacterData) obj;
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
		return "CharacterData [id=" + id + ", value=" + value + "]";
	}
	
}
