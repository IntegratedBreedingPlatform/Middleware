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
package org.generationcp.middleware.domain.h2h;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;

/**
 * The different trait info types used - NUMERIC, CHARACTER, CATEGORICAL
 *
 */
public enum TraitType {

	NUMERIC(Arrays.asList(
            TermId.NUMERIC_VARIABLE.getId(),
            TermId.DATE_VARIABLE.getId())),
	CHARACTER(Arrays.asList(
            TermId.CHARACTER_VARIABLE.getId())),
	CATEGORICAL(Arrays.asList(
            TermId.CATEGORICAL_VARIABLE.getId()));

	
	private final List<Integer> typeIds;
	
	private TraitType(List<Integer> termIds) {
		this.typeIds = termIds;
	}
		
	public List<Integer> getTypeIds() {
		return this.typeIds;
	}
	
	public static TraitType valueOf(Integer typeId){
		for (TraitType type : TraitType.values()){
			if (type.getTypeIds().contains(typeId)){
				return type;
			}
		}
		return null;
	}
}
