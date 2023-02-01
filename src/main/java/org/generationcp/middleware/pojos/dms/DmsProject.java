/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.dms;

import org.generationcp.middleware.pojos.StudyExternalReference;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_project
 * <p>
 * A Study is captured using the Project table. Information stored at this level describes properties relevant for all field trials in a
 * Project (Study). Since it is important both that local breeders are free to use their nomenclature and that these local terms are mapped
 * to a central ontology, the properties table maps all terms to the Ontology at the project level.
 *
 * @author tippsgo
 */
@Entity()
@Table(name = "project", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "project")
public class DmsProject implements Serializable {

	private static final long serialVersionUID = 464731947805951726L;

	/**
	 * The project id of the SYSTEM root folder
	 */
	public static final Integer SYSTEM_FOLDER_ID = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "project_id")
	private Integer projectId;

	/**
	 * The name of the project.
	 */
	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	/**
	 * The description of the project.
	 */
	@Basic(optional = false)
	@Column(name = "description")
	private String description;

	@Column(name = "program_uuid")
	private String programUUID;

	/**
	 * List of Project Properties
	 */
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@BatchSize(size = 1000)
	private List<ProjectProperty> properties = new ArrayList<>();

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "deleted", columnDefinition = "TINYINT")
	private boolean deleted;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "locked", columnDefinition = "TINYINT")
	private boolean locked;

	@ManyToOne(targetEntity = StudyType.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "study_type_id")
	@NotFound(action = NotFoundAction.EXCEPTION)
	private StudyType studyType;

	@ManyToOne(targetEntity = DatasetType.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "dataset_type_id")
	private DatasetType datasetType;

	@ManyToOne(targetEntity = DmsProject.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_project_id")
	private DmsProject parent;

	@ManyToOne(targetEntity = DmsProject.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "study_id")
	private DmsProject study;

	@Column(name = "start_date")
	private String startDate;

	@Column(name = "end_date")
	private String endDate;

	@Column(name = "study_update")
	private String studyUpdate;

	@Column(name = "objective")
	private String objective;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "generation_level")
	private Integer generationLevel;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "study_id")
	private List<StudyExternalReference> externalReferences = new ArrayList<>();

	public DmsProject() {
		super();
	}

	public DmsProject(final Integer projectId) {
		this.projectId = projectId;
	}

	public DmsProject(
		final String name, final String description, final String programUUID, final List<ProjectProperty> properties,
		final boolean deleted,
		final boolean locked, final StudyType studyType, final String startDate, final String endDate, final String studyUpdate,
		final String objective, final String createdBy) {
		this.name = name;
		this.description = description;
		this.programUUID = programUUID;
		this.properties = properties;
		this.deleted = deleted;
		this.locked = locked;
		this.studyType = studyType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.studyUpdate = studyUpdate;
		this.objective = objective;
		this.createdBy = createdBy;
	}

	public Integer getProjectId() {
		return this.projectId;
	}

	public void setProjectId(final Integer projectId) {
		this.projectId = projectId;
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

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public List<ProjectProperty> getProperties() {
		return this.properties;
	}

	public void setProperties(final List<ProjectProperty> properties) {
		this.properties.clear();
		this.properties.addAll(properties);
	}

	public boolean getDeleted() {
		return this.deleted;
	}

	public void setDeleted(final boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean isLocked() {
		return this.locked;
	}

	public void setLocked(final Boolean locked) {
		this.locked = locked;
	}

	public StudyType getStudyType() {
		return this.studyType;
	}

	public void setStudyType(final StudyType studyType) {
		this.studyType = studyType;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public String getStudyUpdate() {
		return this.studyUpdate;
	}

	public void setStudyUpdate(final String studyUpdate) {
		this.studyUpdate = studyUpdate;
	}

	public String getObjective() {
		return this.objective;
	}

	public void setObjective(final String objective) {
		this.objective = objective;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(final String createdBy) {
		if (this.createdBy == null || this.createdBy.isEmpty()) {
			this.createdBy = createdBy;
		}
	}

	public DatasetType getDatasetType() {
		return this.datasetType;
	}

	public void setDatasetType(final DatasetType datasetType) {
		this.datasetType = datasetType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.projectId == null ? 0 : this.projectId.hashCode());
		return result;
	}

	public DmsProject getParent() {
		return this.parent;
	}

	public void setParent(final DmsProject parent) {
		this.parent = parent;
	}

	public DmsProject getStudy() {
		return this.study;
	}

	public void setStudy(final DmsProject study) {
		this.study = study;
	}

	public Integer getGenerationLevel() {
		return generationLevel;
	}

	public void setGenerationLevel(final Integer generationLevel) {
		this.generationLevel = generationLevel;
	}

	public List<StudyExternalReference> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<StudyExternalReference> externalReferences) {
		this.externalReferences = externalReferences;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final DmsProject other = (DmsProject) obj;
		if (this.projectId == null) {
			if (other.projectId != null) {
				return false;
			}
		} else if (!this.projectId.equals(other.projectId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(this.getEntityName() + " [projectId=");
		builder.append(this.projectId);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", description=");
		builder.append(this.description);
		builder.append(", startDate=");
		builder.append(this.startDate);
		builder.append(", endDate=");
		builder.append(this.endDate);
		builder.append(", deleted=");
		builder.append(this.deleted);
		builder.append(", study=");
		builder.append(this.study);
		builder.append("]");
		return builder.toString();
	}

	public String getEntityName() {
		return "DmsProject";
	}

	public void addProperty(final ProjectProperty property) {
		if (this.properties == null) {
			this.properties = new ArrayList<>();
		}
		this.properties.add(property);
	}


	public int getNextPropertyRank() {
		 return this.getProperties().stream()
			 .map(ProjectProperty::getRank)
			 .max(Integer::compareTo)
			 .orElse(0) + 1;
	}

	public boolean isFolder() {
		return this.studyType == null;
	}

}
