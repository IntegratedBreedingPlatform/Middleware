package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "steffect")
public class StudyEffect implements Serializable
{
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private StudyEffectPK id;
	
	@Basic(optional = false)
	@Column(name = "effectname")
	private String name;
	
	public StudyEffect()
	{
	}

	public StudyEffect(StudyEffectPK id, String name)
	{
		super();
		this.id = id;
		this.name = name;
	}

	public StudyEffectPK getId()
	{
		return id;
	}

	public void setId(StudyEffectPK id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return "StudyEffect [id=" + id + ", name=" + name + "]";
	}
	
	@Override
	public boolean equals(Object obj) 
	{
	   if (obj == null)
		   return false;
	   if (obj == this) 
		   return true; 
	   if (!(obj instanceof StudyEffect)) 
		   return false;
	
	   StudyEffect rhs = (StudyEffect) obj;
	   return new EqualsBuilder()
	                 .appendSuper(super.equals(obj))
	                 .append(id, rhs.id)
	                 .isEquals();
	}
	
	@Override
	public int hashCode() 
	{
	     return new HashCodeBuilder(13, 79)
	       .append(id)
	       .toHashCode();
	}
}
