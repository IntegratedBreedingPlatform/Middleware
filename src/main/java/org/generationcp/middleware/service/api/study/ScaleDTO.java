package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.service.api.BrapiView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScaleDTO {

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private Map<String, String> additionalInfo = new HashMap<>();

	private String dataType;
	private Integer decimalPlaces;

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private List<ExternalReferenceDTO> externalReferences;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String name;

	private OntologyReferenceDTO ontologyReference = new OntologyReferenceDTO();
	private String scaleDbId;
	private String scaleName;
	private ValidValuesDTO validValues = new ValidValuesDTO();

	@JsonView(BrapiView.BrapiV1_3.class)
	private String xref;

	// Getter Methods

	public String getDataType() {
		return this.dataType;
	}

	public Integer getDecimalPlaces() {
		return this.decimalPlaces;
	}

	public String getName() {
		return this.name;
	}

	public OntologyReferenceDTO getOntologyReference() {
		return this.ontologyReference;
	}

	public String getScaleDbId() {
		return this.scaleDbId;
	}

	public String getScaleName() {
		return this.scaleName;
	}

	public ValidValuesDTO getValidValues() {
		return this.validValues;
	}

	public String getXref() {
		return this.xref;
	}

	// Setter Methods

	public void setDataType(final String dataType) {
		this.dataType = dataType;
	}

	public void setDecimalPlaces(final Integer decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOntologyReference(final OntologyReferenceDTO ontologyReferenceObject) {
		this.ontologyReference = ontologyReferenceObject;
	}

	public void setScaleDbId(final String scaleDbId) {
		this.scaleDbId = scaleDbId;
	}

	public void setScaleName(final String scaleName) {
		this.scaleName = scaleName;
	}

	public void setValidValues(final ValidValuesDTO validValuesObject) {
		this.validValues = validValuesObject;
	}

	public void setXref(final String xref) {
		this.xref = xref;
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}
}
