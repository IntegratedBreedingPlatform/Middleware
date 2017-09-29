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

package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProjectPropertySaver {

	protected static final String PROJECT_PROPERTY_ID = "projectPropertyId";

	private final Saver daoFactory;

	public ProjectPropertySaver(final HibernateSessionProvider sessionProviderForLocal) {
		this.daoFactory = new Saver(sessionProviderForLocal);
	}

	public ProjectPropertySaver(final Saver saver) {
		this.daoFactory = saver;
	}

	public List<ProjectProperty> create(final DmsProject project, final VariableTypeList variableTypeList) {
		final List<ProjectProperty> properties = new ArrayList<>();
		final List<DMSVariableType> variableTypes = variableTypeList != null ? variableTypeList.getVariableTypes() : null;

		if (variableTypes != null && !variableTypes.isEmpty()) {
			for (final DMSVariableType variableType : variableTypes) {
				final List<ProjectProperty> list = this.createVariableProperties(project, variableType);
				properties.addAll(list);
			}
		}
		return properties;
	}

	public void saveProjectProperties(final DmsProject project, final VariableTypeList variableTypeList) {
		final List<ProjectProperty> properties = this.create(project, variableTypeList);
		final ProjectPropertyDao projectPropertyDao = this.daoFactory.getProjectPropertyDao();
		for (final ProjectProperty property : properties) {
			property.setProject(project);
			projectPropertyDao.save(property);
		}

		project.setProperties(properties);
	}

	private List<ProjectProperty> createVariableProperties(final DmsProject project, final DMSVariableType variableType) {

	  	// Setting property, scale and method to standard variable
	  	final StandardVariableSummary standardVariableSummary =
			  this.daoFactory.getStandardVariableBuilder().getStandardVariableSummary(variableType.getStandardVariable().getId());

	  	variableType.getStandardVariable().setProperty(new Term(0, standardVariableSummary.getProperty().getName(), ""));
	  	variableType.getStandardVariable().setScale(new Term(0, standardVariableSummary.getScale().getName(), ""));
	  	variableType.getStandardVariable().setMethod(new Term(0, standardVariableSummary.getMethod().getName(), ""));

	  	variableType.setVariableTypeIfNull();

		VariableType variableTypeEnum = variableType.getVariableType();

		if(variableTypeEnum == null) {
			throw new RuntimeException("Variable do not have a valid variable type.");
		}

		int variableTypeId;

		// This makes sure that selection values are actually saved as selections in the projectprop tables. Note roles cannot be used for
		// this as both selections and traits map to roles. Thus if the role has evaluated to a Trait varible type and the DMSVariableType
		// is not null use the DMSVariableType as it could be a selection.
		variableTypeId = variableTypeEnum.getId();

		final List<ProjectProperty> properties = new ArrayList<>();
		
		properties.add(new ProjectProperty(project, variableTypeId, variableType.getLocalName(), variableType.getRank()));
		properties.add(new ProjectProperty(project, TermId.VARIABLE_DESCRIPTION.getId(), variableType.getLocalDescription(), variableType
				.getRank()));
		properties.add(new ProjectProperty(project, TermId.STANDARD_VARIABLE.getId(), String.valueOf(variableType.getId()), variableType
				.getRank()));

		if (variableType.getTreatmentLabel() != null && !"".equals(variableType.getTreatmentLabel())) {
			properties.add(new ProjectProperty(project, TermId.MULTIFACTORIAL_INFO.getId(), variableType.getTreatmentLabel(), variableType
					.getRank()));
		}

		return properties;
	}

	public void saveProjectPropValues(final int projectId, final VariableList variableList) {
		if (variableList != null && variableList.getVariables() != null && !variableList.getVariables().isEmpty()) {
			for (Variable variable : variableList.getVariables()) {

				DMSVariableType dmsVariableType = variable.getVariableType();
				dmsVariableType.setVariableTypeIfNull();

				VariableType variableTypeEnum = dmsVariableType.getVariableType();

				if (variableTypeEnum == org.generationcp.middleware.domain.ontology.VariableType.STUDY_DETAIL) {
					final ProjectProperty property = new ProjectProperty();
					property.setTypeId(variable.getVariableType().getStandardVariable().getId());
					property.setValue(variable.getValue());
					property.setRank(variable.getVariableType().getRank());
					property.setProject(this.daoFactory.getDmsProjectDao().getById(projectId));
					this.daoFactory.getProjectPropertyDao().save(property);
				}
			}
		}
	}

	/***
	 * Saving project property from DMSVariableType. Setting VariableType from Role when DMSVariableType does not have value fro Variable
	 * Type. DMSVariableType will hold two similar values. Role and VariableType. We should consider using VariableType first and as a
	 * fallback we should use Role. Next iterations should remove Role from everywhere. Currently we have taken care of setting valid
	 * variable type from EditNurseryController for Variates (Trait and Selection Methods)
	 *
	 * @param project DMSProject
	 * @param objDMSVariableType DMSVariableType
	 * @throws MiddlewareQueryException
	 */
	public void saveVariableType(final DmsProject project, final DMSVariableType objDMSVariableType) {

	  // Setting property, scale and method to standard variable
	  	final StandardVariableSummary standardVariableSummary =
			  this.daoFactory.getStandardVariableBuilder().getStandardVariableSummary(objDMSVariableType.getStandardVariable().getId());

	  	objDMSVariableType.getStandardVariable().setProperty(new Term(0, standardVariableSummary.getProperty().getName(), ""));
	  	objDMSVariableType.getStandardVariable().setScale(new Term(0, standardVariableSummary.getScale().getName(), ""));
	  	objDMSVariableType.getStandardVariable().setMethod(new Term(0, standardVariableSummary.getMethod().getName(), ""));

		objDMSVariableType.setVariableTypeIfNull();
		final org.generationcp.middleware.domain.ontology.VariableType variableTypeEnum = objDMSVariableType.getVariableType();
		this.saveProjectProperty(project, variableTypeEnum.getId(), objDMSVariableType.getLocalName(), objDMSVariableType.getRank());
		this.saveProjectProperty(project, TermId.VARIABLE_DESCRIPTION.getId(), objDMSVariableType.getLocalDescription(),
			objDMSVariableType.getRank());
		this.saveProjectProperty(project, TermId.STANDARD_VARIABLE.getId(),
			Integer.toString(objDMSVariableType.getStandardVariable().getId()), objDMSVariableType.getRank());
		if (objDMSVariableType.getTreatmentLabel() != null && !objDMSVariableType.getTreatmentLabel().isEmpty()) {
			this.saveProjectProperty(project, TermId.MULTIFACTORIAL_INFO.getId(), objDMSVariableType.getTreatmentLabel(),
				objDMSVariableType.getRank());
		}
	}

	private void saveProjectProperty(final DmsProject project, final int typeId, final String value, final int rank) {
		final ProjectProperty property = new ProjectProperty();
		property.setTypeId(typeId);
		property.setValue(value);
		property.setRank(rank);
		property.setProject(project);
		this.daoFactory.getProjectPropertyDao().save(property);
		project.addProperty(property);
	}

	public void createProjectPropertyIfNecessary(final DmsProject project, final TermId termId, final PhenotypicType role) {
		final ProjectProperty property = this.daoFactory.getProjectPropertyDao().getByStandardVariableId(project, termId.getId());
		if (property == null) {
			final int rank = this.daoFactory.getProjectPropertyDao().getNextRank(project.getProjectId());
			final StandardVariable stdvar = new StandardVariable();
			stdvar.setId(termId.getId());
			stdvar.setPhenotypicType(role);
			final CVTerm cvTerm = this.daoFactory.getCvTermDao().getById(termId.getId());
			String localVariableName = termId.toString();
			String localVariableDescription = termId.toString();
			if (cvTerm != null) {
				localVariableName = cvTerm.getName();
				localVariableDescription = cvTerm.getDefinition();
			}

			final DMSVariableType variableType = new DMSVariableType(localVariableName, localVariableDescription, stdvar, rank);
			this.saveVariableType(project, variableType);
		}
	}

	public void saveProjectProperties(final DmsProject study, final DmsProject trialDataset, final DmsProject measurementDataset,
			final List<MeasurementVariable> variables, final boolean isConstant) {

		if (variables != null) {

			int rank = this.getNextRank(study);
			final Set<Integer> geoIds = this.daoFactory.getGeolocationDao().getLocationIds(study.getProjectId());
			final Geolocation geolocation = this.daoFactory.getGeolocationDao().getById(geoIds.iterator().next());
			Hibernate.initialize(geolocation.getProperties());

			for (final MeasurementVariable variable : variables) {
				final String operation = variable.getOperation().toString();
				switch (operation) {

					case "DELETE":
						this.deleteVariable(study, trialDataset, measurementDataset, variable.getRole(), variable.getTermId(), geolocation);
						break;
					case "ADD":
						this.insertVariable(study, trialDataset, measurementDataset, variable, rank, isConstant, geolocation);
						rank++;
						break;
					case "UPDATE":
						this.updateVariable(study, trialDataset, measurementDataset, variable, isConstant, geolocation);
						break;
					default:
						break;
				}
			}
		}
	}

	private int getNextRank(final DmsProject project) {
		int nextRank = 1;
		if (project.getProperties() != null) {
			for (final ProjectProperty property : project.getProperties()) {
				if (property.getRank() >= nextRank) {
					nextRank = property.getRank() + 1;
				}
			}
		}
		return nextRank;
	}

	private boolean isInGeolocation(final int termId) {
		return TermId.TRIAL_INSTANCE_FACTOR.getId() == termId || TermId.LATITUDE.getId() == termId || TermId.LONGITUDE.getId() == termId
				|| TermId.GEODETIC_DATUM.getId() == termId || TermId.ALTITUDE.getId() == termId;
	}

	private void insertVariable(final DmsProject project, final DmsProject trialDataset, final DmsProject measurementDataset,
			final MeasurementVariable variable, final int rank, final boolean isConstant, final Geolocation geolocation) {

		if (PhenotypicType.TRIAL_ENVIRONMENT == variable.getRole()) {
			final int datasetRank = this.getNextRank(trialDataset);
			final int measurementRank = this.getNextRank(measurementDataset);

			this.insertVariable(trialDataset, variable, datasetRank);

			// GCP-9959
			if (variable.getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
				this.insertVariable(measurementDataset, variable, measurementRank);
			}
			if (this.isInGeolocation(variable.getTermId())) {
				this.daoFactory.getGeolocationSaver().setGeolocation(geolocation, variable.getTermId(), variable.getValue());
				this.daoFactory.getGeolocationDao().saveOrUpdate(geolocation);
			} else {
				this.daoFactory.getGeolocationPropertySaver().saveOrUpdate(geolocation, variable.getTermId(), variable.getValue());
			}

		} else if (PhenotypicType.VARIATE == variable.getRole()) {

			if (isConstant) {
				if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel())) {
					// a trial constant
					final int datasetRank = this.getNextRank(trialDataset);
					this.insertVariable(trialDataset, variable, datasetRank);
					this.daoFactory.getPhenotypeSaver().saveOrUpdatePhenotypeValue(trialDataset.getProjectId(), variable.getTermId(),
							variable.getValue(), variable.getDataTypeId());
				} else {
					// a study constant
					this.insertVariable(project, variable, rank);
					this.daoFactory.getPhenotypeSaver().saveOrUpdatePhenotypeValue(project.getProjectId(), variable.getTermId(),
							variable.getValue(), variable.getDataTypeId());
				}
			} else {
				final int measurementRank = this.getNextRank(measurementDataset);
				this.insertVariable(measurementDataset, variable, measurementRank);
			}
		} else {
			// study
			this.insertVariable(project, variable, rank);
			final VariableList variableList = new VariableList();
			variableList.add(new Variable(this.createVariableType(variable, rank), variable.getValue()));
			this.saveProjectPropValues(project.getProjectId(), variableList);
		}
	}

	private void insertVariable(final DmsProject project, final MeasurementVariable variable, final int rank) {
		if (project.getProperties() == null) {
			project.setProperties(new ArrayList<ProjectProperty>());
		}
		this.saveVariableType(project, this.createVariableType(variable, rank));
	}

	protected DMSVariableType createVariableType(final MeasurementVariable variable, final int rank) {
		final DMSVariableType varType = new DMSVariableType();
		final StandardVariable stdVariable = new StandardVariable();

		varType.setRole(variable.getRole());
		varType.setVariableType(variable.getVariableType());
		varType.setLocalName(variable.getName());
		varType.setLocalDescription(variable.getDescription());
		varType.setRank(rank);

		stdVariable.setId(variable.getTermId());
		stdVariable.setPhenotypicType(variable.getRole());
		stdVariable.setMethod(new Term(0, variable.getMethod(), ""));
		stdVariable.setProperty(new Term(0, variable.getProperty(), ""));
		stdVariable.setScale(new Term(0, variable.getScale(), ""));

		varType.setStandardVariable(stdVariable);

		if (variable.getTreatmentLabel() != null && !variable.getTreatmentLabel().isEmpty()) {
			varType.setTreatmentLabel(variable.getTreatmentLabel());
		}
		return varType;
	}

	private void updateVariable(final DmsProject project, final DmsProject trialDataset, final DmsProject measurementDataset,
			final MeasurementVariable variable, final boolean isConstant, final Geolocation geolocation) {
		if (TermId.TRIAL_INSTANCE_FACTOR.getId() != variable.getTermId()) {

			if (PhenotypicType.TRIAL_ENVIRONMENT == variable.getRole()) {
				this.updateVariable(project, variable);
				this.updateVariable(trialDataset, variable);
				this.updateVariable(measurementDataset, variable);

				if (this.isInGeolocation(variable.getTermId())) {
					this.daoFactory.getGeolocationSaver().setGeolocation(geolocation, variable.getTermId(), variable.getValue());
					this.daoFactory.getGeolocationDao().saveOrUpdate(geolocation);
				} else {
					this.daoFactory.getGeolocationPropertySaver().saveOrUpdate(geolocation, variable.getTermId(), variable.getValue());
				}

			} else if (PhenotypicType.VARIATE == variable.getRole()) {

				if (isConstant) {
					if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel())) {
						// a trial constant
						this.updateVariable(trialDataset, variable);
						this.updateVariable(measurementDataset, variable);
						this.daoFactory.getPhenotypeSaver()
							.saveOrUpdatePhenotypeValue(trialDataset.getProjectId(), variable.getTermId(), variable.getValue(),
								variable.getDataTypeId());
					} else {
						// a study constant
						this.updateVariable(project, variable);
						this.daoFactory.getPhenotypeSaver()
							.saveOrUpdatePhenotypeValue(project.getProjectId(), variable.getTermId(), variable.getValue(),
								variable.getDataTypeId());
					}
				} else {
					this.updateVariable(measurementDataset, variable);
				}
			} else {
				// study
				this.updateVariable(project, variable);
				if (variable.getTermId() == TermId.STUDY_NAME.getId()) {
					project.setName(variable.getValue());
					this.daoFactory.getDmsProjectDao().merge(project);
				} else if (variable.getTermId() == TermId.STUDY_TITLE.getId()) {
					project.setDescription(variable.getValue());
					this.daoFactory.getDmsProjectDao().merge(project);
				}
			}
		}
	}

	private void updateVariable(final DmsProject project, final MeasurementVariable variable) {
		if (project.getProperties() != null) {
			final int rank = this.getRank(project, variable.getTermId());
			for (final ProjectProperty property : project.getProperties()) {
				if (rank == property.getRank()) {
					if (property.getTypeId().intValue() == TermId.VARIABLE_DESCRIPTION.getId()) {
						property.setValue(variable.getDescription());
					} else if (property.getTypeId().intValue() == variable.getTermId()) {
						property.setValue(variable.getValue());
					} else if (VariableType.getById(property.getTypeId().intValue()) != null) {
						property.setValue(variable.getName());
					}
					this.daoFactory.getProjectPropertyDao().update(property);
				}
			}
		}
	}

	private int getRank(final DmsProject project, final int termId) {
		int rank = -1;
		if (project.getProperties() != null) {
			for (final ProjectProperty property : project.getProperties()) {
				if (property.getTypeId().intValue() == TermId.STANDARD_VARIABLE.getId()
						&& property.getValue().equals(String.valueOf(termId))) {
					rank = property.getRank();
					break;
				}
			}
		}
		return rank;
	}

	private void deleteVariable(final DmsProject project, final DmsProject trialDataset, final DmsProject measurementDataset,
			final PhenotypicType role, final int termId, final Geolocation geolocation) {

		if (PhenotypicType.TRIAL_ENVIRONMENT == role) {
			this.deleteVariable(trialDataset, termId);
			this.deleteVariable(measurementDataset, termId);

			if (this.isInGeolocation(termId)) {
				this.daoFactory.getGeolocationSaver().setGeolocation(geolocation, termId, null);
				this.daoFactory.getGeolocationDao().saveOrUpdate(geolocation);
			} else {
				this.daoFactory.getGeolocationPropertyDao().deleteGeolocationPropertyValueInProject(project.getProjectId(), termId);
			}

		} else if (PhenotypicType.VARIATE == role) {
			// for constants
			this.deleteVariable(project, termId);
			this.deleteVariable(trialDataset, termId);

			// for variates
			this.deleteVariable(measurementDataset, termId);
			// remove phoenotype value
			final List<Integer> ids = Arrays.asList(project.getProjectId(), trialDataset.getProjectId(), measurementDataset.getProjectId());
			this.daoFactory.getPhenotypeDao().deletePhenotypesInProjectByTerm(ids, termId);
		}else{
			// study
			this.deleteVariable(project, termId);
		}
	}

	private void deleteVariable(final DmsProject project, final int termId) {
		final int rank = this.getRank(project, termId);
		if (project.getProperties() != null && !project.getProperties().isEmpty()) {
			for (final Iterator<ProjectProperty> iterator = project.getProperties().iterator(); iterator.hasNext();) {
				final ProjectProperty property = iterator.next();
				if (rank == property.getRank()) {
					this.daoFactory.getProjectPropertyDao().makeTransient(property);
					iterator.remove();
				}
			}
		}
	}

	private void deleteVariableForFactors(final DmsProject project, final MeasurementVariable variable) {
		this.deleteVariable(project, variable.getTermId());

		if (variable.getRole() == PhenotypicType.TRIAL_DESIGN) {
			this.daoFactory.getExperimentPropertyDao().deleteExperimentPropInProjectByTermId(project.getProjectId(), variable.getTermId());
		} else if (variable.getRole() == PhenotypicType.GERMPLASM) {
			this.daoFactory.getStockPropertyDao().deleteStockPropInProjectByTermId(project.getProjectId(), variable.getTermId());
		}
	}

	public void saveFactors(final DmsProject measurementDataset, final List<MeasurementVariable> variables) {
		if (variables != null && !variables.isEmpty()) {
			for (final MeasurementVariable variable : variables) {
				switch (variable.getOperation().toString()) {
					case "ADD":
						final int rank = this.getNextRank(measurementDataset);
						this.insertVariable(measurementDataset, variable, rank);
						break;

					case "DELETE":
						this.deleteVariableForFactors(measurementDataset, variable);
						break;
					// update operation is not allowed with factors

					default:
						break;
				}
			}
		}
	}

	public void updateVariablesRanking(final int datasetId, final List<Integer> variableIds) {
		int rank = this.daoFactory.getProjectPropertyDao().getNextRank(datasetId);
		final Map<Integer, List<Integer>> projectPropIDMap =
				this.daoFactory.getProjectPropertyDao().getProjectPropertyIDsPerVariableId(datasetId);
		rank = this.updateVariableRank(variableIds, rank, projectPropIDMap);

		// if any factors were added but not included in list of variables, update their ranks also so they come last
		final List<Integer> storedInIds = new ArrayList<>();
		storedInIds.addAll(PhenotypicType.GERMPLASM.getTypeStorages());
		storedInIds.addAll(PhenotypicType.TRIAL_DESIGN.getTypeStorages());
		storedInIds.addAll(PhenotypicType.VARIATE.getTypeStorages());

		final List<Integer> germplasmPlotVariateIds =
				this.daoFactory.getProjectPropertyDao().getDatasetVariableIdsForGivenStoredInIds(datasetId, storedInIds, variableIds);
		this.updateVariableRank(germplasmPlotVariateIds, rank, projectPropIDMap);
	}

	// Iterate and update rank, exclude deleted variables
	private int updateVariableRank(final List<Integer> variableIds, final int startRank, final Map<Integer, List<Integer>> projectPropIDMap) {
		int rank = startRank;
		for (final Integer variableId : variableIds) {
			final List<Integer> projectPropIds = projectPropIDMap.get(variableId);
			if (projectPropIds != null) {
				this.daoFactory.getProjectPropertyDao().updateRank(projectPropIds, rank);
				rank++;
			}
		}
		return rank;
	}

}
