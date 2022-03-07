package org.generationcp.middleware.api.brapi.v2.attribute;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.service.api.study.MethodDTO;
import org.generationcp.middleware.service.api.study.OntologyReferenceDTO;
import org.generationcp.middleware.service.api.study.ScaleDTO;
import org.generationcp.middleware.service.api.study.TraitDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeDTO {
	private Map<String, String> additionalInfo = new HashMap<>();
	private String attributeCategory;
	private String attributeDbId;
	private String attributeDescription;
	private String attributeName;
	private String commonCropName;
	private List<String> contextOfUse = new ArrayList<>();
	private String defaultValue;
	private String documentationURL;
	private List<ExternalReferenceDTO> externalReferences;
	private String growthStage;
	private String institution;
	private String language;
	private MethodDTO method = new MethodDTO();
	private OntologyReferenceDTO ontologyReference = new OntologyReferenceDTO();
	private ScaleDTO scale = new ScaleDTO();
	private String scientist;
	private String status;
	private String submissionTimestamp;
	private List<String> synonyms = new ArrayList<>();
	private TraitDTO trait = new TraitDTO();

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getAttributeCategory() {
		return this.attributeCategory;
	}

	public void setAttributeCategory(final String attributeCategory) {
		this.attributeCategory = attributeCategory;
	}

	public String getAttributeDbId() {
		return this.attributeDbId;
	}

	public void setAttributeDbId(final String attributeDbId) {
		this.attributeDbId = attributeDbId;
	}

	public String getAttributeDescription() {
		return this.attributeDescription;
	}

	public void setAttributeDescription(final String attributeDescription) {
		this.attributeDescription = attributeDescription;
	}

	public String getAttributeName() {
		return this.attributeName;
	}

	public void setAttributeName(final String attributeName) {
		this.attributeName = attributeName;
	}

	public String getCommonCropName() {
		return this.commonCropName;
	}

	public void setCommonCropName(final String commonCropName) {
		this.commonCropName = commonCropName;
	}

	public List<String> getContextOfUse() {
		return this.contextOfUse;
	}

	public void setContextOfUse(final List<String> contextOfUse) {
		this.contextOfUse = contextOfUse;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDocumentationURL() {
		return this.documentationURL;
	}

	public void setDocumentationURL(final String documentationURL) {
		this.documentationURL = documentationURL;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public String getGrowthStage() {
		return this.growthStage;
	}

	public void setGrowthStage(final String growthStage) {
		this.growthStage = growthStage;
	}

	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(final String institution) {
		this.institution = institution;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public MethodDTO getMethod() {
		return this.method;
	}

	public void setMethod(final MethodDTO method) {
		this.method = method;
	}

	public OntologyReferenceDTO getOntologyReference() {
		return this.ontologyReference;
	}

	public void setOntologyReference(final OntologyReferenceDTO ontologyReference) {
		this.ontologyReference = ontologyReference;
	}

	public ScaleDTO getScale() {
		return this.scale;
	}

	public void setScale(final ScaleDTO scale) {
		this.scale = scale;
	}

	public String getScientist() {
		return this.scientist;
	}

	public void setScientist(final String scientist) {
		this.scientist = scientist;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getSubmissionTimestamp() {
		return this.submissionTimestamp;
	}

	public void setSubmissionTimestamp(final String submissionTimestamp) {
		this.submissionTimestamp = submissionTimestamp;
	}

	public List<String> getSynonyms() {
		return this.synonyms;
	}

	public void setSynonyms(final List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public TraitDTO getTrait() {
		return this.trait;
	}

	public void setTrait(final TraitDTO trait) {
		this.trait = trait;
	}

}

