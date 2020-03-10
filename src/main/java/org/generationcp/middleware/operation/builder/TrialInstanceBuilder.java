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

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.TrialInstance;
import org.generationcp.middleware.domain.dms.TrialInstanceProperty;
import org.generationcp.middleware.domain.dms.TrialInstances;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TrialInstanceBuilder extends Builder {

	@Resource
	private DataSetBuilder dataSetBuilder;

	private DaoFactory daoFactory;

	public TrialInstanceBuilder() {

	}

	public TrialInstanceBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public TrialInstances getTrialInstanceInDataset(final int studyId, final int datasetId) {
		final DmsProject project = this.dataSetBuilder.getTrialDataset(studyId);
		final Integer environmentDatasetId = project.getProjectId();
		final DataSet dataSet = this.dataSetBuilder.build(environmentDatasetId);
		final Study study = this.getStudyBuilder().createStudy(dataSet.getStudyId());

		final VariableTypeList trialEnvironmentVariableTypes = this.getTrialInstanceVariableTypes(study, dataSet);
		final List<ExperimentModel> locations = this.daoFactory.getInstanceDao().getInstancesByDataset(datasetId, environmentDatasetId.equals(datasetId));

		return this.buildTrialInstances(locations, trialEnvironmentVariableTypes);
	}

	private VariableTypeList getTrialInstanceVariableTypes(final Study study, final DataSet dataSet) {
		final VariableTypeList trialInstanceVariableTypes = new VariableTypeList();
		trialInstanceVariableTypes.addAll(study.getVariableTypesByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT));
		trialInstanceVariableTypes.addAll(dataSet.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT));
		return trialInstanceVariableTypes;
	}

	private TrialInstances buildTrialInstances(final List<ExperimentModel> locations,
			final VariableTypeList trialInstanceVariableTypes) {

		final TrialInstances trialInstances = new TrialInstances();
		for (final ExperimentModel location : locations) {
			final VariableList variables = new VariableList();
			for (final DMSVariableType variableType : trialInstanceVariableTypes.getVariableTypes()) {
				final Variable variable = new Variable(variableType, this.getValue(location, variableType));
				variables.add(variable);
			}
			trialInstances.add(new TrialInstance(location.getNdExperimentId(), variables));
		}
		return trialInstances;
	}

	private String getValue(final ExperimentModel location, final DMSVariableType variableType) {
		String value = null;
		final int id = variableType.getStandardVariable().getId();
		if (id == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
			value = String.valueOf(location.getObservationUnitNo());
		} else {
			value = this.getPropertyValue(variableType.getId(), location.getProperties());
		}
		return value;
	}

	private String getPropertyValue(final int id, final List<ExperimentProperty> properties) {
		String value = null;
		if (properties != null) {
			for (final ExperimentProperty property : properties) {
				if (property.getTypeId() == id) {
					value = property.getValue();
					break;
				}
			}
		}
		return value;
	}

	public TrialInstances getAllTrialInstances() {
		final TrialInstances instances = new TrialInstances();
		instances.addAll(this.daoFactory.getInstanceDao().getAllTrialInstances());
		return instances;
	}

	public long countAllTrialInstances() {
		return this.daoFactory.getInstanceDao().countAllTrialInstances();
	}

	public List<TrialInstanceProperty> getPropertiesForTrialInstances(final List<Integer> instanceIds) {
		return this.daoFactory.getInstanceDao().getPropertiesForTrialInstances(instanceIds);
	}

	public List<GermplasmPair> getInstanceForGermplasmPairs(final List<GermplasmPair> germplasmPairs,
			final List<Integer> experimentTypes, final String programUUID) {
		final List<TrialInstance> trialInstances = new ArrayList<>();

		final Set<Integer> allGids = new HashSet<>();
		for (final GermplasmPair pair : germplasmPairs) {
			allGids.add(pair.getGid1());
			allGids.add(pair.getGid2());
		}

		// Step 1: Get Trial Instances for each GID
		final Map<Integer, Set<Integer>> germplasmInstances = this.getExperimentDao().getInstancesOfGermplasms(allGids, programUUID);

		// Step 2: Get the trial instance details
		final Set<TrialInstance> trialInstanceDetails = new HashSet<>();
		this.getTrialInstanceDetails(germplasmInstances, trialInstanceDetails);

		// Step 3: Get instance traits
		final List<TrialInstance> localTrialInstances =
				this.getPhenotypeDao().getInstanceTraits(trialInstanceDetails, experimentTypes);
		trialInstances.addAll(localTrialInstances);

		// Step 4: Build germplasm pairs. Get what's common between GID1 AND GID2
		this.buildGermplasmPairsBetweenGids(germplasmPairs, germplasmInstances, trialInstances);
		return germplasmPairs;
	}

	private void getTrialInstanceDetails(final Map<Integer, Set<Integer>> germplasmInstances,
			final Set<TrialInstance> trialInstanceDetails) {
		final Set<Integer> localInstanceIds = this.getInstanceIdsFromMap(germplasmInstances);
		trialInstanceDetails.addAll(this.daoFactory.getInstanceDao().getTrialInstanceDetails(localInstanceIds));
	}

	private void buildGermplasmPairsBetweenGids(final List<GermplasmPair> germplasmPairs,
			final Map<Integer, Set<Integer>> germplasmInstances, final List<TrialInstance> trialInstances) {
		for (final GermplasmPair pair : germplasmPairs) {
			final int gid1 = pair.getGid1();
			final int gid2 = pair.getGid2();

			final Set<Integer> g1Instances = germplasmInstances.get(gid1);
			final Set<Integer> g2Instances = germplasmInstances.get(gid2);

			final TrialInstances instances = new TrialInstances();
			if (g1Instances != null && g2Instances != null) {
				for (final Integer env1 : g1Instances) {
					for (final Integer env2 : g2Instances) {

						if (env1.equals(env2)) {
							final int index = trialInstances.indexOf(new TrialInstance(env1));
							if (index > -1) {
								final TrialInstance newEnv = trialInstances.get(index);
								// If the instance has no traits, do not include in the list of common instances
								if (newEnv != null && newEnv.getTraits() != null && !newEnv.getTraits().isEmpty()) {
									instances.add(newEnv);
								}
							}
						}
					}
				}
			}
			pair.setTrialInstances(instances);
		}
	}

	private Set<Integer> getInstanceIdsFromMap(final Map<Integer, Set<Integer>> germplasmInstances) {
		final Set<Integer> idsToReturn = new HashSet<>();

		for (final Entry<Integer, Set<Integer>> instanceIds : germplasmInstances.entrySet()) {
			final Set<Integer> ids = instanceIds.getValue();
			for (final Integer id : ids) {
				idsToReturn.add(id);
			}
		}
		return idsToReturn;

	}

	public TrialInstances getInstancesForTraits(final List<Integer> traitIds, final String programUUID) {
		final TrialInstances instances = new TrialInstances();
		instances.addAll(this.daoFactory.getInstanceDao().getInstancesForTraits(traitIds, programUUID));
		return instances;
	}

}
