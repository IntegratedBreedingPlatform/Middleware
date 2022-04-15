package org.generationcp.middleware.api.file;

import java.util.List;

public class FileMetadataFilterRequest {

	private String observationUnitUUID;
	private String germplasmUUID;
	private String variableName;
	private String fileName;
	private List<Integer> instanceIds;

	public String getObservationUnitUUID() {
		return this.observationUnitUUID;
	}

	public void setObservationUnitUUID(final String observationUnitUUID) {
		this.observationUnitUUID = observationUnitUUID;
	}

	public String getGermplasmUUID() {
		return germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}

	public String getVariableName() {
		return this.variableName;
	}

	public void setVariableName(final String variableName) {
		this.variableName = variableName;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public List<Integer> getInstanceIds() {
		return instanceIds;
	}

	public void setInstanceIds(final List<Integer> instanceIds) {
		this.instanceIds = instanceIds;
	}
}
