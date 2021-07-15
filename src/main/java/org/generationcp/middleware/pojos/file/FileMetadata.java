package org.generationcp.middleware.pojos.file;

import org.generationcp.middleware.pojos.AbstractEntity;
import org.generationcp.middleware.pojos.dms.ExperimentModel;

import javax.persistence.*;
import java.util.Date;

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
}
