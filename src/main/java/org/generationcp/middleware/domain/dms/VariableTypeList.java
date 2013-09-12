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
package org.generationcp.middleware.domain.dms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;

/** 
 * List of variable types.
 */
public class VariableTypeList {

	private List<VariableType> variableTypes = new ArrayList<VariableType>();
	
	public void add(VariableType variableType) {
		variableTypes.add(variableType);
	}
	
	public void addAll(VariableTypeList variableTypes) {
		for (VariableType variableType : variableTypes.getVariableTypes()) {
			if (findById(variableType.getId()) == null) {
		        this.variableTypes.add(variableType);
			}
		}
	}

	public VariableType findById(TermId termId) {
		return findById(termId.getId());
	}
		
    public VariableType findById(int id) {
		if (variableTypes != null) {
			for (VariableType variableType : variableTypes) {
				if (variableType.getId() == id) {
					return variableType;
				}
			}
		}
		return null;
	}
    
    public VariableType findByLocalName(String localName) {
    	if (variableTypes != null) {
			for (VariableType variableType : variableTypes) {
				if (variableType.getLocalName().equals(localName)) {
					return variableType;
				}
			}
    	}
    	return null;
    }

	public List<VariableType> getVariableTypes() {
		return variableTypes;
	}

	public void setVariableTypes(List<VariableType> variableTypes) {
		this.variableTypes = variableTypes;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableTypeList [variableTypes=");
		builder.append(variableTypes);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		if (variableTypes != null) {
			for (VariableType variableType : variableTypes) {
				variableType.print(indent);
			}
		}
	}

	public VariableTypeList getFactors() {
		VariableTypeList factors = new VariableTypeList();
		if (variableTypes != null) {
			for (VariableType variableType : variableTypes) {
				if (!isVariate(variableType)) {
					factors.add(variableType);
				}
			}
		}
		return factors.sort();
	}

	public VariableTypeList getVariates() {
		VariableTypeList variates = new VariableTypeList();
		if (variableTypes != null) {
			for (VariableType variableType : variableTypes) {
				if (isVariate(variableType)) {
					variates.add(variableType);
				}
			}
		}
		return variates.sort();
	}

	private boolean isVariate(VariableType variableType) {
		return variableType.getStandardVariable().getStoredIn().getId() == TermId.OBSERVATION_VARIATE.getId() ||
			   variableType.getStandardVariable().getStoredIn().getId() == TermId.CATEGORICAL_VARIATE.getId();
	}
	
	public VariableTypeList sort(){
		Collections.sort(variableTypes);
		return this;
	}
	
	public void makeRoom(int rank) {
		for (VariableType vtype : variableTypes) {
			if (vtype.getRank() >= rank) {
				vtype.setRank(vtype.getRank() + 1);
			}
		}
	}
	
	public void allocateRoom(int size) {
		for (VariableType vtype : variableTypes) {
			vtype.setRank(vtype.getRank() + size);
		}
	}
	
	public int size() {
		return (variableTypes != null ? variableTypes.size() : 0);
	}
}
