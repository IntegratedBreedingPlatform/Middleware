package org.generationcp.middleware.api.brapi.v2.trial;

import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.pojos.workbench.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrialImportRequestDTO {

	private boolean active;
	private Map<String, String> additionalInfo = new HashMap<>();
	private String commonCropName;
	private List<Contact> contacts = new ArrayList<>();
	private List<DatasetAuthorship> datasetAuthorships = new ArrayList<>();
	private String documentationURL = StringUtils.EMPTY;
	private List<ExternalReferenceDTO> externalReferences;
	private String endDate;
	private String startDate;
	private String programDbId;
	private String programName;
	private List<Publication> publications = new ArrayList<>();
	private String trialDescription;
	private String trialName;
	private String trialPUI;

	public TrialImportRequestDTO() {

	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getCommonCropName() {
		return this.commonCropName;
	}

	public void setCommonCropName(final String commonCropName) {
		this.commonCropName = commonCropName;
	}

	public List<Contact> getContacts() {
		return this.contacts;
	}

	public void setContacts(final List<Contact> contacts) {
		this.contacts = contacts;
	}

	public List<DatasetAuthorship> getDatasetAuthorships() {
		return this.datasetAuthorships;
	}

	public void setDatasetAuthorships(final List<DatasetAuthorship> datasetAuthorships) {
		this.datasetAuthorships = datasetAuthorships;
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

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public String getProgramDbId() {
		return this.programDbId;
	}

	public void setProgramDbId(final String programDbId) {
		this.programDbId = programDbId;
	}

	public String getProgramName() {
		return this.programName;
	}

	public void setProgramName(final String programName) {
		this.programName = programName;
	}

	public List<Publication> getPublications() {
		return this.publications;
	}

	public void setPublications(final List<Publication> publications) {
		this.publications = publications;
	}

	public String getTrialDescription() {
		return this.trialDescription;
	}

	public void setTrialDescription(final String trialDescription) {
		this.trialDescription = trialDescription;
	}

	public String getTrialName() {
		return this.trialName;
	}

	public void setTrialName(final String trialName) {
		this.trialName = trialName;
	}

	public String getTrialPUI() {
		return this.trialPUI;
	}

	public void setTrialPUI(final String trialPUI) {
		this.trialPUI = trialPUI;
	}
}
