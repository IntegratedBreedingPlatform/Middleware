package org.generationcp.middleware.domain.study;

import java.io.Serializable;
import java.util.Objects;

public class StudyTypeDto implements Serializable, Comparable<StudyTypeDto> {

	public static final String TRIAL_NAME="T";
	public static final String NURSERY_NAME="N";

	public static final String TRIAL_LABEL="Trial";
	public static final String NURSERY_LABEL="Nursery";

	private Integer id;
	private String label;
	private String name;
	private Integer cvTermId;
	private boolean visible;

	public StudyTypeDto(final Integer id, final String label, final String name, final Integer cvTermId, final boolean visible) {
		this.id = id;
		this.label = label;
		this.name = name;
		this.cvTermId = cvTermId;
		this.visible = visible;
	}

	public StudyTypeDto(final Integer id, final String label, final String name){
			this.setId(id);
			this.setLabel(label);
			this.setName(name);
	}

	public StudyTypeDto(final String name) {
		super();
		this.name = name;
	}

	public StudyTypeDto() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
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
		if (!(o instanceof StudyTypeDto))
			return false;
		final StudyTypeDto that = (StudyTypeDto) o;
		return Objects.equals(getLabel(), that.getLabel());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	@Override
	public String toString() {
		return this.getLabel();
	}

	@Override
	public int compareTo(@SuppressWarnings("NullableProblems") final StudyTypeDto o) {
		final int compareId = o.getId();
		return this.getId().compareTo(compareId);
	}

	//TODO try to improve this
	public static StudyTypeDto getTrialDto() {
		return new StudyTypeDto(6, StudyTypeDto.TRIAL_LABEL, StudyTypeDto.TRIAL_NAME, 10010, true);
	}

	//TODO try to improve this
	public static StudyTypeDto getNurseryDto() {
		return new StudyTypeDto(1, StudyTypeDto.NURSERY_LABEL, StudyTypeDto.NURSERY_NAME, 10000, true);
	}
	
}
