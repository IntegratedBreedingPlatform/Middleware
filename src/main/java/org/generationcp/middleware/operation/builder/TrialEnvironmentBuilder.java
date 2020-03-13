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
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
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

public class TrialEnvironmentBuilder extends Builder {

	@Resource
	private DataSetBuilder dataSetBuilder;

	private DaoFactory daoFactory;

	public TrialEnvironmentBuilder() {

	}

	public TrialEnvironmentBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public TrialEnvironments getTrialEnvironmentsInDataset(final int studyId, final int datasetId) {
		final DmsProject project = this.dataSetBuilder.getTrialDataset(studyId);
		final Integer environmentDatasetId = project.getProjectId();
		final DataSet dataSet = this.dataSetBuilder.build(environmentDatasetId);
		final Study study = this.getStudyBuilder().createStudy(dataSet.getStudyId());

		final VariableTypeList trialEnvironmentVariableTypes = this.getTrialEnvironmentVariableTypes(study, dataSet);
		final List<ExperimentModel> locations = this.daoFactory.getInstanceDao().getEnvironmentsByDataset(datasetId, environmentDatasetId.equals(datasetId));

		return this.buildTrialEnvironments(locations, trialEnvironmentVariableTypes);
	}

	private VariableTypeList getTrialEnvironmentVariableTypes(final Study study, final DataSet dataSet) {
		final VariableTypeList trialEnvironmentVariableTypes = new VariableTypeList();
		trialEnvironmentVariableTypes.addAll(study.getVariableTypesByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT));
		trialEnvironmentVariableTypes.addAll(dataSet.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT));
		return trialEnvironmentVariableTypes;
	}

	private TrialEnvironments buildTrialEnvironments(final List<ExperimentModel> locations,
			final VariableTypeList trialEnvironmentVariableTypes) {

		final TrialEnvironments trialEnvironments = new TrialEnvironments();
		for (final ExperimentModel location : locations) {
			final VariableList variables = new VariableList();
			for (final DMSVariableType variableType : trialEnvironmentVariableTypes.getVariableTypes()) {
				final Variable variable = new Variable(variableType, this.getValue(location, variableType));
				variables.add(variable);
			}
			trialEnvironments.add(new TrialEnvironment(location.getNdExperimentId(), variables));
		}
		return trialEnvironments;
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

	public TrialEnvironments getAllTrialEnvironments() {
		final TrialEnvironments environments = new TrialEnvironments();
		environments.addAll(this.daoFactory.getInstanceDao().getAllTrialEnvironments());
		return environments;
	}

	public long countAllTrialEnvironments() {
		return this.daoFactory.getInstanceDao().countAllTrialEnvironments();
	}

	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(final List<Integer> environmentIds) {
		return this.daoFactory.getInstanceDao().getPropertiesForTrialEnvironments(environmentIds);
	}

	public List<GermplasmPair> getEnvironmentForGermplasmPairs(final List<GermplasmPair> germplasmPairs,
			final List<Integer> experimentTypes, final String programUUID) {
		final List<TrialEnvironment> trialEnvironments = new ArrayList<>();

		final Set<Integer> allGids = new HashSet<>();
		for (final GermplasmPair pair : germplasmPairs) {
			allGids.add(pair.getGid1());
			allGids.add(pair.getGid2());
		}

		// Step 1: Get Trial Environments for each GID
		final Map<Integer, Set<Integer>> germplasmEnvironments = this.getExperimentDao().getEnvironmentsOfGermplasms(allGids, programUUID);

		// Step 2: Get the trial environment details
		final Set<TrialEnvironment> trialEnvironmentDetails = new HashSet<>();
		this.getTrialEnvironmentDetails(germplasmEnvironments, trialEnvironmentDetails);

		// Step 3: Get environment traits
		final List<TrialEnvironment> localTrialEnvironments =
				this.getPhenotypeDao().getEnvironmentTraits(trialEnvironmentDetails, experimentTypes);
		trialEnvironments.addAll(localTrialEnvironments);

		// Step 4: Build germplasm pairs. Get what's common between GID1 AND GID2
		this.buildGermplasmPairsBetweenGids(germplasmPairs, germplasmEnvironments, trialEnvironments);
		return germplasmPairs;
	}

	private void getTrialEnvironmentDetails(final Map<Integer, Set<Integer>> germplasmEnvironments,
			final Set<TrialEnvironment> trialEnvironmentDetails) {
		final Set<Integer> localEnvironmentIds = this.getEnvironmentIdsFromMap(germplasmEnvironments);
		trialEnvironmentDetails.addAll(this.daoFactory.getInstanceDao().getTrialEnvironmentDetails(localEnvironmentIds));
	}

	private void buildGermplasmPairsBetweenGids(final List<GermplasmPair> germplasmPairs,
			final Map<Integer, Set<Integer>> germplasmEnvironments, final List<TrialEnvironment> trialEnvironments) {
		for (final GermplasmPair pair : germplasmPairs) {
			final int gid1 = pair.getGid1();
			final int gid2 = pair.getGid2();

			final Set<Integer> g1Environments = germplasmEnvironments.get(gid1);
			final Set<Integer> g2Environments = germplasmEnvironments.get(gid2);

			final TrialEnvironments environments = new TrialEnvironments();
			if (g1Environments != null && g2Environments != null) {
				for (final Integer env1 : g1Environments) {
					for (final Integer env2 : g2Environments) {

						if (env1.equals(env2)) {
							final int index = trialEnvironments.indexOf(new TrialEnvironment(env1));
							if (index > -1) {
								final TrialEnvironment newEnv = trialEnvironments.get(index);
								// If the environment has no traits, do not include in the list of common environments
								if (newEnv != null && newEnv.getTraits() != null && !newEnv.getTraits().isEmpty()) {
									environments.add(newEnv);
								}
							}
						}
					}
				}
			}
			pair.setTrialEnvironments(environments);
		}
	}

	private Set<Integer> getEnvironmentIdsFromMap(final Map<Integer, Set<Integer>> germplasmEnvironments) {
		final Set<Integer> idsToReturn = new HashSet<>();

		for (final Entry<Integer, Set<Integer>> environmentIds : germplasmEnvironments.entrySet()) {
			final Set<Integer> ids = environmentIds.getValue();
			for (final Integer id : ids) {
				idsToReturn.add(id);
			}
		}
		return idsToReturn;

	}

	public TrialEnvironments getEnvironmentsForTraits(final List<Integer> traitIds, final String programUUID) {
		final TrialEnvironments environments = new TrialEnvironments();
		environments.addAll(this.daoFactory.getInstanceDao().getEnvironmentsForTraits(traitIds, programUUID));
		return environments;
	}

}
