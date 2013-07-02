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
package org.generationcp.middleware.v2.domain.cache;

import java.util.HashMap;
import java.util.Map;

import org.generationcp.middleware.v2.domain.StandardVariable;

/**
 * This class is used to store standard variable in the cache to improve retrieval performance. 
 * When a standard variable is retrieved, it is first checked if it's in the cache. 
 * If it's not, it will be retrieved from the database.
 * 
 * @author Joyce Avestro
 *
 */
public class StandardVariableCache {

	private static final StandardVariableCache instance = new StandardVariableCache();

	private Map<Integer, StandardVariable> values = new HashMap<Integer, StandardVariable>();

	private StandardVariableCache(){
	}
	
	public static StandardVariableCache getInstance(){
        return instance;
	}
	
	/** Retrieves a standard variable from the cache. Returns null if not found. */
	public StandardVariable get(int standardVariableId){
		StandardVariable value = values.get(standardVariableId);
		if (value != null) {
		    return value;
		}
		return null;
	}
	
	public void put(StandardVariable standardVariable){
		values.put(standardVariable.getId(), standardVariable);
	}

	
	
	
}
