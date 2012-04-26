package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "oindex")
public class Oindex implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private OindexPK id;

	public Oindex()
	{
	}
	
	public Oindex(OindexPK id)
	{
		super();
		this.id = id;
	}

	public OindexPK getId()
	{
		return id;
	}

	public void setId(OindexPK id)
	{
		this.id = id;
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
		Oindex other = (Oindex) obj;
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
		return "Oindex [id=" + id + "]";
	}
	
}
