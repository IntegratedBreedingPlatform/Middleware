package org.generationcp.middleware.pojos.dms;

import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "study_type")
public class StudyType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "study_type_id")
	private Integer studyTypeId;

	@Basic(optional = false)
	@Column(name = "label")
	private String label;

	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	@Basic(optional = true)
	@Column(name = "cvterm_id")
	private Integer cvTermId;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "visible", columnDefinition = "TINYINT")
	private boolean visible;

	public StudyType(final String label, final String name, final Integer cvTermId, final boolean visible) {
		this.label = label;
		this.name = name;
		this.cvTermId = cvTermId;
		this.visible = visible;
	}

	public StudyType() {
	}

	public Integer getStudyTypeId() {
		return studyTypeId;
	}

	public void setStudyTypeId(final Integer studyTypeId) {
		this.studyTypeId = studyTypeId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getCvTermId() {
		return cvTermId;
	}

	public void setCvTermId(final Integer cvTermId) {
		this.cvTermId = cvTermId;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (!(o instanceof StudyType))
			return false;
		final StudyType studyType = (StudyType) o;
		return isVisible() == studyType.isVisible() && Objects.equals(getStudyTypeId(), studyType.getStudyTypeId()) && Objects
			.equals(getLabel(), studyType.getLabel()) && Objects.equals(getName(), studyType.getName()) && Objects
			.equals(getCvTermId(), studyType.getCvTermId());
	}

	@Override
	public int hashCode() {

		return Objects.hash(getStudyTypeId(), getLabel(), getName(), getCvTermId(), isVisible());
	}

	@Override
	public String toString() {
		return "StudyType{" + "studyTypeId=" + studyTypeId + ", label='" + label + '\'' + ", name='" + name + '\'' + ", cvTermId="
			+ cvTermId + ", visible=" + visible + '}';
	}
}
