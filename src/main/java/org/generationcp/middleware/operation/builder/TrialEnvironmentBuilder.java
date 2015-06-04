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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironmentProperty;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;

public class TrialEnvironmentBuilder extends Builder {

	public TrialEnvironmentBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public TrialEnvironments getTrialEnvironmentsInDataset(int studyId, int datasetId) throws MiddlewareQueryException {
		DmsProject project = this.getDataSetBuilder().getTrialDataset(studyId, datasetId);
		DataSet dataSet = this.getDataSetBuilder().build(project.getProjectId());
		Study study = this.getStudyBuilder().createStudy(dataSet.getStudyId());

		VariableTypeList trialEnvironmentVariableTypes = this.getTrialEnvironmentVariableTypes(study, dataSet);
		Set<Geolocation> locations = this.getGeoLocations(datasetId);

		return this.buildTrialEnvironments(locations, trialEnvironmentVariableTypes);
	}

	private VariableTypeList getTrialEnvironmentVariableTypes(Study study, DataSet dataSet) {
		VariableTypeList trialEnvironmentVariableTypes = new VariableTypeList();
		trialEnvironmentVariableTypes.addAll(study.getVariableTypesByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT));
		trialEnvironmentVariableTypes.addAll(dataSet.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT));
		return trialEnvironmentVariableTypes;
	}

	private Set<Geolocation> getGeoLocations(int datasetId) throws MiddlewareQueryException {
		return this.getGeolocationDao().findInDataSet(datasetId);
	}

	private TrialEnvironments buildTrialEnvironments(Set<Geolocation> locations, VariableTypeList trialEnvironmentVariableTypes) {

		TrialEnvironments trialEnvironments = new TrialEnvironments();
		for (Geolocation location : locations) {
			VariableList variables = new VariableList();
			for (VariableType variableType : trialEnvironmentVariableTypes.getVariableTypes()) {
				Variable variable = new Variable(variableType, this.getValue(location, variableType));
				variables.add(variable);
			}
			trialEnvironments.add(new TrialEnvironment(location.getLocationId(), variables));
		}
		return trialEnvironments;
	}

	private String getValue(Geolocation location, VariableType variableType) {
		String value = null;
		int storedInId = variableType.getStandardVariable().getStoredIn().getId();
		if (storedInId == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
			value = location.getDescription();
		} else if (storedInId == TermId.LATITUDE_STORAGE.getId()) {
			value = location.getLatitude() == null ? null : Double.toString(location.getLatitude());
		} else if (storedInId == TermId.LONGITUDE_STORAGE.getId()) {
			value = location.getLongitude() == null ? null : Double.toString(location.getLongitude());
		} else if (storedInId == TermId.DATUM_STORAGE.getId()) {
			value = location.getGeodeticDatum();
		} else if (storedInId == TermId.ALTITUDE_STORAGE.getId()) {
			value = location.getAltitude() == null ? null : Double.toString(location.getAltitude());
		} else if (storedInId == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
			value = this.getPropertyValue(variableType.getId(), location.getProperties());
		}
		return value;
	}

	private String getPropertyValue(int id, List<GeolocationProperty> properties) {
		String value = null;
		if (properties != null) {
			for (GeolocationProperty property : properties) {
				if (property.getTypeId() == id) {
					value = property.getValue();
					break;
				}
			}
		}
		return value;
	}

	public TrialEnvironments getAllTrialEnvironments(boolean includePublicData) throws MiddlewareQueryException {
		TrialEnvironments environments = new TrialEnvironments();
		environments.addAll(this.getGeolocationDao().getAllTrialEnvironments());
		return environments;
	}

	public long countAllTrialEnvironments() throws MiddlewareQueryException {
		return this.getGeolocationDao().countAllTrialEnvironments();
	}

	public List<TrialEnvironmentProperty> getPropertiesForTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException {
		return this.getGeolocationDao().getPropertiesForTrialEnvironments(environmentIds);
	}

	public List<GermplasmPair> getEnvironmentForGermplasmPairs(List<GermplasmPair> germplasmPairs) throws MiddlewareQueryException {
		List<TrialEnvironment> trialEnvironments = new ArrayList<TrialEnvironment>();

		Set<Integer> allGids = new HashSet<Integer>();
		for (GermplasmPair pair : germplasmPairs) {
			allGids.add(pair.getGid1());
			allGids.add(pair.getGid2());
		}

		// Step 1: Get Trial Environments for each GID
		Map<Integer, Set<Integer>> germplasmEnvironments = this.getExperimentStockDao().getEnvironmentsOfGermplasms(allGids);

		// Step 2: Get the trial environment details
		Set<Integer> localEnvironmentIds = this.getEnvironmentIdsFromMap(germplasmEnvironments);
		Set<TrialEnvironment> trialEnvironmentDetails = new HashSet<TrialEnvironment>();
		trialEnvironmentDetails.addAll(this.getGeolocationDao().getTrialEnvironmentDetails(localEnvironmentIds));

		// Step 3: Get environment traits
		List<TrialEnvironment> localTrialEnvironments = this.getPhenotypeDao().getEnvironmentTraits(trialEnvironmentDetails);
		trialEnvironments.addAll(localTrialEnvironments);

		// Step 4: Build germplasm pairs. Get what's common between GID1 AND GID2
		for (GermplasmPair pair : germplasmPairs) {
			int gid1 = pair.getGid1();
			int gid2 = pair.getGid2();

			Set<Integer> g1Environments = germplasmEnvironments.get(gid1);
			Set<Integer> g2Environments = germplasmEnvironments.get(gid2);

			TrialEnvironments environments = new TrialEnvironments();
			if (g1Environments != null && g2Environments != null) {
				for (Integer env1 : g1Environments) {
					for (Integer env2 : g2Environments) {

						if (env1.equals(env2)) {
							int index = trialEnvironments.indexOf(new TrialEnvironment(env1));
							if (index > -1) {
								TrialEnvironment newEnv = trialEnvironments.get(index);
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
		return germplasmPairs;
	}

	private Set<Integer> getEnvironmentIdsFromMap(Map<Integer, Set<Integer>> germplasmEnvironments) {
		Set<Integer> idsToReturn = new HashSet<Integer>();

		for (Entry<Integer, Set<Integer>> environmentIds : germplasmEnvironments.entrySet()) {
			Set<Integer> ids = environmentIds.getValue();
			for (Integer id : ids) {
				idsToReturn.add(id);
			}
		}
		return idsToReturn;

	}

	public TrialEnvironments getEnvironmentsForTraits(List<Integer> traitIds) throws MiddlewareQueryException {
		TrialEnvironments environments = new TrialEnvironments();
		environments.addAll(this.getGeolocationDao().getEnvironmentsForTraits(traitIds));
		return environments;
	}

}
