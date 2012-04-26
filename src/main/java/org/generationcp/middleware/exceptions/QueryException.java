package org.generationcp.middleware.exceptions;

public class QueryException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public QueryException(String message)
	{
		super(message);
	}

	public QueryException(String message, Throwable cause) {
		super(message, cause);
	}
}
