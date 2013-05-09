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
import org.generationcp.middleware.v2.util.Debug;


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
		    Debug.println(4, "Standard Variable retrieved from cache: " + value); //TODO: Remove line after QA
		    return value;
		}
		return null;
	}
	
	public void put(StandardVariable standardVariable){
		Debug.println(4, "Standard variable put to cache: " + standardVariable); //TODO: Remove line after QA
		values.put(standardVariable.getId(), standardVariable);
	}

	
	
	
}
