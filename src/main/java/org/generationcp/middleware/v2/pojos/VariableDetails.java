package org.generationcp.middleware.v2.pojos;


public abstract class VariableDetails {

	private Integer id;
	private String name;
	private String description;
	private String property;
	private String method;
	private String scale;
	private String dataType;
	private Integer studyId;
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}
	public Integer getStudyId() {
		return studyId;
	}
	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableDetails other = (VariableDetails) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "VariableDetails [id=" + id + ", name=" + name
				+ ", description=" + description + ", property=" + property
				+ ", method=" + method + ", scale=" + scale + ", dataType="
				+ dataType + ", studyId=" + studyId + "]";
	}

}
