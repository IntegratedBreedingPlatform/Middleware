package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.service.api.BrapiView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoProperty
public class MethodDTO {

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private Map<String, String> additionalInfo = new HashMap<>();

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private String bibliographicalReference;

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private List<ExternalReferenceDTO> externalReferences;

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private String methodClass;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String methodClassAttribute;
	private String description;
	private String formula;
	private String methodDbId;
	private String methodName;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String name;
	private OntologyReferenceDTO ontologyReferenceObject = new OntologyReferenceDTO();

	@JsonView(BrapiView.BrapiV1_3.class)
	private String reference;

	// Getter Methods

	@JsonProperty("class")
	public String getMethodClassAttribute() {
		return this.methodClassAttribute;
	}

	public String getDescription() {
		return this.description;
	}

	public String getFormula() {
		return this.formula;
	}

	public String getMethodDbId() {
		return this.methodDbId;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public String getName() {
		return this.name;
	}

	public OntologyReferenceDTO getOntologyReference() {
		return this.ontologyReferenceObject;
	}

	public String getReference() {
		return this.reference;
	}

	// Setter Methods

	public void setMethodClassAttribute(final String methodClassAttribute) {
		this.methodClassAttribute = methodClassAttribute;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setFormula(final String formula) {
		this.formula = formula;
	}

	public void setMethodDbId(final String methodDbId) {
		this.methodDbId = methodDbId;
	}

	public void setMethodName(final String methodName) {
		this.methodName = methodName;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOntologyReference(final OntologyReferenceDTO ontologyReferenceObject) {
		this.ontologyReferenceObject = ontologyReferenceObject;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getBibliographicalReference() {
		return this.bibliographicalReference;
	}

	public void setBibliographicalReference(final String bibliographicalReference) {
		this.bibliographicalReference = bibliographicalReference;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public OntologyReferenceDTO getOntologyReferenceObject() {
		return this.ontologyReferenceObject;
	}

	public void setOntologyReferenceObject(final OntologyReferenceDTO ontologyReferenceObject) {
		this.ontologyReferenceObject = ontologyReferenceObject;
	}

	public String getMethodClass() {
		return this.methodClass;
	}

	public void setMethodClass(final String methodClass) {
		this.methodClass = methodClass;
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
