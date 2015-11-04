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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hibernate.Hibernate;

public class ProjectPropertySaver {

	protected static final String PROJECT_PROPERTY_ID = "projectPropertyId";

	private Saver daoFactory;

	public ProjectPropertySaver(HibernateSessionProvider sessionProviderForLocal) {
		this.daoFactory = new Saver(sessionProviderForLocal);
	}

	public ProjectPropertySaver(final Saver saver) {
		this.daoFactory = saver;
	}

	public List<ProjectProperty> create(DmsProject project, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		List<DMSVariableType> variableTypes = variableTypeList != null ? variableTypeList.getVariableTypes() : null;

		if (variableTypes != null && !variableTypes.isEmpty()) {
			for (DMSVariableType variableType : variableTypes) {
				List<ProjectProperty> list = this.createVariableProperties(project, variableType);
				properties.addAll(list);
			}
		}
		return properties;
	}

	public void saveProjectProperties(DmsProject project, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		List<ProjectProperty> properties = this.create(project, variableTypeList);
		ProjectPropertyDao projectPropertyDao = this.daoFactory.getProjectPropertyDao();
		for (ProjectProperty property : properties) {
			property.setProject(project);
			projectPropertyDao.save(property);
		}

		project.setProperties(properties);
	}

	private List<ProjectProperty> createVariableProperties(DmsProject project, DMSVariableType variableType)
			throws MiddlewareQueryException {
		List<ProjectProperty> properties = new ArrayList<ProjectProperty>();
		org.generationcp.middleware.domain.ontology.VariableType variableTypeEnum =
				this.daoFactory.getStandardVariableBuilder().mapPhenotypicTypeToDefaultVariableType(variableType.getRole());
		int variableTypeId = variableTypeEnum.getId();
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

	public void saveProjectPropValues(int projectId, VariableList variableList) throws MiddlewareQueryException {
		if (variableList != null && variableList.getVariables() != null && !variableList.getVariables().isEmpty()) {
			for (Variable variable : variableList.getVariables()) {
				org.generationcp.middleware.domain.ontology.VariableType variableTypeEnum =
						this.daoFactory.getStandardVariableBuilder().mapPhenotypicTypeToDefaultVariableType(
								variable.getVariableType().getRole());
				if (variableTypeEnum == org.generationcp.middleware.domain.ontology.VariableType.STUDY_DETAIL) {
					ProjectProperty property = new ProjectProperty();
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
	 * Saving project property from DMSVariableType.
	 * Setting VariableType from Role when DMSVariableType does not have value fro Variable Type.
	 * DMSVariableType will hold two similar values. Role and VariableType. We should consider using VariableType first and as a fallback
	 * we should use Role. Next iterations should remove Role from everywhere. Currently we have taken care of setting valid variable type
	 * from EditNurseryController for Variates (Trait and Selection Methods)
	 * @param project DMSProject
	 * @param objDMSVariableType DMSVariableType
	 * @throws MiddlewareQueryException
	 */
	public void saveVariableType(DmsProject project, DMSVariableType objDMSVariableType) throws MiddlewareQueryException {

		if(objDMSVariableType.getVariableType() == null){
			objDMSVariableType.setVariableType(this.daoFactory.getStandardVariableBuilder().mapPhenotypicTypeToDefaultVariableType(
							objDMSVariableType.getStandardVariable().getPhenotypicType()));
		}

		org.generationcp.middleware.domain.ontology.VariableType variableTypeEnum = objDMSVariableType.getVariableType();
		this.saveProjectProperty(project, variableTypeEnum.getId(), objDMSVariableType.getLocalName(), objDMSVariableType.getRank());
		this.saveProjectProperty(project, TermId.VARIABLE_DESCRIPTION.getId(), objDMSVariableType.getLocalDescription(), objDMSVariableType.getRank());
		this.saveProjectProperty(project, TermId.STANDARD_VARIABLE.getId(), Integer.toString(
				objDMSVariableType.getStandardVariable().getId()),
				objDMSVariableType.getRank());
		if (objDMSVariableType.getTreatmentLabel() != null && !objDMSVariableType.getTreatmentLabel().isEmpty()) {
			this.saveProjectProperty(project, TermId.MULTIFACTORIAL_INFO.getId(), objDMSVariableType.getTreatmentLabel(), objDMSVariableType
					.getRank());
		}
	}

	private void saveProjectProperty(DmsProject project, int typeId, String value, int rank) throws MiddlewareQueryException {
		ProjectProperty property = new ProjectProperty();
		property.setTypeId(typeId);
		property.setValue(value);
		property.setRank(rank);
		property.setProject(project);
		this.daoFactory.getProjectPropertyDao().save(property);
		project.addProperty(property);
	}

	public void createProjectPropertyIfNecessary(DmsProject project, TermId termId, PhenotypicType role) throws MiddlewareQueryException {
		ProjectProperty property = this.daoFactory.getProjectPropertyDao().getByStandardVariableId(project, termId.getId());
		if (property == null) {
			int rank = this.daoFactory.getProjectPropertyDao().getNextRank(project.getProjectId());
			StandardVariable stdvar = new StandardVariable();
			stdvar.setId(termId.getId());
			stdvar.setPhenotypicType(role);
			CVTerm cvTerm = this.daoFactory.getCvTermDao().getById(termId.getId());
			String localVariableName = termId.toString();
			String localVariableDescription = termId.toString();
			if (cvTerm != null) {
				localVariableName = cvTerm.getName();
				localVariableDescription = cvTerm.getDefinition();
			}

			DMSVariableType variableType = new DMSVariableType(localVariableName, localVariableDescription, stdvar, rank);
			this.saveVariableType(project, variableType);
		}
	}

	public void saveProjectProperties(DmsProject study, DmsProject trialDataset, DmsProject measurementDataset,
			List<MeasurementVariable> variables, boolean isConstant) throws MiddlewareQueryException {

		if (variables != null) {

			int rank = this.getNextRank(study);
			Set<Integer> geoIds = this.daoFactory.getGeolocationDao().getLocationIds(study.getProjectId());
			Geolocation geolocation = this.daoFactory.getGeolocationDao().getById(geoIds.iterator().next());
			Hibernate.initialize(geolocation.getProperties());

			for (MeasurementVariable variable : variables) {
				if (variable.getOperation() == Operation.ADD) {
					this.insertVariable(study, trialDataset, measurementDataset, variable, rank, isConstant, geolocation);
					rank++;
				} else if (variable.getOperation() == Operation.UPDATE) {
					if (variable.getTermId() != TermId.TRIAL_INSTANCE_FACTOR.getId()) {
						this.updateVariable(study, trialDataset, measurementDataset, variable, isConstant, geolocation);
					}
				} else if (variable.getOperation() == Operation.DELETE) {
					this.deleteVariable(study, trialDataset, measurementDataset, variable.getRole(), variable.getTermId(), geolocation);
				}
			}
		}
	}

	private int getNextRank(DmsProject project) {
		int nextRank = 1;
		if (project.getProperties() != null) {
			for (ProjectProperty property : project.getProperties()) {
				if (property.getRank() >= nextRank) {
					nextRank = property.getRank() + 1;
				}
			}
		}
		return nextRank;
	}

	private boolean isInGeolocation(int termId) {
		return TermId.TRIAL_INSTANCE_FACTOR.getId() == termId || TermId.LATITUDE.getId() == termId || TermId.LONGITUDE.getId() == termId
				|| TermId.GEODETIC_DATUM.getId() == termId || TermId.ALTITUDE.getId() == termId;
	}

	private void insertVariable(DmsProject project, DmsProject trialDataset, DmsProject measurementDataset, MeasurementVariable variable,
			int rank, boolean isConstant, Geolocation geolocation) throws MiddlewareQueryException {

		if (PhenotypicType.TRIAL_ENVIRONMENT == variable.getRole()) {
			int datasetRank = this.getNextRank(trialDataset);
			int measurementRank = this.getNextRank(measurementDataset);

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
					int datasetRank = this.getNextRank(trialDataset);
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
				int measurementRank = this.getNextRank(measurementDataset);
				this.insertVariable(measurementDataset, variable, measurementRank);
			}
		} else {
			// study
			this.insertVariable(project, variable, rank);
			VariableList variableList = new VariableList();
			variableList.add(new Variable(this.createVariableType(variable, rank), variable.getValue()));
			this.saveProjectPropValues(project.getProjectId(), variableList);
		}
	}

	private void insertVariable(DmsProject project, MeasurementVariable variable, int rank) throws MiddlewareQueryException {
		if (project.getProperties() == null) {
			project.setProperties(new ArrayList<ProjectProperty>());
		}
		this.saveVariableType(project, this.createVariableType(variable, rank));
	}

	protected DMSVariableType createVariableType(MeasurementVariable variable, int rank) {
		DMSVariableType varType = new DMSVariableType();
		StandardVariable stdvar = new StandardVariable();
		varType.setStandardVariable(stdvar);

		stdvar.setId(variable.getTermId());
		varType.setRole(variable.getRole());
		varType.setVariableType(variable.getVariableType());
		stdvar.setPhenotypicType(variable.getRole());
		varType.setLocalName(variable.getName());
		varType.setLocalDescription(variable.getDescription());
		varType.setRank(rank);

		if (variable.getTreatmentLabel() != null && !variable.getTreatmentLabel().isEmpty()) {
			varType.setTreatmentLabel(variable.getTreatmentLabel());
		}
		return varType;
	}

	private void updateVariable(DmsProject project, DmsProject trialDataset, DmsProject measurementDataset, MeasurementVariable variable,
			boolean isConstant, Geolocation geolocation) throws MiddlewareQueryException {

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
					this.daoFactory.getPhenotypeSaver().saveOrUpdatePhenotypeValue(trialDataset.getProjectId(), variable.getTermId(),
							variable.getValue(), variable.getDataTypeId());
				} else {
					// a study constant
					this.updateVariable(project, variable);
					this.daoFactory.getPhenotypeSaver().saveOrUpdatePhenotypeValue(project.getProjectId(), variable.getTermId(),
							variable.getValue(), variable.getDataTypeId());
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

	private void updateVariable(DmsProject project, MeasurementVariable variable) throws MiddlewareQueryException {
		if (project.getProperties() != null) {
			int rank = this.getRank(project, variable.getTermId());
			for (ProjectProperty property : project.getProperties()) {
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

	private int getRank(DmsProject project, int termId) {
		int rank = -1;
		if (project.getProperties() != null) {
			for (ProjectProperty property : project.getProperties()) {
				if (property.getTypeId().intValue() == TermId.STANDARD_VARIABLE.getId()
						&& property.getValue().equals(String.valueOf(termId))) {
					rank = property.getRank();
					break;
				}
			}
		}
		return rank;
	}

	private void deleteVariable(DmsProject project, DmsProject trialDataset, DmsProject measurementDataset, PhenotypicType role,
			int termId, Geolocation geolocation) throws MiddlewareQueryException {

		this.deleteVariable(project, termId);
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
			List<Integer> ids = Arrays.asList(project.getProjectId(), trialDataset.getProjectId(), measurementDataset.getProjectId());
			this.daoFactory.getPhenotypeDao().deletePhenotypesInProjectByTerm(ids, termId);
		}
	}

	private void deleteVariable(DmsProject project, int termId) throws MiddlewareQueryException {
		int rank = this.getRank(project, termId);
		if (project.getProperties() != null && !project.getProperties().isEmpty()) {
			for (Iterator<ProjectProperty> iterator = project.getProperties().iterator(); iterator.hasNext();) {
				ProjectProperty property = iterator.next();
				if (rank == property.getRank()) {
					this.daoFactory.getProjectPropertyDao().makeTransient(property);
					iterator.remove();
				}
			}
		}
	}

	private void deleteVariableForFactors(DmsProject project, MeasurementVariable variable) throws MiddlewareQueryException {
		this.deleteVariable(project, variable.getTermId());

		if (variable.getRole() == PhenotypicType.TRIAL_DESIGN) {
			this.daoFactory.getExperimentPropertyDao().deleteExperimentPropInProjectByTermId(project.getProjectId(), variable.getTermId());
		} else if (variable.getRole() == PhenotypicType.GERMPLASM) {
			this.daoFactory.getStockPropertyDao().deleteStockPropInProjectByTermId(project.getProjectId(), variable.getTermId());
		}
	}

	public void saveFactors(DmsProject measurementDataset, List<MeasurementVariable> variables) throws MiddlewareQueryException {
		if (variables != null && !variables.isEmpty()) {
			for (MeasurementVariable variable : variables) {
				if (variable.getOperation() == Operation.ADD) {
					int measurementRank = this.getNextRank(measurementDataset);
					this.insertVariable(measurementDataset, variable, measurementRank);
				} else if (variable.getOperation() == Operation.DELETE) {
					this.deleteVariableForFactors(measurementDataset, variable);
				}
				// update operation is not allowed with factors
			}
		}
	}

	public void updateVariablesRanking(int datasetId, List<Integer> variableIds) throws MiddlewareQueryException {
		int rank = this.daoFactory.getProjectPropertyDao().getNextRank(datasetId);
		Map<Integer, List<Integer>> projectPropIDMap =
				this.daoFactory.getProjectPropertyDao().getProjectPropertyIDsPerVariableId(datasetId);
		rank = this.updateVariableRank(variableIds, rank, projectPropIDMap);

		// if any factors were added but not included in list of variables, update their ranks also so they come last
		List<Integer> storedInIds = new ArrayList<Integer>();
		storedInIds.addAll(PhenotypicType.GERMPLASM.getTypeStorages());
		storedInIds.addAll(PhenotypicType.TRIAL_DESIGN.getTypeStorages());
		storedInIds.addAll(PhenotypicType.VARIATE.getTypeStorages());

		List<Integer> germplasmPlotVariateIds =
				this.daoFactory.getProjectPropertyDao().getDatasetVariableIdsForGivenStoredInIds(datasetId, storedInIds, variableIds);
		this.updateVariableRank(germplasmPlotVariateIds, rank, projectPropIDMap);
	}

	// Iterate and update rank, exclude deleted variables
	private int updateVariableRank(List<Integer> variableIds, int startRank, Map<Integer, List<Integer>> projectPropIDMap) {
		int rank = startRank;
		for (Integer variableId : variableIds) {
			List<Integer> projectPropIds = projectPropIDMap.get(variableId);
			if (projectPropIds != null) {
				this.daoFactory.getProjectPropertyDao().updateRank(projectPropIds, rank);
				rank++;
			}
		}
		return rank;
	}

}
