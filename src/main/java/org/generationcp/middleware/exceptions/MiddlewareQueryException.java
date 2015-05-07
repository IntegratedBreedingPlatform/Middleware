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

/**
 * Exceptions for database-related Middleware issues.
 *
 */
public class MiddlewareQueryException extends MiddlewareException{

    private static final long serialVersionUID = 1L;
    
    private String code;

    public MiddlewareQueryException(String message) {
        super(message);
    }

    public MiddlewareQueryException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MiddlewareQueryException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public String getCode() {
        return this.code;
    }
}
