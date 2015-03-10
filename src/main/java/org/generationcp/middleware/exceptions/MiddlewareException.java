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

import org.generationcp.middleware.util.Message;

/**
 * Exceptions for non-database Middleware issues.
 *
 */
public class MiddlewareException extends Exception{

    private static final long serialVersionUID = 1L;

    private Message message;
    
    public MiddlewareException(String message) {
        super(message);
    }

    public MiddlewareException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MiddlewareException(String logMessage, String messageKey, String... params) {
        super(logMessage);
        this.message = new Message(messageKey, params);
    }
    
    public String getMessageKey() {
        return this.message.getMessageKey();
    }
    
    public String[] getMessageParameters(){
        return this.message.getMessageParams();
    }
    
}
