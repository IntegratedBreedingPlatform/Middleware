package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataLink {

	private String dataFormat;
	private String description;
	private String fileFormat;
	private String name;
	private String provenance;
	private String scientificType;
	private String url;
	private String version;

	public DataLink() {

	}

	public String getDataFormat() {
		return this.dataFormat;
	}

	public void setDataFormat(final String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getFileFormat() {
		return this.fileFormat;
	}

	public void setFileFormat(final String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getProvenance() {
		return this.provenance;
	}

	public void setProvenance(final String provenance) {
		this.provenance = provenance;
	}

	public String getScientificType() {
		return this.scientificType;
	}

	public void setScientificType(final String scientificType) {
		this.scientificType = scientificType;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(final String version) {
		this.version = version;
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
