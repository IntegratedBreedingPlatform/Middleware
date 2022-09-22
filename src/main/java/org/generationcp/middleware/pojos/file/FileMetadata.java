package org.generationcp.middleware.pojos.file;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.pojos.AbstractEntity;
import org.generationcp.middleware.pojos.FileMetadataExternalReference;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.oms.CVTerm;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "file_metadata")
public class FileMetadata extends AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "file_id")
	private Integer fileId;

	@Column(name = "file_uuid")
	private String fileUUID;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "path")
	private String path;

	@Column(name = "copyright")
	private String copyright;

	@Column(name = "size")
	private Integer size;

	@Column(name = "image_height")
	private Integer imageHeight;

	@Column(name = "image_width")
	private Integer imageWidth;

	@Column(name = "image_location")
	private String imageLocation;

	@Column(name = "mime_type")
	private String mimeType;

	@Column(name = "file_timestamp")
	private Date fileTimestamp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nd_experiment_id")
	private ExperimentModel experimentModel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gid")
	private Germplasm germplasm;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nd_geolocation_id")
	private Geolocation geolocation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lotid")
	private Lot lot;

	@OneToMany
	@JoinTable(
		name = "file_metadata_cvterm",
		joinColumns = @JoinColumn(name = "file_metadata_id"),
		inverseJoinColumns = @JoinColumn(name = "cvterm_id"))
	private List<CVTerm> variables = new ArrayList<>();

	@OneToMany(mappedBy = "fileMetadata", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FileMetadataExternalReference> externalReferences = new ArrayList<>();

	public Integer getFileId() {
		return this.fileId;
	}

	public void setFileId(final Integer fileId) {
		this.fileId = fileId;
	}

	public String getFileUUID() {
		return this.fileUUID;
	}

	public void setFileUUID(final String fileUUID) {
		this.fileUUID = fileUUID;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public String getCopyright() {
		return this.copyright;
	}

	public void setCopyright(final String copyright) {
		this.copyright = copyright;
	}

	public Integer getSize() {
		return this.size;
	}

	public void setSize(final Integer imageSize) {
		this.size = imageSize;
	}

	public Integer getImageHeight() {
		return this.imageHeight;
	}

	public void setImageHeight(final Integer imageHeight) {
		this.imageHeight = imageHeight;
	}

	public Integer getImageWidth() {
		return this.imageWidth;
	}

	public void setImageWidth(final Integer imageWidth) {
		this.imageWidth = imageWidth;
	}

	public String getImageLocation() {
		return this.imageLocation;
	}

	public void setImageLocation(final String imageLocation) {
		this.imageLocation = imageLocation;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
	}

	public Date getFileTimestamp() {
		return this.fileTimestamp;
	}

	public void setFileTimestamp(final Date modifiedDate) {
		this.fileTimestamp = modifiedDate;
	}

	public ExperimentModel getExperimentModel() {
		return this.experimentModel;
	}

	public void setExperimentModel(final ExperimentModel experimentModel) {
		this.experimentModel = experimentModel;
	}

	public Germplasm getGermplasm() {
		return this.germplasm;
	}

	public void setGermplasm(final Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	public Geolocation getGeolocation() {
		return this.geolocation;
	}

	public void setGeolocation(final Geolocation geolocation) {
		this.geolocation = geolocation;
	}

	public Lot getLot() {
		return this.lot;
	}

	public void setLot(final Lot lot) {
		this.lot = lot;
	}

	public List<CVTerm> getVariables() {
		if (this.variables == null) {
			this.variables = new ArrayList<>();
		}
		return this.variables;
	}

	public List<FileMetadataExternalReference> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<FileMetadataExternalReference> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public void setVariables(final List<CVTerm> variables) {
		this.variables = variables;
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
