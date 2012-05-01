package org.generationcp.middleware.pojos;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NumericDataElement implements Serializable
{
	private static final long serialVersionUID = -4284129132975100671L;

	private Integer ounitId;
	private Integer variateId;
	private String variateName;
	private Integer value;
	
	public NumericDataElement(Integer ounitId, Integer factorId,
			String factorName, Integer value)
	{
		super();
		this.ounitId = ounitId;
		this.variateId = factorId;
		this.variateName = factorName;
		this.value = value;
	}

	public Integer getOunitId()
	{
		return ounitId;
	}

	public void setOunitId(Integer ounitId)
	{
		this.ounitId = ounitId;
	}

	public Integer getFactorId()
	{
		return variateId;
	}

	public void setFactorId(Integer factorId)
	{
		this.variateId = factorId;
	}

	public String getFactorName()
	{
		return variateName;
	}

	public void setFactorName(String factorName)
	{
		this.variateName = factorName;
	}

	public Integer getValue()
	{
		return value;
	}

	public void setValue(Integer value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "NumericDataElement [ounitId=" + ounitId + ", variateId="
				+ variateId + ", variateName=" + variateName + ", value="
				+ value + "]";
	}

	@Override
	public boolean equals(Object obj) 
	{
	   if (obj == null)
		   return false;
	   if (obj == this) 
		   return true; 
	   if (!(obj instanceof NumericDataElement)) 
		   return false;
	
	   NumericDataElement rhs = (NumericDataElement) obj;
	   return new EqualsBuilder()
	                 .appendSuper(super.equals(obj))
	                 .append(ounitId, rhs.ounitId)
	                 .append(variateId, rhs.variateId)
	                 .isEquals();
	}
	
	@Override
	public int hashCode() 
	{
	     return new HashCodeBuilder(41, 29).
	       append(ounitId).
	       append(variateId).
	       toHashCode();
	}
}
