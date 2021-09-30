package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.service.api.BrapiView;

import java.util.*;

public class VariableDTO {

	@JsonView(BrapiView.BrapiV2.class)
	private Map<String, String> additionalInfo = new HashMap<>();

	@JsonView(BrapiView.BrapiV2.class)
	private String commonCropName;

	private List<String> contextOfUse = new ArrayList<>();

	@JsonView(BrapiView.BrapiV1_3.class)
	private String crop;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String date;

	private String defaultValue;
	private String documentationURL;

	@JsonView(BrapiView.BrapiV2.class)
	private List<ExternalReferenceDTO> externalReferences;

	private String growthStage;
	private String institution;
	private String language;

	@JsonView(BrapiView.BrapiV1_3.class)
	private String name;

	private String observationVariableDbId;
	private String observationVariableName;
	private String ontologyDbId;
	private String ontologyName;
	private OntologyReference ontologyReference;
	private Trait trait = new Trait();
	private Method method = new Method();
	private Scale scale = new Scale();
	private String scientist;
	private String status;
	private String submissionTimestamp;
	private List<String> synonyms = new ArrayList<>();

	@JsonView(BrapiView.BrapiV1_3.class)
	private String xref;

	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getCommonCropName() {
		return commonCropName;
	}

	public void setCommonCropName(String commonCropName) {
		this.commonCropName = commonCropName;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return externalReferences;
	}

