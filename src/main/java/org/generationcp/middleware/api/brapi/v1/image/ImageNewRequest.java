package org.generationcp.middleware.api.brapi.v1.image;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.service.api.BrapiView;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
public class ImageNewRequest {
	private Map<String, String> additionalInfo;
	private String copyright;
	private String description;
	private List<String> descriptiveOntologyTerms;
	private String imageFileName;
	private Integer imageFileSize;
	private Integer imageHeight;
	private Map<String, Object> imageLocation;

	@JsonView(BrapiView.BrapiV2.class)
	private List<ExternalReferenceDTO> externalReferences;

	/**
	 * human readable file name. Ignored for the moment
	 */
	private String imageName;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date imageTimeStamp;

	private String imageURL;
	private Integer imageWidth;
	private String mimeType;
	/**
	 * Note KSU Field (should) send observationUnitDbId + observationVariableDbId
	 * https://github.com/plantbreeding/API/issues/477
	 */
	private List<String> observationDbIds;
	private String observationUnitDbId;

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getCopyright() {
		return this.copyright;
	}

	public void setCopyright(final String copyright) {
		this.copyright = copyright;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public List<String> getDescriptiveOntologyTerms() {
		return this.descriptiveOntologyTerms;
	}

	public void setDescriptiveOntologyTerms(final List<String> descriptiveOntologyTerms) {
		this.descriptiveOntologyTerms = descriptiveOntologyTerms;
	}

	public String getImageFileName() {
		return this.imageFileName;
	}

	public void setImageFileName(final String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public Integer getImageFileSize() {
		return this.imageFileSize;
	}

	public void setImageFileSize(final Integer imageFileSize) {
		this.imageFileSize = imageFileSize;
	}

	public Integer getImageHeight() {
		return this.imageHeight;
	}

	public void setImageHeight(final Integer imageHeight) {
		this.imageHeight = imageHeight;
	}

	public Map<String, Object> getImageLocation() {
		return this.imageLocation;
	}

	public void setImageLocation(final Map<String, Object> imageLocation) {
		this.imageLocation = imageLocation;
	}

	public String getImageName() {
		return this.imageName;
	}

	public void setImageName(final String imageName) {
		this.imageName = imageName;
	}

	public Date getImageTimeStamp() {
		return this.imageTimeStamp;
	}

	public void setImageTimeStamp(final Date imageTimeStamp) {
		this.imageTimeStamp = imageTimeStamp;
	}

	public String getImageURL() {
		return this.imageURL;
	}

	public void setImageURL(final String imageURL) {
		this.imageURL = imageURL;
	}

	public Integer getImageWidth() {
		return this.imageWidth;
	}

	public void setImageWidth(final Integer imageWidth) {
		this.imageWidth = imageWidth;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
	}

	public List<String> getObservationDbIds() {
		return this.observationDbIds;
	}

	public void setObservationDbIds(final List<String> observationDbIds) {
		this.observationDbIds = observationDbIds;
	}

	public String getObservationUnitDbId() {
		return this.observationUnitDbId;
	}

	public void setObservationUnitDbId(final String observationUnitDbId) {
		this.observationUnitDbId = observationUnitDbId;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
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
