/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.dms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.oms.TermId;

/**
 * List of variable types.
 */
public class VariableTypeList implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<DMSVariableType> variableTypes = new ArrayList<DMSVariableType>();
	private final Map<String, DMSVariableType> idVarTypeMap = new HashMap();
	private final Map<String, DMSVariableType> nameVarTypeMap = new HashMap();

	public void add(DMSVariableType variableType) {
		this.variableTypes.add(variableType);

		this.idVarTypeMap.put(Integer.toString(variableType.getId()), variableType);
		if (variableType.getLocalName() != null) {
			this.nameVarTypeMap.put(variableType.getLocalName(), variableType);
		}
	}

	public void addAll(VariableTypeList variableTypes) {
		for (DMSVariableType variableType : variableTypes.getVariableTypes()) {
			if (this.findByLocalName(variableType.getLocalName()) == null) {
				this.variableTypes.add(variableType);

				this.idVarTypeMap.put(Integer.toString(variableType.getId()), variableType);
				if (variableType.getLocalName() != null) {
					this.nameVarTypeMap.put(variableType.getLocalName(), variableType);
				}

			}
		}
	}

	public DMSVariableType findById(TermId termId) {
		return this.findById(termId.getId());
	}

	public DMSVariableType findById(int id) {

		// added for optimization
		if (this.idVarTypeMap != null && this.idVarTypeMap.get(Integer.toString(id)) != null) {
			return this.idVarTypeMap.get(Integer.toString(id));
		}

		if (this.variableTypes != null) {
			for (DMSVariableType variableType : this.variableTypes) {
				if (variableType.getId() == id) {
					return variableType;
				}
			}
		}
		return null;
	}

	public DMSVariableType findByLocalName(String localName) {
		if (localName == null) {
			return null;
		}

		// added for optimization
		if (this.nameVarTypeMap != null && this.nameVarTypeMap.get(localName) != null) {
			return this.nameVarTypeMap.get(localName);
		}

		if (this.variableTypes != null) {
			for (DMSVariableType variableType : this.variableTypes) {
				if (localName.equals(variableType.getLocalName())) {
					return variableType;
				}
			}
		}
		return null;
	}

	public List<DMSVariableType> getVariableTypes() {
		return this.variableTypes;
	}

	public void setVariableTypes(List<DMSVariableType> variableTypes) {
		this.variableTypes = variableTypes;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VariableTypeList [variableTypes=");
		builder.append(this.variableTypes);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		if (this.variableTypes != null) {
			for (DMSVariableType variableType : this.variableTypes) {
				variableType.print(indent);
			}
		}
	}

	public VariableTypeList getFactors() {
		VariableTypeList factors = new VariableTypeList();
		if (this.variableTypes != null) {
			for (DMSVariableType variableType : this.variableTypes) {
				if (!this.isVariate(variableType)) {
					factors.add(variableType);
				}
			}
		}
		return factors.sort();
	}

	public VariableTypeList getVariates() {
		VariableTypeList variates = new VariableTypeList();
		if (this.variableTypes != null) {
			for (DMSVariableType variableType : this.variableTypes) {
				if (this.isVariate(variableType)) {
					variates.add(variableType);
				}
			}
		}
		return variates.sort();
	}

	private boolean isVariate(DMSVariableType variableType) {
		return variableType.getRole() == PhenotypicType.VARIATE;
	}

	public VariableTypeList sort() {
		Collections.sort(this.variableTypes);
		return this;
	}

	public void makeRoom(int rank) {
		for (DMSVariableType vtype : this.variableTypes) {
			if (vtype.getRank() >= rank) {
				vtype.setRank(vtype.getRank() + 1);
			}
		}
	}

	public void allocateRoom(int size) {
		for (DMSVariableType vtype : this.variableTypes) {
			vtype.setRank(vtype.getRank() + size);
		}
	}

	public int size() {
		return this.variableTypes != null ? this.variableTypes.size() : 0;
	}

	public boolean isEmpty() {
		return this.variableTypes == null || this.variableTypes.isEmpty();
	}
}
