package org.generationcp.middleware.pojos;

import java.io.Serializable;

public class NumericRange implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Double start;
	private Double end;
	
	public NumericRange(Double start, Double end)
	{
		super();
		this.start = start;
		this.end = end;
	}

	public Double getStart()
	{
		return start;
	}

	public void setStart(Double start)
	{
		this.start = start;
	}

	public Double getEnd()
	{
		return end;
	}

	public void setEnd(Double end)
	{
		this.end = end;
	}
	
}
