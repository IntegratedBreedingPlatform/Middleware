/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.exceptions;

import org.generationcp.middleware.domain.dms.PhenotypeExceptionDto;

import java.util.Map;

/**
 * Exceptions for Phenotype data issues.
 *
 */
public class PhenotypeException extends MiddlewareQueryException{

	private static final long serialVersionUID = 1L;
	
	private PhenotypeExceptionDto exception;

	private Map<Integer,PhenotypeExceptionDto> exceptions;

    public PhenotypeException(PhenotypeExceptionDto exception) {
    	super(exception.toString());
    	this.exception = exception;
    }
    
    public PhenotypeException(Map<Integer,PhenotypeExceptionDto> exceptions) {
    	super(exceptions.toString());
    	this.exceptions = exceptions;
    }

	public PhenotypeExceptionDto getException() {
		return exception;
	}

	public void setException(PhenotypeExceptionDto exception) {
		this.exception = exception;
	}

	public Map<Integer, PhenotypeExceptionDto> getExceptions() {
		return exceptions;
	}

	public void setExceptions(Map<Integer, PhenotypeExceptionDto> exceptions) {
		this.exceptions = exceptions;
	}

	public String getMessage() {
		if(exceptions!=null) {
			StringBuilder sb = new StringBuilder();
			sb.append("One or more variables are assigned with invalid values:\n\n");
			for (Integer key : exceptions.keySet()) {
				sb.append(exceptions.get(key).toString());
				sb.append("\n");
			}
			sb.append("\n");
			return sb.toString();
		}
		if(exception!=null) {
			StringBuilder sb = new StringBuilder();
			sb.append(exception.toString());
			return sb.toString();
		}
		return super.getMessage();
	}
	
}