	public void setExternalReferences(List<ExternalReferenceDTO> externalReferences) {
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

	public Method getMethod() {
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

	public String getOntologyDbId() {
		return this.ontologyDbId;
	}

	public String getOntologyName() {
		return this.ontologyName;
	}

	public OntologyReference getOntologyReference() {
		return this.ontologyReference;
	}

	public Scale getScale() {
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

	public Trait getTrait() {
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

	public void setMethod(final Method methodObject) {
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

	public void setOntologyDbId(final String ontologyDbId) {
		this.ontologyDbId = ontologyDbId;
	}

	public void setOntologyName(final String ontologyName) {
		this.ontologyName = ontologyName;
	}

	public void setOntologyReference(final OntologyReference ontologyReferenceObject) {
		this.ontologyReference = ontologyReferenceObject;
	}

	public void setScale(final Scale scaleObject) {
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

	public void setTrait(final Trait traitObject) {
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

	public class Trait {
		@JsonView(BrapiView.BrapiV2.class)
		private Map<String, String> additionalInfo = new HashMap<>();

		private final List<String> alternativeAbbreviations = new ArrayList<>();
		private String attribute;
		private String entity;

		@JsonView(BrapiView.BrapiV2.class)
		private List<ExternalReferenceDTO> externalReferences;
		private String mainAbbreviation;

		private String traitClass;
		@JsonView(BrapiView.BrapiV1_3.class)
		private String description;

		@JsonView(BrapiView.BrapiV2.class)
		private String traitDescription;


		private String name;
		private OntologyReference ontologyReferenceObject = new OntologyReference();
		private String status;

		private List<String> synonyms = new ArrayList<>();
		private String traitDbId;
		private String traitName;
		@JsonView(BrapiView.BrapiV1_3.class)
		private String xref;

		// Getter Methods

		public Map<String, String> getAdditionalInfo() {
			return additionalInfo;
		}

		public void setAdditionalInfo(Map<String, String> additionalInfo) {
			this.additionalInfo = additionalInfo;
		}

		public List<String> getAlternativeAbbreviations() {
			return alternativeAbbreviations;
		}

		public List<ExternalReferenceDTO> getExternalReferences() {
			return externalReferences;
		}

		public void setExternalReferences(List<ExternalReferenceDTO> externalReferences) {
			this.externalReferences = externalReferences;
		}

		public String getTraitDescription() {
			return traitDescription;
		}

		public void setTraitDescription(String traitDescription) {
			this.traitDescription = traitDescription;
		}

		public String getAttribute() {
			return this.attribute;
		}

		@JsonProperty("class")
		public String getTraitClass() {
			return this.traitClass;
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

		public OntologyReference getOntologyReference() {
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

		public void setTraitClass(final String traitClass) {
			this.traitClass = traitClass;
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

		public void setOntologyReference(final OntologyReference ontologyReferenceObject) {
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

	}


	public class Scale {

		public static final String NOMINAL = "Nominal";
		public static final String ORDINAL = "Ordinal";
		public static final String DATE = "Date";
		public static final String NUMERICAL = "Numerical";
		public static final String TEXT = "Text";

		@JsonView(BrapiView.BrapiV2.class)
		private Map<String, String> additionalInfo = new HashMap<>();

		private String dataType;
		private Integer decimalPlaces;

		@JsonView(BrapiView.BrapiV2.class)
		private List<ExternalReferenceDTO> externalReferences;

		@JsonView(BrapiView.BrapiV1_3.class)
		private String name;

		private OntologyReference ontologyReference = new OntologyReference();
		private String scaleDbId;
		private String scaleName;
		private ValidValues validValues = new ValidValues();

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

		public OntologyReference getOntologyReference() {
			return this.ontologyReference;
		}

		public String getScaleDbId() {
			return this.scaleDbId;
		}

		public String getScaleName() {
			return this.scaleName;
		}

		public ValidValues getValidValues() {
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

		public void setOntologyReference(final OntologyReference ontologyReferenceObject) {
			this.ontologyReference = ontologyReferenceObject;
		}

		public void setScaleDbId(final String scaleDbId) {
			this.scaleDbId = scaleDbId;
		}

		public void setScaleName(final String scaleName) {
			this.scaleName = scaleName;
		}

		public void setValidValues(final ValidValues validValuesObject) {
			this.validValues = validValuesObject;
		}

		public void setXref(final String xref) {
			this.xref = xref;
		}

		public Map<String, String> getAdditionalInfo() {
			return additionalInfo;
		}

		public void setAdditionalInfo(Map<String, String> additionalInfo) {
			this.additionalInfo = additionalInfo;
		}

		public List<ExternalReferenceDTO> getExternalReferences() {
			return externalReferences;
		}

		public void setExternalReferences(List<ExternalReferenceDTO> externalReferences) {
			this.externalReferences = externalReferences;
		}
	}


	public class ValidValues {

		private List<String> categories = new ArrayList<>();
		private Double max;
		private Double min;

		// Getter Methods

		public Double getMax() {
			return this.max;
		}

		public Double getMin() {
			return this.min;
		}

		// Setter Methods
		public void setMax(final Double max) {
			this.max = max;
		}

		public void setMin(final Double min) {
			this.min = min;
		}

		public List<String> getCategories() {
			return this.categories;
		}

		public void setCategories(final List<String> categories) {
			this.categories = categories;
		}
	}


	public class Method {
		@JsonView(BrapiView.BrapiV2.class)
		private Map<String, String> additionalInfo = new HashMap<>();

		@JsonView(BrapiView.BrapiV2.class)
		private String bibliographicalReference;

		@JsonView(BrapiView.BrapiV2.class)
		private List<ExternalReferenceDTO> externalReferences;
		private String methodClass;
		private String description;
		private String formula;
		private String methodDbId;
		private String methodName;

		@JsonView(BrapiView.BrapiV1_3.class)
		private String name;
		private OntologyReference ontologyReferenceObject = new OntologyReference();

		@JsonView(BrapiView.BrapiV1_3.class)
		private String reference;

		// Getter Methods

		@JsonProperty("class")
		public String getMethodClass() {
			return this.methodClass;
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

		public OntologyReference getOntologyReference() {
			return this.ontologyReferenceObject;
		}

		public String getReference() {
			return this.reference;
		}

		// Setter Methods

		public void setMethodClass(final String methodClass) {
			this.methodClass = methodClass;
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

		public void setOntologyReference(final OntologyReference ontologyReferenceObject) {
			this.ontologyReferenceObject = ontologyReferenceObject;
		}

		public void setReference(final String reference) {
			this.reference = reference;
		}

		public Map<String, String> getAdditionalInfo() {
			return additionalInfo;
		}

		public void setAdditionalInfo(Map<String, String> additionalInfo) {
			this.additionalInfo = additionalInfo;
		}

		public String getBibliographicalReference() {
			return bibliographicalReference;
		}

		public void setBibliographicalReference(String bibliographicalReference) {
			this.bibliographicalReference = bibliographicalReference;
		}

		public List<ExternalReferenceDTO> getExternalReferences() {
			return externalReferences;
		}

		public void setExternalReferences(List<ExternalReferenceDTO> externalReferences) {
			this.externalReferences = externalReferences;
		}

		public OntologyReference getOntologyReferenceObject() {
			return ontologyReferenceObject;
		}

		public void setOntologyReferenceObject(OntologyReference ontologyReferenceObject) {
			this.ontologyReferenceObject = ontologyReferenceObject;
		}
	}


	public class OntologyReference {

		private List<DocumentationLink> documentationLinks = Arrays.asList(new DocumentationLink());
		private String ontologyDbId;
		private String ontologyName;
		private String version;

		// Getter Methods

		public String getOntologyDbId() {
			return this.ontologyDbId;
		}

		public String getOntologyName() {
			return this.ontologyName;
		}

		public String getVersion() {
			return this.version;
		}

		public List<DocumentationLink> getDocumentationLinks() {
			return this.documentationLinks;
		}

		// Setter Methods
		public void setOntologyDbId(final String ontologyDbId) {
			this.ontologyDbId = ontologyDbId;
		}

		public void setOntologyName(final String ontologyName) {
			this.ontologyName = ontologyName;
		}

		public void setVersion(final String version) {
			this.version = version;
		}

		public void setDocumentationLinks(
			final List<DocumentationLink> documentationLinks) {
			this.documentationLinks = documentationLinks;
		}

		public class DocumentationLink {

			private String ontologyURL = "https://ontology.org";
			private String type = "WEBPAGE";

			@JsonView(BrapiView.BrapiV1_3.class)
			private String url = "https://cropontology.org";

			@JsonProperty("URL")
			public String getOntologyURL() {
				return ontologyURL;
			}

			public void setOntologyURL(final String ontologyURL) {
				this.ontologyURL = ontologyURL;
			}

			public String getType() {
				return this.type;
			}

			public void setType(final String type) {
				this.type = type;
			}

			public String getUrl() {
				return this.url;
			}

			public void setUrl(final String url) {
				this.url = url;
			}

		}
	}

}




