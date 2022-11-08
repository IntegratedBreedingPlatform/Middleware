package org.generationcp.middleware.domain.dataset;

import org.generationcp.middleware.api.nametype.GermplasmNameTypeDTO;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeRequestDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.List;

@AutoProperty
public class ProjectPropertiesDTO {

	private List<MeasurementVariable> variables = new ArrayList<MeasurementVariable>();

	private List<GermplasmNameTypeDTO> nameTypes = new ArrayList<GermplasmNameTypeDTO>();

	public ProjectPropertiesDTO(){

	}

	public List<GermplasmNameTypeDTO> getNameTypes() {
		return this.nameTypes;
	}

	public void setNameTypes(final List<GermplasmNameTypeDTO> nameTypes) {
		this.nameTypes = nameTypes;
	}

	public List<MeasurementVariable> getVariables() {
		return this.variables;
	}

	public void setVariables(final List<MeasurementVariable> variables) {
		this.variables = variables;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}
}
