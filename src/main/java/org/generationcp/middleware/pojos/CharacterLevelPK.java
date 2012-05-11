package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Embeddable
public class CharacterLevelPK implements Serializable {
	
	private static final long serialVersionUID = -2491179486836968854L;

	@Basic(optional = false)
    @Column(name = "labelid")
	private Integer labelId;
	
	@Basic(optional = false)
    @Column(name = "factorid")
	private Integer factorId;
	
	@Basic(optional = false)
    @Column(name = "levelno")
	private Integer levelNumber;
	
	public CharacterLevelPK() {
		
	}
	
	public CharacterLevelPK(Integer labelId, Integer factorId, Integer levelNumber) {
		super();
		this.labelId = labelId;
		this.factorId = factorId;
		this.levelNumber = levelNumber;
	}
	
	public Integer getLabelId() {
		return labelId;
	}

	public void setLabelId(Integer labelId) {
		this.labelId = labelId;
	}

	public Integer getFactorId() {
		return factorId;
	}

	public void setFactorId(Integer factorId) {
		this.factorId = factorId;
	}

	public Integer getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(Integer levelNumber) {
		this.levelNumber = levelNumber;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
	   if (obj == null)
		   return false;
	   if (obj == this) 
		   return true; 
	   if (!(obj instanceof CharacterLevelPK)) 
		   return false;
	
	   CharacterLevelPK rhs = (CharacterLevelPK) obj;
	   return new EqualsBuilder()
	                 .appendSuper(super.equals(obj))
	                 .append(labelId, rhs.labelId)
	                 .append(factorId, rhs.factorId)
	                 .append(levelNumber, rhs.levelNumber)
	                 .isEquals();
	}
	
	@Override
	public int hashCode() 
	{
	     return new HashCodeBuilder(11, 17).
	       append(labelId).
	       append(factorId).
	       append(levelNumber).
	       toHashCode();
	}
	
	@Override
	public String toString()
	{
		return "CharacterLevelPK [labelId=" + labelId
				+ ", factorId=" + factorId
				+ ", levelNumber=" + levelNumber + "]";
	}

}
