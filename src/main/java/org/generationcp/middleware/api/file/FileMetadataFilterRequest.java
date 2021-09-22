package org.generationcp.middleware.api.file;

public class FileMetadataFilterRequest {

	private String observationUnitUUID;
	private String germplasmUUID;
	private String variableName;
	private String fileName;

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
}
