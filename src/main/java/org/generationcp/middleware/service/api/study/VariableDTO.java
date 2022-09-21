package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.service.api.BrapiView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoProperty
public class VariableDTO {

	public enum ContextOfUseEnum {
		PLOT,
		MEANS,
		SUMMARY
	}

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private Map<String, String> additionalInfo = new HashMap<>();

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private String commonCropName;

	@JsonIgnore
	private String definition;

	private List<String> contextOfUse = new ArrayList<>();

	@JsonView(BrapiView.BrapiV1_3.class)
	private String crop;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String date;

	private String defaultValue;
	private String documentationURL;

	@JsonView({BrapiView.BrapiV2.class, BrapiView.BrapiV2_1.class})
	private List<ExternalReferenceDTO> externalReferences;

	private String growthStage;
	private String institution;
	private String language;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String name;

	private String observationVariableDbId;
	private String observationVariableName;
	private OntologyReferenceDTO ontologyReference = new OntologyReferenceDTO();
	private TraitDTO trait = new TraitDTO();
	private MethodDTO method = new MethodDTO();
	private ScaleDTO scale = new ScaleDTO();
	private String scientist;
	private String status;
	private String submissionTimestamp;
	private List<String> synonyms = new ArrayList<>();

	@JsonView(BrapiView.BrapiV2_1.class)
	private List<String> studyDbIds = new ArrayList<>();

	@JsonView(BrapiView.BrapiV1_3.class)
	private String xref;

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getCommonCropName() {
		return this.commonCropName;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(final String definition) {
		this.definition = definition;
	}

	public void setCommonCropName(final String commonCropName) {
		this.commonCropName = commonCropName;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public List<String> getContextOfUse() {
		return this.contextOfUse;
	}

	public String getCrop() {
		return this.crop;
	}

	public String getDate() {
		return this.date;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public String getDocumentationURL() {
		return this.documentationURL;
	}

	public String getGrowthStage() {
		return this.growthStage;
	}

	public String getInstitution() {
		return this.institution;
	}

	public String getLanguage() {
		return this.language;
	}

	public MethodDTO getMethod() {
		return this.method;
	}

	public String getName() {
		return this.name;
	}

	public String getObservationVariableDbId() {
		return this.observationVariableDbId;
	}

	public String getObservationVariableName() {
		return this.observationVariableName;
	}

	public OntologyReferenceDTO getOntologyReference() {
		return this.ontologyReference;
	}

	public ScaleDTO getScale() {
		return this.scale;
	}

	public String getScientist() {
		return this.scientist;
	}

	public String getStatus() {
		return this.status;
	}

	public String getSubmissionTimestamp() {
		return this.submissionTimestamp;
	}

	public TraitDTO getTrait() {
		return this.trait;
	}

	public String getXref() {
		return this.xref;
	}

	// Setter Methods

	public void setContextOfUse(final List<String> contextOfUse) {
		this.contextOfUse = contextOfUse;
	}

	public void setCrop(final String crop) {
		this.crop = crop;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDocumentationURL(final String documentationURL) {
		this.documentationURL = documentationURL;
	}

	public void setGrowthStage(final String growthStage) {
		this.growthStage = growthStage;
	}

	public void setInstitution(final String institution) {
		this.institution = institution;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public void setMethod(final MethodDTO methodObject) {
		this.method = methodObject;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setObservationVariableDbId(final String observationVariableDbId) {
		this.observationVariableDbId = observationVariableDbId;
	}

	public void setObservationVariableName(final String observationVariableName) {
		this.observationVariableName = observationVariableName;
	}

	public void setOntologyReference(final OntologyReferenceDTO ontologyReferenceObject) {
		this.ontologyReference = ontologyReferenceObject;
	}

	public void setScale(final ScaleDTO scaleObject) {
		this.scale = scaleObject;
	}

	public void setScientist(final String scientist) {
		this.scientist = scientist;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setSubmissionTimestamp(final String submissionTimestamp) {
		this.submissionTimestamp = submissionTimestamp;
	}

	public void setTrait(final TraitDTO traitObject) {
		this.trait = traitObject;
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

	public List<String> getStudyDbIds() {
		return this.studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
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




