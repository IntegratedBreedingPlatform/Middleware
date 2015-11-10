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

package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.TreatmentVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.fieldbook.NonEditableFactors;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.util.DatasetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class WorkbookBuilder extends Builder {

	private static final List<Integer> CHARACTER_TYPE_TERM_IDS = Arrays.asList(TermId.CHARACTER_VARIABLE.getId(),
			TermId.TIMESTAMP_VARIABLE.getId(), TermId.CHARACTER_DBID_VARIABLE.getId(), TermId.CATEGORICAL_VARIABLE.getId(),
			TermId.PERSON_DATA_TYPE.getId(), TermId.LOCATION_DATA_TYPE.getId(), TermId.STUDY_DATA_TYPE.getId(),
			TermId.DATASET_DATA_TYPE.getId(), TermId.GERMPLASM_LIST_DATA_TYPE.getId(), TermId.BREEDING_METHOD_DATA_TYPE.getId());

	private static final List<Integer> EXPERIMENTAL_DESIGN_VARIABLES = Arrays.asList(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
			TermId.NUMBER_OF_REPLICATES.getId(), TermId.BLOCK_SIZE.getId(), TermId.BLOCKS_PER_REPLICATE.getId(),
			TermId.REPLICATIONS_MAP.getId(), TermId.NO_OF_REPS_IN_COLS.getId(), TermId.NO_OF_ROWS_IN_REPS.getId(),
			TermId.NO_OF_COLS_IN_REPS.getId(), TermId.NO_OF_CROWS_LATINIZE.getId(), TermId.NO_OF_CCOLS_LATINIZE.getId(),
			TermId.NO_OF_CBLKS_LATINIZE.getId());
	
	private static final Logger LOG = LoggerFactory.getLogger(WorkbookBuilder.class);

	public WorkbookBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public Workbook create(final int id) {
		return this.create(id, StudyType.N);
	}

	public Workbook create(final int id, final StudyType studyType) {
		
		Monitor monitor = MonitorFactory.start("Build Workbook");
		
		final boolean isTrial = studyType == StudyType.T;
		final Workbook workbook = new Workbook();

		/**
		 * 1. Get the dataset id 
		 * 2. Count total no. of experiments of the dataset 
		 * 3. getExperiments 
		 * 4. Per experiment, transform it to MeasurementRow 
		 *    a. MeasurementRow (list of MeasurementData) 
		 *    b. MeasurementData 
		 *       label (Experiment > VariableList > Variable > localName), 
		 *       value (Experiment > VariableList > Variable), 
		 *       datatype (Experiment > VariableList > Variable > VariableType > StandardVariable), 
		 *       iseditable (true for variates, else, false)
		 */
		
		// DA
		final StudyDetails studyDetails = this.getStudyDataManager().getStudyDetails(studyType, id);
		
		// DA getDMSProject
		final Study study = this.getStudyBuilder().createStudy(id);
		
		// DA if name not conventional
		// FIXME : this heavy id fetch pattern needs changing
		final int dataSetId = this.getMeasurementDataSetId(id, studyDetails.getStudyName());
		// validation, bring inline
		this.checkMeasurementDataset(Integer.valueOf(dataSetId));
		workbook.setMeasurementDatesetId(dataSetId);
		
		// PERF : rationale for count - improve
		final long expCount = this.getStudyDataManager().countExperiments(dataSetId);
		// Variables required to get Experiments (?)
		VariableTypeList variables = this.getDataSetBuilder().getVariableTypes(dataSetId);
		// DA get experiments
		final List<Experiment> experiments = this.getStudyDataManager().getExperiments(dataSetId, 0, (int) expCount, variables);
		
		// FIXME : this heavy id fetch pattern needs changing
		final DmsProject trialDataSetProject = this.getDataSetBuilder().getTrialDataset(study.getId());
		final DataSet trialDataSet = this.getDataSetBuilder().build(trialDataSetProject.getProjectId());
		workbook.setTrialDatasetId(trialDataSet.getId());
		
		VariableList conditionVariables = null;
		VariableList constantVariables = null; 
		VariableList trialConstantVariables = null;
		// for Trials, conditions and trial environment variables are combined
		// the trialEnvironmentVariables are filtered from the TrialDataset
		final VariableList trialEnvironmentVariables = this.getTrialEnvironmentVariableList(trialDataSet);
		if (isTrial) {
			conditionVariables = new VariableList();
			conditionVariables.addAll(study.getConditions());
			conditionVariables.addAll(trialEnvironmentVariables);
		} else {
			conditionVariables = study.getConditions();
		}
		constantVariables = study.getConstants();
		trialConstantVariables = this.getTrialConstants(trialDataSet);
		// FIXME : I think we are reducing to traits, but difficult to understand
		variables = this.removeTrialDatasetVariables(variables, trialEnvironmentVariables);

		// we set roles here (study, trial, variate) which seem to match the dataset : reconcile - we might be over-categorising
		final List<MeasurementVariable> conditions = this.buildStudyMeasurementVariables(conditionVariables, true, true);
		final List<MeasurementVariable> factors = this.buildFactors(variables, isTrial);
		final List<MeasurementVariable> constants = this.buildStudyMeasurementVariables(constantVariables, false, true);
		constants.addAll(this.buildStudyMeasurementVariables(trialConstantVariables, false, false));
		final List<MeasurementVariable> variates = this.buildVariates(variables, constants);
		final List<MeasurementVariable> expDesignVariables = new ArrayList<MeasurementVariable>();
		
		// Nursery case
		if (!isTrial) {
			// remove OCC from nursery level conditions for nursery cause its duplicating becuase its being added in conditions and factors
			// FIXME : redesign dataset or filter earlier
			final Iterator<MeasurementVariable> iter = conditions.iterator();
			while (iter.hasNext()) {
				if (iter.next().getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					iter.remove();
				}
			}
		}
		
		// Set possible values of breeding method
		for (final MeasurementVariable variable : variates) {
			if (this.getOntologyDataManager().getProperty(variable.getProperty()).getTerm().getId() == TermId.BREEDING_METHOD_PROP.getId()) {
				// DA get all methods not generative
				variable.setPossibleValues(this.getAllBreedingMethods());
			}
		}

		// Build Observation Unit from a Measurement
		// DA previous for experiments
		final List<MeasurementRow> observations =
				this.buildObservations(experiments, variables.getVariates(), factors, variates, isTrial, conditions);
		final List<TreatmentVariable> treatmentFactors = this.buildTreatmentFactors(variables);
		final List<ProjectProperty> projectProperties = trialDataSetProject.getProperties();

		final Map<Integer, org.generationcp.middleware.domain.ontology.VariableType> projectPropRoleMapping =
				this.generateProjectPropertyRoleMap(projectProperties);
		
		for (final ProjectProperty projectProperty : projectProperties) {
			if (projectProperty.getTypeId().equals(TermId.STANDARD_VARIABLE.getId())) {
				// DA IN A LOOP
				final StandardVariable stdVariable =
						this.getStandardVariableBuilder().create(Integer.parseInt(projectProperty.getValue()), study.getProgramUUID());

				final org.generationcp.middleware.domain.ontology.VariableType varType = projectPropRoleMapping.get(stdVariable.getId());
				if (varType != null) {
					stdVariable.setPhenotypicType(varType.getRole());
					if (!isTrial && PhenotypicType.TRIAL_ENVIRONMENT == varType.getRole()) {

						Double minRange = null, maxRange = null;
						if (stdVariable.getConstraints() != null) {
							minRange = stdVariable.getConstraints().getMinValue();
							maxRange = stdVariable.getConstraints().getMaxValue();
						}

						String value = null;
						final int varId = stdVariable.getId();
						if (varType.getRole() == PhenotypicType.TRIAL_ENVIRONMENT) {
							// DA geolocation prop access for value
							value = this.getStudyDataManager().getGeolocationPropValue(stdVariable.getId(), id);
						}
						// if value is null we have a .... trial instance, or location attribute (lat,long etc)
						if (value == null) {
							// set trial env for nursery studies
							final List<Integer> locIds = this.getExperimentDao().getLocationIdsOfStudy(id);
							if (locIds != null && !locIds.isEmpty()) {
								final Integer locId = locIds.get(0);
								// DA geolocation table
								final Geolocation geolocation = this.getGeolocationDao().getById(locId);
								if (geolocation != null) {
									if (TermId.TRIAL_INSTANCE_FACTOR.getId() == varId) {
										value = geolocation.getDescription();

									} else if (TermId.LATITUDE.getId() == varId && geolocation.getLatitude() != null) {
										value = geolocation.getLatitude().toString();

									} else if (TermId.LONGITUDE.getId() == varId && geolocation.getLongitude() != null) {
										value = geolocation.getLongitude().toString();

									} else if (TermId.GEODETIC_DATUM.getId() == varId && geolocation.getGeodeticDatum() != null) {
										geolocation.setGeodeticDatum(value);

									} else if (TermId.ALTITUDE.getId() == varId && geolocation.getAltitude() != null) {
										value = geolocation.getAltitude().toString();
									}
								}
							}
							// redundant logic?
							if (value == null) {
								value = "";
							}
						}
						
						// continuing redundant logic ... 
						if (value != null) {
							final MeasurementVariable measurementVariable =
									new MeasurementVariable(stdVariable.getId(), this.getLocalName(projectProperty.getRank(),
											projectProperties),// projectProperty.getValue(),
											stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod()
													.getName(), stdVariable.getProperty().getName(), stdVariable.getDataType().getName(),
											value, "", minRange, maxRange);
							measurementVariable.setFactor(true);
							measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
							measurementVariable.setPossibleValues(this.getMeasurementVariableTransformer().transformPossibleValues(
									stdVariable.getEnumerations()));
							measurementVariable.setRole(varType.getRole());
							if (WorkbookBuilder.EXPERIMENTAL_DESIGN_VARIABLES.contains(stdVariable.getId())) {
								expDesignVariables.add(measurementVariable);
							} else if (!conditions.contains(measurementVariable)) {
								conditions.add(measurementVariable);
							}
						}
					// control flow is unreadable here
					} else if (isTrial && WorkbookBuilder.EXPERIMENTAL_DESIGN_VARIABLES.contains(stdVariable.getId())) {

						final String value = this.getStudyDataManager().getGeolocationPropValue(stdVariable.getId(), id);

						Double minRange = null, maxRange = null;
						if (stdVariable.getConstraints() != null) {
							minRange = stdVariable.getConstraints().getMinValue();
							maxRange = stdVariable.getConstraints().getMaxValue();
						}
						final MeasurementVariable measurementVariable =
								new MeasurementVariable(
										stdVariable.getId(),
										this.getLocalName(projectProperty.getRank(), projectProperties),// projectProperty.getValue(),
										stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod().getName(),
										stdVariable.getProperty().getName(), stdVariable.getDataType().getName(), value, "", minRange,
										maxRange);
						measurementVariable.setFactor(true);
						measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
						measurementVariable.setPossibleValues(this.getMeasurementVariableTransformer().transformPossibleValues(
								stdVariable.getEnumerations()));
						measurementVariable.setRole(varType.getRole());
						expDesignVariables.add(measurementVariable);
						this.setValueInCondition(conditions, value, stdVariable.getId());
					}
				}
			}
		}

		workbook.setStudyDetails(studyDetails);
		workbook.setFactors(factors);
		workbook.setVariates(variates);
		workbook.setConditions(conditions);
		workbook.setConstants(constants);
		workbook.setObservations(observations);
		workbook.setTreatmentFactors(treatmentFactors);
		workbook.setExperimentalDesignVariables(expDesignVariables);

		final List<MeasurementRow> trialObservations = this.getTrialObservations(workbook, isTrial);
		workbook.setTrialObservations(trialObservations);
		LOG.debug("" + monitor.stop() + ". This instance was for studyId: " + id);
		
		return workbook;
	}

	private List<MeasurementRow> getTrialObservations(final Workbook workbook, final boolean isTrial) {
		List<MeasurementRow> trialObservations = null;
		if (!isTrial) {
			trialObservations =
					this.buildTrialObservations(workbook.getTrialDatasetId(), workbook.getTrialConditions(), workbook.getTrialConstants());
		} else {
			trialObservations = this.getDataSetBuilder().buildCompleteDataset(workbook.getTrialDatasetId(), isTrial).getObservations();
		}
		return trialObservations;
	}

	protected void checkMeasurementDataset(final Integer dataSetId) {
		// if study has no measurementDataset, throw an error as it is an invalid template
		if (dataSetId == null || dataSetId.equals(0)) {
			throw new MiddlewareQueryException(ErrorCode.STUDY_FORMAT_INVALID.getCode(), "The term you entered is invalid");
		}
	}

	private void setValueInCondition(final List<MeasurementVariable> conditions, final String value, final int id) {
		if (conditions != null && !conditions.isEmpty()) {
			for (final MeasurementVariable condition : conditions) {
				if (condition.getTermId() == id) {
					condition.setValue(value);
					break;
				}
			}
		}
	}

	private Map<Integer, org.generationcp.middleware.domain.ontology.VariableType> generateProjectPropertyRoleMap(
			final List<ProjectProperty> projectProperties) {
		final Map<Integer, org.generationcp.middleware.domain.ontology.VariableType> projPropRoleMap =
				new HashMap<Integer, org.generationcp.middleware.domain.ontology.VariableType>();
		for (final ProjectProperty projectProp : projectProperties) {
			if (projectProp.getTypeId().equals(TermId.STANDARD_VARIABLE.getId())) {

				final int currentRank = projectProp.getRank();
				for (final ProjectProperty projectPropInside : projectProperties) {
					if (projectPropInside.getRank() == currentRank
							&& org.generationcp.middleware.domain.ontology.VariableType.getById(projectPropInside.getTypeId()) != null) {
						final org.generationcp.middleware.domain.ontology.VariableType varType =
								org.generationcp.middleware.domain.ontology.VariableType.getById(projectPropInside.getTypeId());
						projPropRoleMap.put(Integer.parseInt(projectProp.getValue()), varType);
						break;
					}
				}

			}
		}
		return projPropRoleMap;
	}

	public Workbook createStudyVariableSettings(final int id, final boolean isNursery) {
		final Workbook workbook = new Workbook();
		final Study study = this.getStudyBuilder().createStudy(id);
		Integer dataSetId = null, trialDatasetId = null;
		// get observation dataset

		final List<DatasetReference> datasetRefList = this.getStudyDataManager().getDatasetReferences(id);
		if (datasetRefList != null) {
			StudyType studyType = StudyType.N;
			if (!isNursery) {
				studyType = StudyType.T;
			}
			final StudyDetails studyDetails = this.getStudyDataManager().getStudyDetails(studyType, id);
			workbook.setStudyDetails(studyDetails);
			for (final DatasetReference datasetRef : datasetRefList) {
				if (datasetRef.getName().equals("MEASUREMENT EFEC_" + studyDetails.getStudyName())
						|| datasetRef.getName().equals("MEASUREMENT EFECT_" + studyDetails.getStudyName())) {
					dataSetId = datasetRef.getId();
				} else if (datasetRef.getName().equals("TRIAL_" + studyDetails.getStudyName())) {
					trialDatasetId = datasetRef.getId();
				}
			}
		}


		// if dataset is not found, get dataset with Plot Data type
		if (dataSetId == null || dataSetId == 0) {
			final DataSet dataset = this.getStudyDataManager().findOneDataSetByType(id, DataSetType.PLOT_DATA);
			if (dataset != null) {
				dataSetId = dataset.getId();
			}
		}

		if (trialDatasetId == null || trialDatasetId == 0) {
			final DataSet dataset = this.getStudyDataManager().findOneDataSetByType(id, DataSetType.SUMMARY_DATA);
			if (dataset != null) {
				trialDatasetId = dataset.getId();
			}
		}

		this.checkMeasurementDataset(dataSetId);

		workbook.setMeasurementDatesetId(dataSetId);
		workbook.setTrialDatasetId(trialDatasetId);

		VariableTypeList variables = null;
		if (dataSetId != null) {
			variables = this.getDataSetBuilder().getVariableTypes(dataSetId);
			// variable type roles are being set inside getexperiment
			this.getStudyDataManager().getExperiments(dataSetId, 0, Integer.MAX_VALUE, variables);
		}

		final List<MeasurementVariable> factors = this.buildFactors(variables, !isNursery);
		List<MeasurementVariable> variates = this.buildVariates(variables);
		final List<MeasurementVariable> conditions = this.buildStudyMeasurementVariables(study.getConditions(), true, true);
		final List<MeasurementVariable> constants = this.buildStudyMeasurementVariables(study.getConstants(), false, true);
		final List<TreatmentVariable> treatmentFactors = this.buildTreatmentFactors(variables);
		if (dataSetId != null) {
			this.setTreatmentFactorValues(treatmentFactors, dataSetId);
		}
		final DmsProject dmsProject = this.getDataSetBuilder().getTrialDataset(id);
		final List<MeasurementVariable> experimentalDesignVariables = new ArrayList<MeasurementVariable>();
		final List<ProjectProperty> projectProperties = dmsProject != null ? dmsProject.getProperties() : new ArrayList<ProjectProperty>();
		final Map<Integer, org.generationcp.middleware.domain.ontology.VariableType> projectPropRoleMapping =
				this.generateProjectPropertyRoleMap(projectProperties);
		for (final ProjectProperty projectProperty : projectProperties) {
			boolean isConstant = false;
			if (projectProperty.getTypeId().equals(TermId.STANDARD_VARIABLE.getId())) {
				final StandardVariable stdVariable =
						this.getStandardVariableBuilder().create(Integer.parseInt(projectProperty.getValue()), study.getProgramUUID());
				final org.generationcp.middleware.domain.ontology.VariableType varType = projectPropRoleMapping.get(stdVariable.getId());
				if (varType != null) {
					stdVariable.setPhenotypicType(varType.getRole());

					if (PhenotypicType.TRIAL_ENVIRONMENT == varType.getRole() || PhenotypicType.VARIATE == varType.getRole()) {

						Double minRange = null, maxRange = null;
						if (stdVariable.getConstraints() != null) {
							minRange = stdVariable.getConstraints().getMaxValue();
							maxRange = stdVariable.getConstraints().getMaxValue();
						}

						String value = null;
						if (PhenotypicType.TRIAL_ENVIRONMENT == varType.getRole()) {
							value = this.getStudyDataManager().getGeolocationPropValue(stdVariable.getId(), id);
							if (value == null) {
								value = "";
							}
						} else if (PhenotypicType.VARIATE == varType.getRole()) {
							// constants, no need to retrieve the value if it's a trial study
							isConstant = true;
							if (isNursery) {
								final List<Phenotype> phenotypes =
										this.getPhenotypeDao().getByProjectAndType(trialDatasetId, stdVariable.getId());
								// expects only 1 value for nursery
								if (phenotypes != null && !phenotypes.isEmpty()) {
									if (phenotypes.get(0).getcValueId() != null) {
										// categorical constant
										final Enumeration enumeration = stdVariable.getEnumeration(phenotypes.get(0).getcValueId());
										value = enumeration.getDescription();
									} else {
										value = phenotypes.get(0).getValue();
									}
								}
								if (value == null) {
									value = "";
								}
							} else {
								value = "";
							}
						}

						if (isNursery && "".equalsIgnoreCase(value)) {
							// set trial env for nursery studies
							final List<Integer> locIds = this.getExperimentDao().getLocationIdsOfStudy(id);
							if (locIds != null && !locIds.isEmpty()) {
								final Integer locId = locIds.get(0);
								final Geolocation geolocation = this.getGeolocationDao().getById(locId);
								final int varId = stdVariable.getId();
								if (geolocation != null) {
									if (TermId.TRIAL_INSTANCE_FACTOR.getId() == varId) {
										value = geolocation.getDescription();

									} else if (TermId.LATITUDE.getId() == varId && geolocation.getLatitude() != null) {
										value = geolocation.getLatitude().toString();

									} else if (TermId.LONGITUDE.getId() == varId && geolocation.getLongitude() != null) {
										value = geolocation.getLongitude().toString();

									} else if (TermId.GEODETIC_DATUM.getId() == varId && geolocation.getGeodeticDatum() != null) {
										geolocation.setGeodeticDatum(value);

									} else if (TermId.ALTITUDE.getId() == varId && geolocation.getAltitude() != null) {
										value = geolocation.getAltitude().toString();
									}
								}
							}
							if (value == null) {
								value = "";
							}
						}

						if (value != null) {
							final MeasurementVariable measurementVariable =
									new MeasurementVariable(stdVariable.getId(), this.getLocalName(projectProperty.getRank(),
											projectProperties),// projectProperty.getValue(),
											stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod()
													.getName(), stdVariable.getProperty().getName(), stdVariable.getDataType().getName(),
											value, "", minRange, maxRange);
							measurementVariable.setFactor(true);
							measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
							measurementVariable.setPossibleValues(this.getMeasurementVariableTransformer().transformPossibleValues(
									stdVariable.getEnumerations()));
							measurementVariable.setRole(varType.getRole());
							if (WorkbookBuilder.EXPERIMENTAL_DESIGN_VARIABLES.contains(stdVariable.getId())) {
								experimentalDesignVariables.add(measurementVariable);
							} else if (isConstant) {
								constants.add(measurementVariable);
							} else {
								conditions.add(measurementVariable);
							}
						}
					}
				}
			}
		}

		variates = this.removeConstantsFromVariates(variates, constants);
		workbook.setFactors(factors);
		workbook.setVariates(variates);
		workbook.setConditions(conditions);
		workbook.setConstants(constants);
		workbook.setTreatmentFactors(treatmentFactors);
		workbook.setExperimentalDesignVariables(experimentalDesignVariables);
		return workbook;
	}

	private List<MeasurementRow> buildObservations(final List<Experiment> experiments, final VariableTypeList variateTypes,
			final List<MeasurementVariable> factorList, final List<MeasurementVariable> variateList, final boolean isTrial,
			final List<MeasurementVariable> conditionList) {

		final List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
		for (final Experiment experiment : experiments) {
			final int experimentId = experiment.getId();
			final VariableList factors = experiment.getFactors();
			final VariableList variates = this.getCompleteVariatesInExperiment(experiment, variateTypes);
			final List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();

			if (isTrial) {
				for (final MeasurementVariable condition : conditionList) {
					for (final Variable variable : factors.getVariables()) {
						if (condition.getTermId() == variable.getVariableType().getStandardVariable().getId()
								&& variable.getVariableType().getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
							final boolean isEditable =
									NonEditableFactors.find(variable.getVariableType().getStandardVariable().getId()) == null ? true
											: false;
							final MeasurementData measurementData =
									new MeasurementData(variable.getVariableType().getLocalName(), variable.getValue(), isEditable,
											this.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
											condition);
							measurementDataList.add(measurementData);
							break;
						}
					}
				}
			}
			for (final MeasurementVariable factor : factorList) {
				boolean found = false;
				for (final Variable variable : factors.getVariables()) {
					if (factor.getTermId() == variable.getVariableType().getStandardVariable().getId()) {
						found = true;
						if (isTrial && variable.getVariableType().getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()
								|| PhenotypicType.TRIAL_ENVIRONMENT != variable.getVariableType().getRole()) {
							final boolean isEditable =
									NonEditableFactors.find(variable.getVariableType().getStandardVariable().getId()) == null ? true
											: false;
							final MeasurementData measurementData;
							if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE
									.getId()) {
								final Integer id =
										variable.getValue() != null && NumberUtils.isNumber(variable.getValue()) ? Integer.valueOf(variable
												.getValue()) : null;
								measurementData =
										new MeasurementData(variable.getVariableType().getLocalName(), variable.getDisplayValue(),
												isEditable, this.getDataType(variable.getVariableType().getStandardVariable().getDataType()
														.getId()), id, factor);
							} else {
								measurementData =
										new MeasurementData(variable.getVariableType().getLocalName(), variable.getValue(), isEditable,
												this.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
												factor);
							}
							measurementDataList.add(measurementData);
							break;
						}
					}
				}
				if (!found) {
					final boolean isEditable = NonEditableFactors.find(factor.getTermId()) == null ? true : false;
					final MeasurementData measurementData =
							new MeasurementData(factor.getName(), null, isEditable, this.getDataType(factor.getDataTypeId()),
									factor.getTermId(), factor);
					measurementDataList.add(measurementData);
				}
			}

			this.populateMeasurementData(variateList, variates, measurementDataList);

			final MeasurementRow measurementRow = new MeasurementRow(measurementDataList);
			measurementRow.setExperimentId(experimentId);
			measurementRow.setLocationId(experiment.getLocationId());

			observations.add(measurementRow);
		}

		return observations;
	}

	protected void populateMeasurementData(final List<MeasurementVariable> variateList, final VariableList variates,
			final List<MeasurementData> measurementDataList) {
		for (final MeasurementVariable variate : variateList) {
			boolean found = false;

			for (final Variable variable : variates.getVariables()) {
				if (variate.getTermId() == variable.getVariableType().getStandardVariable().getId()) {
					found = true;
					final MeasurementData measurementData =
							new MeasurementData(variable.getVariableType().getLocalName(), variable.getValue(), true,
									this.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()), variate);
					measurementData.setPhenotypeId(variable.getPhenotypeId());
					measurementData.setAccepted(true);
					if (this.isCategoricalVariate(variable) && !variable.isCustomValue() && NumberUtils.isNumber(variable.getValue())) {
						// we set the cValue id if the isCustomValue flag is false, since this is an id of the valid value
						// we check if its a number to be sure
						measurementData.setcValueId(variable.getValue());
					}
					measurementDataList.add(measurementData);
					break;
				}
			}
			if (!found) {
				final MeasurementData measurementData =
						new MeasurementData(variate.getName(), null, true, this.getDataType(variate.getDataTypeId()), variate);
				measurementDataList.add(measurementData);
			}
		}
	}

	protected boolean isCategoricalVariate(final Variable variable) {
		final StandardVariable stdVar = variable.getVariableType().getStandardVariable();
		return PhenotypicType.VARIATE == stdVar.getPhenotypicType() && stdVar.getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId();
	}

	private List<ValueReference> getAllBreedingMethods() {
		final List<ValueReference> list = new ArrayList<ValueReference>();
		final List<Method> methodList = this.getGermplasmDataManager().getAllMethodsNotGenerative();

		Collections.sort(methodList, new Comparator<Method>() {

			@Override
			public int compare(final Method o1, final Method o2) {
				final String methodName1 = o1.getMname().toUpperCase();
				final String methodName2 = o2.getMname().toUpperCase();

				// ascending order
				return methodName1.compareTo(methodName2);
			}

		});

		if (methodList != null && !methodList.isEmpty()) {
			for (final Method method : methodList) {
				if (method != null) {
					list.add(new ValueReference(method.getMid(), method.getMname() + " - " + method.getMcode(), method.getMname() + " - "
							+ method.getMcode()));
				}
			}
		}
		return list;
	}

	private String getDataType(final int dataTypeId) {
		// datatype ids: 1120, 1125, 1128, 1130
		if (CHARACTER_TYPE_TERM_IDS.contains(dataTypeId)) {
			return "C";
		} else {
			return "N";
		}
	}

	private List<MeasurementVariable> buildStudyMeasurementVariables(final VariableList variableList, final boolean isFactor,
			final boolean isStudy) {
		final List<MeasurementVariable> measurementVariableLists =
				this.getMeasurementVariableTransformer().transform(variableList, isFactor, isStudy);
		setMeasurementVarRoles(measurementVariableLists, isFactor, isStudy);
		return measurementVariableLists;
	}

	protected void setMeasurementVarRoles(final List<MeasurementVariable> measurementVariableLists, final boolean isFactor,
			final boolean isStudy) {
		PhenotypicType role = null;
		if (!isFactor) {
			// is factor == false, then always variate phenotype
			role = PhenotypicType.VARIATE;
		} else if (isStudy) {
			// if factor and is study
			role = PhenotypicType.STUDY;
		} else if (!isStudy) {
			// if factor and is not study
			role = PhenotypicType.TRIAL_ENVIRONMENT;
		}
		if (role != null) {
			for (final MeasurementVariable var : measurementVariableLists) {
				var.setRole(role);
			}
		}
	}

	private List<TreatmentVariable> buildTreatmentFactors(final VariableTypeList variables) {
		final List<TreatmentVariable> treatmentFactors = new ArrayList<TreatmentVariable>();
		List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
		final Map<String, VariableTypeList> treatmentMap = new HashMap<String, VariableTypeList>();
		if (variables != null && variables.getFactors() != null && !variables.getFactors().getVariableTypes().isEmpty()) {
			for (final DMSVariableType variable : variables.getFactors().getVariableTypes()) {
				if (variable.getRole() == PhenotypicType.TRIAL_DESIGN && variable.getTreatmentLabel() != null
						&& !variable.getTreatmentLabel().isEmpty()) {

					VariableTypeList list = treatmentMap.get(variable.getTreatmentLabel());
					if (list == null) {
						list = new VariableTypeList();
						treatmentMap.put(variable.getTreatmentLabel(), list);
					}
					list.add(variable);
				}
			}

			final Set<String> keys = treatmentMap.keySet();
			for (final String key : keys) {
				factors = this.getMeasurementVariableTransformer().transform(treatmentMap.get(key), false);
				final TreatmentVariable treatment = new TreatmentVariable();
				for (final MeasurementVariable factor : factors) {
					if (factor.getName().equals(key)) {
						treatment.setLevelVariable(factor);
					} else {
						treatment.setValueVariable(factor);
					}
				}
				treatmentFactors.add(treatment);
			}
		}

		return treatmentFactors;
	}

	private List<MeasurementVariable> buildFactors(final VariableTypeList variables, final boolean isTrial) {
		List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
		final VariableTypeList factorList = new VariableTypeList();
		if (variables != null && variables.getFactors() != null && !variables.getFactors().getVariableTypes().isEmpty()) {

			for (final DMSVariableType variable : variables.getFactors().getVariableTypes()) {
				if (PhenotypicType.TRIAL_DESIGN == variable.getRole() || PhenotypicType.GERMPLASM == variable.getRole()
						|| PhenotypicType.TRIAL_ENVIRONMENT == variable.getRole()) {

					factorList.add(variable);
				}
			}
			factors = this.getMeasurementVariableTransformer().transform(factorList, true);
		}
		return factors;
	}

	private List<MeasurementVariable> removeConstantsFromVariates(final List<MeasurementVariable> variates,
			final List<MeasurementVariable> constants) {
		final List<MeasurementVariable> newVariates = new ArrayList<MeasurementVariable>();
		if (variates != null && !variates.isEmpty()) {
			for (final MeasurementVariable variate : variates) {
				boolean found = false;
				if (constants != null && !constants.isEmpty()) {
					for (final MeasurementVariable constant : constants) {
						if (variate.getTermId() == constant.getTermId()) {
							found = true;
						}
					}
				}
				if (!found) {
					newVariates.add(variate);
				}
			}
		}
		return newVariates;
	}

	private List<MeasurementVariable> buildVariates(final VariableTypeList variables) {
		return this.buildVariates(variables, null);
	}

	private List<MeasurementVariable> buildVariates(final VariableTypeList variables, final List<MeasurementVariable> constants) {
		List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
		VariableTypeList filteredVariables = null;

		if (variables != null && variables.getVariates() != null && !variables.getVariates().getVariableTypes().isEmpty()) {
			final List<String> constantHeaders = new ArrayList<String>();
			if (constants != null) {
				for (final MeasurementVariable constant : constants) {
					constantHeaders.add(constant.getName());
				}
				filteredVariables = new VariableTypeList();
				for (final DMSVariableType variable : variables.getVariableTypes()) {
					if (!constantHeaders.contains(variable.getLocalName())) {
						filteredVariables.add(variable);
					}
				}
			} else {
				filteredVariables = variables;
			}

			if (!filteredVariables.isEmpty()) {
				variates = this.getMeasurementVariableTransformer().transform(filteredVariables.getVariates(), false);
			}
		}

		return variates;
	}

	private VariableList getCompleteVariatesInExperiment(final Experiment experiment, final VariableTypeList variateTypes) {
		final VariableList vlist = new VariableList();

		for (final DMSVariableType vType : variateTypes.getVariableTypes()) {
			boolean found = false;

			// added for optimization
			final String key = Integer.toString(vType.getId());
			final Variable var = experiment.getVariatesMap().get(key);
			if (var != null) {
				vlist.add(var);
				found = true;
			}
			if (!found) {
				vlist.add(new Variable(vType, (String) null));
			}
		}

		return vlist;
	}

	private MeasurementVariable getMeasurementVariableByName(final String name, final List<MeasurementVariable> list) {
		final MeasurementVariable var = null;
		for (final MeasurementVariable variable : list) {
			if (variable.getName().equalsIgnoreCase(name)) {
				return variable;
			}
		}
		return var;
	}

	private String getLocalName(final int rank, final List<ProjectProperty> properties) {
		for (final ProjectProperty property : properties) {
			if (org.generationcp.middleware.domain.ontology.VariableType.getById(property.getTypeId()) != null
					&& rank == property.getRank()) {
				return property.getValue();
			}
		}
		return "";
	}

	private VariableList getTrialEnvironmentVariableList(DataSet trialDataset) {
		final VariableTypeList typeList = trialDataset.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		final VariableList list = new VariableList();
		for (final DMSVariableType type : typeList.getVariableTypes()) {
			list.add(new Variable(type, (String) null));
		}
		return list;
	}

	private VariableList getTrialConstants(DataSet trialDataSet) {
		final VariableTypeList typeList = trialDataSet.getVariableTypes().getVariates();

		final VariableList list = new VariableList();
		for (final DMSVariableType type : typeList.getVariableTypes()) {
			list.add(new Variable(type, (String) null));
		}
		return list;
	}

	public List<MeasurementRow> buildTrialObservations(final int trialDatasetId, final List<MeasurementVariable> factorList,
			final List<MeasurementVariable> variateList) {

		final int totalRows = (int) this.getStudyDataManager().countExperiments(trialDatasetId);
		final List<Experiment> experiments = this.getStudyDataManager().getExperiments(trialDatasetId, 0, totalRows);

		final List<MeasurementRow> rows = new ArrayList<MeasurementRow>();
		if (experiments != null) {
			for (final Experiment experiment : experiments) {
				final List<MeasurementData> dataList = new ArrayList<MeasurementData>();
				for (final Variable variable : experiment.getFactors().getVariables()) {
					if (variable.getVariableType().getId() == TermId.EXPERIMENT_DESIGN_FACTOR.getId()) {
						continue;
					}
					MeasurementData measurementData = null;
					final MeasurementVariable measurementVariable =
							this.getMeasurementVariableByName(variable.getVariableType().getLocalName(), factorList);
					if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
						final Integer id =
								variable.getValue() != null && NumberUtils.isNumber(variable.getValue()) ? Integer.valueOf(variable
										.getValue()) : null;
						measurementData =
								new MeasurementData(variable.getVariableType().getLocalName(), variable.getDisplayValue(), false,
										this.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()), id,
										measurementVariable);
					} else {
						measurementData =
								new MeasurementData(variable.getVariableType().getLocalName(), variable.getValue(), false,
										this.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
										measurementVariable);
					}

					if (experiments.size() == 1 && measurementVariable != null) {
						measurementVariable.setValue(variable.getValue());
					}
					dataList.add(measurementData);
				}
				for (final Variable variable : experiment.getVariates().getVariables()) {
					MeasurementData measurementData = null;
					final MeasurementVariable measurementVariable =
							this.getMeasurementVariableByName(variable.getVariableType().getLocalName(), variateList);
					Integer id = null;
					if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
						id =
								variable.getValue() != null && NumberUtils.isNumber(variable.getValue()) ? Integer.valueOf(variable
										.getValue()) : null;
					}
					measurementData =
							new MeasurementData(variable.getVariableType().getLocalName(), variable.getValue(), true,
									this.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()), id,
									measurementVariable);
					measurementData.setPhenotypeId(variable.getPhenotypeId());
					if (experiments.size() == 1) {
						measurementVariable.setValue(variable.getValue());
					}
					dataList.add(measurementData);
				}

				final MeasurementRow row = new MeasurementRow(dataList);
				row.setExperimentId(experiment.getId());
				row.setLocationId(experiment.getLocationId());
				rows.add(row);
			}
		}
		return rows;
	}

	protected VariableTypeList removeTrialDatasetVariables(final VariableTypeList variables, final VariableList toBeDeleted) {
		final List<Integer> trialList = new ArrayList<Integer>();
		if (toBeDeleted != null && !toBeDeleted.isEmpty()) {
			for (final Variable variable : toBeDeleted.getVariables()) {
				trialList.add(variable.getVariableType().getStandardVariable().getId());
			}
		}

		final VariableTypeList list = new VariableTypeList();
		if (variables != null) {
			for (final DMSVariableType type : variables.getVariableTypes()) {
				if (!trialList.contains(type.getStandardVariable().getId())) {
					list.add(type);
				}
			}
		}
		return list;
	}

	public int getMeasurementDataSetId(final int studyId, final String studyName) {
		final List<DatasetReference> datasetRefList = this.getStudyDataManager().getDatasetReferences(studyId);
		for (final DatasetReference datasetRef : datasetRefList) {
			String datasetName = datasetRef.getName();
			if (datasetName.endsWith(DatasetUtil.NEW_PLOT_DATASET_NAME_SUFFIX)) {
				return datasetRef.getId();
			}

			// Legacy daatset naming convention handling
			if (datasetName.startsWith(DatasetUtil.OLD_PLOT_DATASET_NAME_PREFIX)) {
				return datasetRef.getId();
			}

			// Legacy daatset naming convention handling
			if (datasetName.endsWith(DatasetUtil.OLD_PLOT_DATASET_NAME_SUFFIX)) {
				return datasetRef.getId();
			}
		}
		// if not found (which should be extremely rare) in the dataset ref list using the name, 
		// get dataset reference by dataset type in projectprops
		final DatasetReference datasetRef = this.getStudyDataManager().findOneDataSetReferenceByType(studyId, DataSetType.PLOT_DATA);
		if (datasetRef != null) {
			return datasetRef.getId();
		} else {
			return 0;
		}
	}

	public int getTrialDataSetId(final int studyId, final String studyName) {
		final List<DatasetReference> datasetRefList = this.getStudyDataManager().getDatasetReferences(studyId);
		if (datasetRefList != null) {
			for (final DatasetReference datasetRef : datasetRefList) {
				if (datasetRef.getName().equals("TRIAL_" + studyName)) {
					return datasetRef.getId();
				}
			}
		}
		// if not found in the list using the name, get dataset with Summary Data type
		final DataSet dataset = this.getStudyDataManager().findOneDataSetByType(studyId, DataSetType.SUMMARY_DATA);
		if (dataset != null) {
			return dataset.getId();
		} else {
			return 0;
		}
	}

	public List<MeasurementRow> buildDatasetObservations(final List<Experiment> experiments, final VariableTypeList variateTypes,
			final List<MeasurementVariable> factorList, final List<MeasurementVariable> variateList) {

		final List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
		for (final Experiment experiment : experiments) {
			final int experimentId = experiment.getId();
			final VariableList factors = experiment.getFactors();
			final VariableList variates = this.getCompleteVariatesInExperiment(experiment, variateTypes);
			final List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();

			for (final MeasurementVariable factor : factorList) {
				boolean found = false;
				for (final Variable variable : factors.getVariables()) {

					if (factor.getTermId() == variable.getVariableType().getStandardVariable().getId()) {
						found = true;

						final boolean isEditable =
								NonEditableFactors.find(variable.getVariableType().getStandardVariable().getId()) == null ? true : false;
						MeasurementData measurementData = null;
						if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
							final Integer id =
									variable.getValue() != null && NumberUtils.isNumber(variable.getValue()) ? Integer.valueOf(variable
											.getValue()) : null;
							measurementData =
									new MeasurementData(variable.getVariableType().getLocalName(), variable.getDisplayValue(), isEditable,
											this.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()), id,
											factor);
						} else {
							measurementData =
									new MeasurementData(variable.getVariableType().getLocalName(), variable.getValue(), isEditable,
											this.getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
											factor);
						}
						measurementDataList.add(measurementData);
						break;
					}
				}
				if (!found) {
					final boolean isEditable = NonEditableFactors.find(factor.getTermId()) == null ? true : false;
					final MeasurementData measurementData =
							new MeasurementData(factor.getName(), null, isEditable, this.getDataType(factor.getDataTypeId()),
									factor.getTermId(), factor);
					measurementDataList.add(measurementData);
				}
			}

			this.populateMeasurementData(variateList, variates, measurementDataList);

			final MeasurementRow measurementRow = new MeasurementRow(measurementDataList);
			measurementRow.setExperimentId(experimentId);
			measurementRow.setLocationId(experiment.getLocationId());

			observations.add(measurementRow);
		}

		return observations;
	}

	public void setTreatmentFactorValues(final List<TreatmentVariable> treatmentVariables, final int measurementDatasetId) {

		for (final TreatmentVariable treatmentVariable : treatmentVariables) {
			final List<String> values =
					this.getExperimentPropertyDao().getTreatmentFactorValues(treatmentVariable.getLevelVariable().getTermId(),
							treatmentVariable.getValueVariable().getTermId(), measurementDatasetId);
			treatmentVariable.setValues(values);
		}
	}
}
