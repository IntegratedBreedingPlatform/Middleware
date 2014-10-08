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

import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.util.Debug;

import java.util.ArrayList;
import java.util.List;

/** 
 * Contains the details of a trial environment - id and variables.
 */
public class TrialEnvironment {

	private int id;
	private VariableList variables;
	private LocationDto location;
    private List<TraitInfo> traits;
	private StudyReference study;
	
	
    public TrialEnvironment(int id) {
        this.id = id;
    }
    
    public TrialEnvironment(int id, VariableList variables) {
        this.id = id;
        this.variables = variables;
    }
    
    public TrialEnvironment(int id, LocationDto location, StudyReference study) {
        this.id = id;
        this.location = location;
        this.study = study;
    }
    
    public TrialEnvironment(int id, LocationDto location, List<TraitInfo> traits, StudyReference study) {
        this.id = id;
        this.traits = traits;
        this.location = location;
        this.study = study;
        this.variables = null;
    }
    
	public int getId() {
		return id;
	}

	public boolean containsValueByLocalName(String localName, String value) {
		return variables.containsValueByLocalName(localName, value);
	}
	
    public VariableList getVariables() {
        return variables;
    }
    
    public List<TraitInfo> getTraits() {
        return traits;
    }
    
	public void setLocation(LocationDto location) {
		this.location = location;
	}

	public LocationDto getLocation() {
		return location;
	}

	public StudyReference getStudy() {
		return study;
	}

    public void setTraits(List<TraitInfo> traits) {
        this.traits = traits;
    }

    public void addTrait(TraitInfo trait) {
        if (traits == null){
            traits = new ArrayList<TraitInfo>();
        }
        traits.add(trait);
    }
    
    

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TrialEnvironment other = (TrialEnvironment) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
    

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrialEnvironment [id=");
		builder.append(id);
		builder.append(", variables=");
		builder.append(variables);
		builder.append(", location=");
		builder.append(location);
		builder.append(", traits=");
		builder.append(traits);
		builder.append(", study=");
		builder.append(study);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "Trial Environment: " + id);
		if (variables != null) {
			variables.print(indent + 3);
		}
		if (location != null) {
			location.print(indent + 3);
		}
        if (traits != null) {
            Debug.println(indent + 3, "Traits: " + traits.size());
            for (TraitInfo trait : traits){
                trait.print(indent + 6);
            }
        }
		if (study != null) {
			study.print(indent + 3);
		}
		
	}
}
