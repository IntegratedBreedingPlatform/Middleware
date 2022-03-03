package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.service.api.BrapiView;
import org.generationcp.middleware.service.api.study.VariableDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraitDTO {

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private Map<String, String> additionalInfo = new HashMap<>();

	private final List<String> alternativeAbbreviations = new ArrayList<>();
	private String attribute;
	private String entity;

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private List<ExternalReferenceDTO> externalReferences;
	private String mainAbbreviation;

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private String traitClass;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String traitClassAttribute;
	@JsonView(BrapiView.BrapiV1_3.class)
	private String description;

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private String traitDescription;

	private String name;
	private OntologyReferenceDTO ontologyReferenceObject = new OntologyReferenceDTO();
	private String status;

	private List<String> synonyms = new ArrayList<>();
	private String traitDbId;
	private String traitName;
	@JsonView(BrapiView.BrapiV1_3.class)
	private String xref;

	// Getter Methods

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public List<String> getAlternativeAbbreviations() {
		return this.alternativeAbbreviations;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public String getTraitDescription() {
		return this.description;
	}

	public void setTraitDescription(final String traitDescription) {
		this.description = traitDescription;
	}

	public String getAttribute() {
		return this.attribute;
	}

	@JsonProperty("class")
	public String getTraitClassAttribute() {
		return this.traitClassAttribute;
	}

	public String getDescription() {
		return this.description;
	}

	public String getEntity() {
		return this.entity;
	}

	public String getMainAbbreviation() {
		return this.mainAbbreviation;
	}

	public String getName() {
		return this.name;
	}

	public OntologyReferenceDTO getOntologyReference() {
		return this.ontologyReferenceObject;
	}

	public String getStatus() {
		return this.status;
	}

	public String getTraitDbId() {
		return this.traitDbId;
	}

	public String getTraitName() {
		return this.traitName;
	}

	public String getXref() {
		return this.xref;
	}

	// Setter Methods

	public void setAttribute(final String attribute) {
		this.attribute = attribute;
	}

	public void setTraitClassAttribute(final String traitClassAttribute) {
		this.traitClassAttribute = traitClassAttribute;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setEntity(final String entity) {
		this.entity = entity;
	}

	public void setMainAbbreviation(final String mainAbbreviation) {
		this.mainAbbreviation = mainAbbreviation;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOntologyReference(final OntologyReferenceDTO ontologyReferenceObject) {
		this.ontologyReferenceObject = ontologyReferenceObject;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setTraitDbId(final String traitDbId) {
		this.traitDbId = traitDbId;
	}

	public void setTraitName(final String traitName) {
		this.traitName = traitName;
	}

	public void setXref(final String xref) {
		this.xref = xref;
	}

	public List<String> getSynonyms() {
		return this.synonyms;
	}

	public void setSynonyms(final List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public String getTraitClass() {
		return this.traitClass;
	}

	public void setTraitClass(final String traitClass) {
		this.traitClass = traitClass;
	}

}
