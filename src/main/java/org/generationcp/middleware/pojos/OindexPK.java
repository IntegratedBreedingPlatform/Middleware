package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class OindexPK implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
    @Column(name = "ounitid")
	private Integer observationUnitId;
	
	@Basic(optional = false)
    @Column(name = "factorid")
	private Integer factorId;
	
	@Basic(optional = false)
    @Column(name = "levelno")
	private Integer levelNumber;
	
	@Basic(optional = false)
    @Column(name = "represno")
	private Integer representationNumber;

	public OindexPK()
	{
	}
	
	public OindexPK(Integer observationUnitId, Integer factorId,
			Integer levelNumber, Integer representationNumber)
	{
		super();
		this.observationUnitId = observationUnitId;
		this.factorId = factorId;
		this.levelNumber = levelNumber;
		this.representationNumber = representationNumber;
	}

	public Integer getObservationUnitId()
	{
		return observationUnitId;
	}

	public void setObservationUnitId(Integer observationUnitId)
	{
		this.observationUnitId = observationUnitId;
	}

	public Integer getFactorId()
	{
		return factorId;
	}

	public void setFactorId(Integer factorId)
	{
		this.factorId = factorId;
	}

	public Integer getLevelNumber()
	{
		return levelNumber;
	}

	public void setLevelNumber(Integer levelNumber)
	{
		this.levelNumber = levelNumber;
	}

	public Integer getRepresentationNumber()
	{
		return representationNumber;
	}

	public void setRepresentationNumber(Integer representationNumber)
	{
		this.representationNumber = representationNumber;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((factorId == null) ? 0 : factorId.hashCode());
		result = prime * result
				+ ((levelNumber == null) ? 0 : levelNumber.hashCode());
		result = prime
				* result
				+ ((observationUnitId == null) ? 0 : observationUnitId
						.hashCode());
		result = prime
				* result
				+ ((representationNumber == null) ? 0 : representationNumber
						.hashCode());
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
		OindexPK other = (OindexPK) obj;
		if (factorId == null)
		{
			if (other.factorId != null)
				return false;
		} else if (!factorId.equals(other.factorId))
			return false;
		if (levelNumber == null)
		{
			if (other.levelNumber != null)
				return false;
		} else if (!levelNumber.equals(other.levelNumber))
			return false;
		if (observationUnitId == null)
		{
			if (other.observationUnitId != null)
				return false;
		} else if (!observationUnitId.equals(other.observationUnitId))
			return false;
		if (representationNumber == null)
		{
			if (other.representationNumber != null)
				return false;
		} else if (!representationNumber.equals(other.representationNumber))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "OindexPK [observationUnitId=" + observationUnitId
				+ ", factorId=" + factorId + ", levelNumber=" + levelNumber
				+ ", representationNumber=" + representationNumber + "]";
	}
	
}
