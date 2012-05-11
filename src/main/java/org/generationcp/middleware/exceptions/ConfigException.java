package org.generationcp.middleware.exceptions;

public class ConfigException extends RuntimeException 
{
	private static final long serialVersionUID = 1L;
	
	public ConfigException(String message)
	{
		super(message);
	}
}
