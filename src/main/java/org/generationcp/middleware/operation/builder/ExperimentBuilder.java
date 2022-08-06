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

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.PedigreeDataManagerImpl;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExperimentBuilder extends Builder {

	private static final Logger LOG = LoggerFactory.getLogger(ExperimentBuilder.class);
	private DaoFactory daoFactory;

	private PedigreeService pedigreeService;

	private CrossExpansionProperties crossExpansionProperties;

	private PedigreeDataManager pedigreeDataManager;

	public ExperimentBuilder(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProviderForLocal);
		this.pedigreeDataManager = new PedigreeDataManagerImpl(sessionProviderForLocal);
		this.pedigreeService = this.getPedigreeService();
		this.crossExpansionProperties = new CrossExpansionProperties();
	}

	private PedigreeService getPedigreeService() {
		if (ManagerFactory.getCurrentManagerFactoryThreadLocal().get() != null) {
			return ManagerFactory.getCurrentManagerFactoryThreadLocal().get().getPedigreeService();
		}
		// we will just return default pedigree service
		return PedigreeFactory.getPedigreeService(this.sessionProvider, null, null);
	}

	public long count(final int dataSetId) {
		return this.daoFactory.getExperimentDao().count(dataSetId);
	}

	public List<Experiment> build(final int projectId, final TermId type, final int start, final int numOfRows,
		final VariableTypeList variableTypes) {
		return this.getExperiments(() -> this.daoFactory.getExperimentDao().getExperiments(projectId, type.getId(), start, numOfRows),
			variableTypes);
	}

	public List<Experiment> build(final int projectId, final TermId type, final int start, final int numOfRows,
		final VariableTypeList variableTypes,
		final boolean hasVariableType) {
		final List<ExperimentModel> experimentModels =
			this.daoFactory.getExperimentDao().getExperiments(projectId, type.getId(), start, numOfRows);
		final Map<Integer, Pair<String, String>> derivativeParentsMapByGids =
			this.getDerivativeParentsMapByGids(experimentModels, variableTypes);
		final com.google.common.collect.Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable =
			this.generatePedigreeTable(experimentModels, variableTypes);
		final MultiKeyMap attributeMapByGidsAndAttributeId = this.getAttributeMapByGidsAndAttributeId(experimentModels, variableTypes);
		return experimentModels.stream()
			.map(experimentModel -> this.createExperiment(experimentModel, variableTypes, hasVariableType, derivativeParentsMapByGids,
				pedigreeTreeNodeTable, attributeMapByGidsAndAttributeId))
			.collect(Collectors.toList());
	}

	private Map<Integer, StockModel> getStockModelMap(final List<ExperimentModel> experimentModels) {
		final Map<Integer, StockModel> stockModelMap = new HashMap<>();
		for (final ExperimentModel experimentModel : experimentModels) {
			final StockModel stock = experimentModel.getStock();
			if (stock != null) {
				final Integer stockId = stock.getStockId();
				stockModelMap.put(stockId, stock);
			}
		}
		return stockModelMap;
	}

	public List<Experiment> build(
		final int projectId, final List<TermId> types, final int start, final int numOfRows, final VariableTypeList variableTypes) {
		final Monitor monitor = MonitorFactory.start("Build Experiments");
		try {
			return this.getExperiments(() -> this.daoFactory.getExperimentDao().getExperiments(projectId, types, start, numOfRows, null, null),
				variableTypes);
		} finally {
			LOG.debug("" + monitor.stop());
		}
	}

	public List<Experiment> build(final int projectId, final List<TermId> types,
		final VariableTypeList variableTypes, final List<Integer> instanceNumbers, final List<Integer> repNumbers) {
		final Monitor monitor = MonitorFactory.start("Build Experiments");
		try {
			return this.getExperiments(() -> this.daoFactory.getExperimentDao().getExperiments(projectId, types, 0, Integer.MAX_VALUE, instanceNumbers, repNumbers),
				variableTypes);
		} finally {
			LOG.debug("" + monitor.stop());
		}
	}

	public Experiment buildOne(final int projectId, final TermId type, final VariableTypeList variableTypes) {
		final List<Experiment> experiments = this.build(projectId, type, 0, 1, variableTypes);
		if (experiments != null && !experiments.isEmpty()) {
			return experiments.get(0);
		}
		return null;
	}

	public Experiment buildOne(final int projectId, final TermId type, final VariableTypeList variableTypes,
		final boolean hasVariableType) {
		final List<Experiment> experiments = this.build(projectId, type, 0, 1, variableTypes, hasVariableType);
		if (experiments != null && !experiments.isEmpty()) {
			return experiments.get(0);
		}
		return null;
	}

	private List<Experiment> getExperiments(Supplier<List<ExperimentModel>> experimentModelsSupplier, final VariableTypeList variableTypes) {
		final List<ExperimentModel> experimentModels = experimentModelsSupplier.get();
		// to improve, we will get all the stocks already and saved it in a map and pass it as a parameter to avoid multiple query in DB
		final Map<Integer, StockModel> stockModelMap = this.getStockModelMap(experimentModels);
		final Map<Integer, Pair<String, String>> derivativeParentsMapByGids = this.getDerivativeParentsMapByGids(experimentModels, variableTypes);
		final com.google.common.collect.Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable =
			this.generatePedigreeTable(experimentModels, variableTypes);
		final MultiKeyMap attributeMapByGidsAndAttributeId = this.getAttributeMapByGidsAndAttributeId(experimentModels, variableTypes);
		return experimentModels.stream()
			.map(experimentModel -> this.createExperiment(experimentModel, variableTypes, stockModelMap, derivativeParentsMapByGids,
				pedigreeTreeNodeTable, attributeMapByGidsAndAttributeId))
			.collect(Collectors.toList());
	}

	private Table<Integer, String, Optional<Germplasm>> generatePedigreeTable(final List<ExperimentModel> experimentModels,
		final VariableTypeList variableTypes) {
		Table<Integer, String, Optional<Germplasm>> table = HashBasedTable.create();
		if (variableTypes.getVariableTypes().stream().anyMatch(this::entryVariablesHasParent)) {
			final Set<Integer> gids = this.getGidsFromExperiments(experimentModels);
			final Integer level = this.crossExpansionProperties.getCropGenerationLevel(this.pedigreeService.getCropName());
			table = this.pedigreeDataManager.generatePedigreeTable(gids, level, false);
		}
		return table;
	}

	private Map<Integer, Pair<String, String>> getDerivativeParentsMapByGids(final List<ExperimentModel> experimentModels, final VariableTypeList variableTypes) {
		if (variableTypes.findByLocalName(TermId.IMMEDIATE_SOURCE_NAME.name()) != null || variableTypes.findByLocalName(TermId.GROUP_SOURCE_NAME.name()) != null) {
			final Set<Integer> gids = this.getGidsFromExperiments(experimentModels);
			if (!CollectionUtils.isEmpty(gids)) {
				return this.getGermplasmService().getDerivativeParentsMapByGids(gids);
			}

		}
		return new HashMap<>();
	}

	private MultiKeyMap getAttributeMapByGidsAndAttributeId(final List<ExperimentModel> experimentModels, final VariableTypeList variableTypes) {
		final MultiKeyMap multiKeyMap = new MultiKeyMap();
		if (this.checkVariableTypeIsGermplasmPassportOrAttribute(variableTypes)) {
			final Set<Integer> gids = this.getGidsFromExperiments(experimentModels);
			if (!CollectionUtils.isEmpty(gids)) {
				this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(new ArrayList<>(gids))
					.forEach(attribute -> multiKeyMap.put(attribute.getGermplasmId(), attribute.getTypeId(), attribute.getAval()));
			}
		}
		return multiKeyMap;
	}

	private Experiment createExperiment(final ExperimentModel experimentModel, final VariableTypeList variableTypes,
		final Map<Integer, StockModel> stockModelMap, final Map<Integer, Pair<String, String>> derivativeParentsMapByGids,
		final com.google.common.collect.Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable,
		final MultiKeyMap attributeMapByGidsAndAttributeId) {
		final Experiment experiment = new Experiment();
		experiment.setId(experimentModel.getNdExperimentId());
		experiment.setFactors(this.getFactors(experimentModel, variableTypes, stockModelMap, derivativeParentsMapByGids,
			pedigreeTreeNodeTable, attributeMapByGidsAndAttributeId));
		experiment.setVariates(this.getVariates(experimentModel, variableTypes));
		experiment.setLocationId(experimentModel.getGeoLocation().getLocationId());
		experiment.setObsUnitId(experimentModel.getObsUnitId());
		return experiment;
	}

	private Experiment createExperiment(final ExperimentModel experimentModel, final VariableTypeList variableTypes,
		final boolean hasVariableType, final Map<Integer, Pair<String, String>> derivativeParentsMapByGids,
		final Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable, final MultiKeyMap attributeMapByGidsAndAttributeId) {
		final Experiment experiment = new Experiment();
		experiment.setId(experimentModel.getNdExperimentId());
		experiment.setFactors(this.getFactors(experimentModel, variableTypes, hasVariableType, derivativeParentsMapByGids,
			pedigreeTreeNodeTable, attributeMapByGidsAndAttributeId));
		experiment.setVariates(this.getVariates(experimentModel, variableTypes));
		experiment.setLocationId(experimentModel.getGeoLocation().getLocationId());
		return experiment;
	}

	private VariableList getVariates(final ExperimentModel experimentModel, final VariableTypeList variableTypes) {
		final VariableList variates = new VariableList();

		this.addPlotVariates(experimentModel, variates, variableTypes);

		return variates.sort();
	}

	private void addPlotVariates(final ExperimentModel experimentModel, final VariableList variates, final VariableTypeList variableTypes) {
		this.addVariates(experimentModel, variates, variableTypes);
	}

	private void addVariates(final ExperimentModel experiment, final VariableList variates, final VariableTypeList variableTypes) {
		if (experiment.getPhenotypes() != null) {
			for (final Phenotype phenotype : experiment.getPhenotypes()) {
				final DMSVariableType variableType = variableTypes.findById(phenotype.getObservableId());
				// TODO: trial constants are currently being saved in the measurement effect dataset
				// added this validation for now, to handle the said scenario, otherwise, and NPE is thrown
				// in the future, trial constant will no longer be saved at the measurements level
				if (variableType != null) {
					Variable var = null;
					if (variableType.getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
						var = new Variable(phenotype.getPhenotypeId(), variableType, phenotype.getcValueId());
						if (phenotype.getcValueId() == null && phenotype.getValue() != null) {
							var.setValue(phenotype.getValue());
							var.setCustomValue(true);
						}

						variates.add(var);
					} else {
						var = new Variable(phenotype.getPhenotypeId(), variableType, phenotype.getValue());
						variates.add(var);
					}
					var.getVariableType().setRole(PhenotypicType.VARIATE);
					var.getVariableType().getStandardVariable().setPhenotypicType(PhenotypicType.VARIATE);
				}
			}
		}
	}

	private VariableList getFactors(final ExperimentModel experimentModel, final VariableTypeList variableTypes,
		final Map<Integer, StockModel> stockModelMap, final Map<Integer, Pair<String, String>> derivativeParentsMapByGids,
		final Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable, final MultiKeyMap attributeMapByGidsAndAttributeId) {
		final VariableList factors = new VariableList();

		this.addPlotExperimentFactors(factors, experimentModel, variableTypes, stockModelMap, derivativeParentsMapByGids,
			pedigreeTreeNodeTable, attributeMapByGidsAndAttributeId);

		this.addLocationFactors(experimentModel, factors, variableTypes);

		return factors.sort();
	}

	private VariableList getFactors(final ExperimentModel experimentModel, final VariableTypeList variableTypes,
		final boolean hasVariableType, final Map<Integer, Pair<String, String>> derivativeParentsMapByGids,
		final Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable, final MultiKeyMap attributeMapByGidsAndAttributeId) {
		final VariableList factors = new VariableList();

		this.addPlotExperimentFactors(factors, experimentModel, variableTypes, hasVariableType, derivativeParentsMapByGids,
			pedigreeTreeNodeTable, attributeMapByGidsAndAttributeId);

		this.addLocationFactors(experimentModel, factors, variableTypes);

		return factors.sort();
	}

	private void addLocationFactors(final ExperimentModel experimentModel, final VariableList factors,
		final VariableTypeList variableTypes) {
		for (final DMSVariableType variableType : variableTypes.getVariableTypes()) {
			if (PhenotypicType.TRIAL_ENVIRONMENT == variableType.getRole()) {
				final Variable variable = this.createLocationFactor(experimentModel.getGeoLocation(), variableType);
				if (variable != null) {
					variable.getVariableType().setRole(PhenotypicType.TRIAL_ENVIRONMENT);
					variable.getVariableType().getStandardVariable().setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
					factors.add(variable);
				}
			}
		}
	}

	protected Variable createLocationFactor(final Geolocation geoLocation, final DMSVariableType variableType) {
		final StandardVariable standardVariable = variableType.getStandardVariable();

		if (standardVariable.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
			return new Variable(variableType, geoLocation.getDescription());
		}
		if (standardVariable.getId() == TermId.LATITUDE.getId()) {
			return new Variable(variableType, geoLocation.getLatitude());
		}
		if (standardVariable.getId() == TermId.LONGITUDE.getId()) {
			return new Variable(variableType, geoLocation.getLongitude());
		}
		if (standardVariable.getId() == TermId.GEODETIC_DATUM.getId()) {
			return new Variable(variableType, geoLocation.getGeodeticDatum());
		}
		if (standardVariable.getId() == TermId.ALTITUDE.getId()) {
			return new Variable(variableType, geoLocation.getAltitude());
		}

		final Optional<GeolocationProperty>
			geolocationPropertyOptional =
			Optional.ofNullable(geoLocation.getProperties()).map(Collection::stream)
				.orElseGet(Stream::empty).filter(o -> o.getTypeId().intValue() == variableType.getId()).findFirst();

		if (geolocationPropertyOptional.isPresent()) {
			return new Variable(geolocationPropertyOptional.get().getGeolocationPropertyId(), variableType,
				geolocationPropertyOptional.get().getValue());
		}

		return null;
	}

	private void addPlotExperimentFactors(final VariableList variables, final ExperimentModel experimentModel,
		final VariableTypeList variableTypes,
		final Map<Integer, StockModel> stockModelMap, final Map<Integer, Pair<String, String>> derivativeParentsMapByGids,
		final Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable, final MultiKeyMap attributeMapByGidsAndAttributeId) {
		this.addExperimentFactors(variables, experimentModel, variableTypes);
		this.addGermplasmFactors(variables, experimentModel, variableTypes, stockModelMap, derivativeParentsMapByGids, pedigreeTreeNodeTable,
			attributeMapByGidsAndAttributeId);
		this.addObsUnitIdFactor(variables, experimentModel, variableTypes);
	}

	private void addPlotExperimentFactors(final VariableList variables, final ExperimentModel experimentModel,
		final VariableTypeList variableTypes, final boolean hasVariableType,
		final Map<Integer, Pair<String, String>> derivativeParentsMapByGids,
		final Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable, final MultiKeyMap attributeMapByGidsAndAttributeId) {
		this.addExperimentFactors(variables, experimentModel, variableTypes, hasVariableType);
		this.addGermplasmFactors(variables, experimentModel, variableTypes, null, derivativeParentsMapByGids,
			pedigreeTreeNodeTable, attributeMapByGidsAndAttributeId);
	}

	private void addObsUnitIdFactor(final VariableList factors, final ExperimentModel experimentModel,
		final VariableTypeList variableTypes) {
		for (final DMSVariableType variableType : variableTypes.getVariableTypes()) {
			final StandardVariable standardVariable = variableType.getStandardVariable();
			if (standardVariable.getId() == TermId.OBS_UNIT_ID.getId()) {
				factors.add(new Variable(variableType, experimentModel.getObsUnitId()));
				return;
			}
		}
	}

	void addGermplasmFactors(final VariableList factors, final ExperimentModel experimentModel, final VariableTypeList variableTypes,
		final Map<Integer, StockModel> stockModelMap, final Map<Integer, Pair<String, String>> derivativeParentsMapByGids,
		final Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable, final MultiKeyMap attributeMapByGidsAndAttributeId) {
		StockModel stockModel = experimentModel.getStock();
		if (stockModel != null) {
			final Integer stockId = stockModel.getStockId();
			if (stockModelMap != null && stockModelMap.get(stockId) != null) {
				stockModel = stockModelMap.get(stockId);
			} else {
				stockModel = this.daoFactory.getStockDao().getById(stockId);
			}

			for (final DMSVariableType variableType : variableTypes.getVariableTypes()) {
				final Variable var = this.createGermplasmFactor(stockModel, variableType);
				if (var != null) {
					factors.add(var);
				}
			}

			final DMSVariableType groupGidVariableType = variableTypes.findById(TermId.GROUPGID);
			if (groupGidVariableType != null) {
				factors.add(new Variable(groupGidVariableType, stockModel.getGermplasm().getMgid()));
			}

			final DMSVariableType guidVariableType = variableTypes.findById(TermId.GUID);
			if (guidVariableType != null) {
				factors.add(new Variable(guidVariableType, stockModel.getGermplasm().getGermplasmUUID()));
			}

			final DMSVariableType crossVariableType = variableTypes.findById(TermId.CROSS);
			if (crossVariableType != null) {
				factors.add(new Variable(crossVariableType, stockModel.getCross()));
			}

			final DMSVariableType immediateSourceNameVariableType = variableTypes.findById(TermId.IMMEDIATE_SOURCE_NAME);
			if (immediateSourceNameVariableType != null) {
				final String immediateSourceName =
					derivativeParentsMapByGids.get(stockModel.getGermplasm().getGid()).getRight();
				factors.add(new Variable(immediateSourceNameVariableType, immediateSourceName));
			}

			final DMSVariableType groupSourceNameVariableType = variableTypes.findById(TermId.GROUP_SOURCE_NAME);
			if (groupSourceNameVariableType != null) {
				final String groupSourceName =
					derivativeParentsMapByGids.get(stockModel.getGermplasm().getGid()).getLeft();
				factors.add(new Variable(groupSourceNameVariableType, groupSourceName));
			}

			if (variableTypes.getVariableTypes().stream().anyMatch(this::entryVariablesHasParent)) {
				this.generatePedigreeTable(pedigreeTreeNodeTable, stockModel, variableTypes, factors);
			}

			if (this.checkVariableTypeIsGermplasmPassportOrAttribute(variableTypes)) {
				final StockModel finalStockModel = stockModel;
				variableTypes.getVariableTypes().stream().filter(variableType -> variableType.getVariableType() == VariableType.GERMPLASM_ATTRIBUTE ||
					variableType.getVariableType() == VariableType.GERMPLASM_PASSPORT)
					.forEach(variableType -> {
						final String attributeValue = (String) attributeMapByGidsAndAttributeId.get(finalStockModel.getGermplasm().getGid(), variableType.getStandardVariable().getId());
						factors.add(new Variable(variableType, attributeValue));
					});
			}
		}
	}

	private void generatePedigreeTable(
		final com.google.common.collect.Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable,
		final StockModel stockModel, final VariableTypeList variableTypes,
		final VariableList factors) {

		final Integer gid = stockModel.getGermplasm().getGid();
		final DMSVariableType femaleParentNameVariableType = variableTypes.findById(TermId.FEMALE_PARENT_NAME);
		final DMSVariableType femaleParentGidVariableType = variableTypes.findById(TermId.FEMALE_PARENT_GID);
		final DMSVariableType maleParentNameVariableType = variableTypes.findById(TermId.MALE_PARENT_NAME);
		final DMSVariableType maleParentGidVariableType = variableTypes.findById(TermId.MALE_PARENT_GID);

		final Optional<Germplasm> femaleParent = pedigreeTreeNodeTable.get(gid, ColumnLabels.FGID.getName());
		femaleParent.ifPresent(value -> {
			final Germplasm germplasm = value;
			if (femaleParentNameVariableType != null) {
				factors.add(new Variable(femaleParentNameVariableType, germplasm.getPreferredName().getNval()));
			}
			if (femaleParentGidVariableType != null) {
				factors.add(new Variable(femaleParentGidVariableType,
					germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN));
			}

		});

		final Optional<Germplasm> maleParent = pedigreeTreeNodeTable.get(gid, ColumnLabels.MGID.getName());
		if (maleParent.isPresent()) {
			final Germplasm germplasm = maleParent.get();

			if (maleParentGidVariableType != null) {
				factors.add(
					new Variable(maleParentGidVariableType, germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN));
			}

			if (maleParentNameVariableType != null) {
				factors.add(
					new Variable(maleParentNameVariableType, germplasm.getPreferredName().getNval()));
			}

		}

	}

	private boolean entryVariablesHasParent(final DMSVariableType variableType) {
		return variableType.getId() == TermId.FEMALE_PARENT_GID.getId() ||
			variableType.getId() == TermId.FEMALE_PARENT_NAME.getId()||
			variableType.getId() ==  TermId.MALE_PARENT_GID.getId() ||
			variableType.getId() ==  TermId.MALE_PARENT_NAME.getId();
	}

	protected Variable createGermplasmFactor(final StockModel stockModel, final DMSVariableType variableType) {
		final StandardVariable standardVariable = variableType.getStandardVariable();

		if (standardVariable.getId() == TermId.ENTRY_NO.getId()) {
			return new Variable(variableType, stockModel.getUniqueName());
		}
		if (standardVariable.getId() == TermId.GID.getId()) {
			return new Variable(variableType, stockModel.getGermplasm().getGid());
		}
		if (standardVariable.getId() == TermId.DESIG.getId()) {
			return new Variable(variableType, stockModel.getName());
		}
		final String val = this.findStockValue(variableType.getId(), stockModel.getProperties());

		if (standardVariable.isCategorical()) {
			return new Variable(variableType, Strings.nullToEmpty(val));
		}

		if (val != null) {
			return new Variable(variableType, val);
		}

		return null;
	}

	private String findStockValue(final int stdVariableId, final Set<StockProperty> properties) {
		if (properties != null) {
			for (final StockProperty property : properties) {
				if (stdVariableId == property.getTypeId()) {
					return property.getVariableValue();
				}
			}
		}
		return null;
	}

	private void addExperimentFactors(final VariableList variables, final ExperimentModel experimentModel,
		final VariableTypeList variableTypes) {
		if (experimentModel.getProperties() != null) {
			for (final ExperimentProperty property : experimentModel.getProperties()) {
				// Exclude the BLOCK_ID experiment property because it is not expected to included in the dataset variable list.
				if (!property.getTypeId().equals(TermId.BLOCK_ID.getId())) {
					variables.add(this.createVariable(property, variableTypes, PhenotypicType.TRIAL_DESIGN));
				}
			}
		}
	}

	private void addExperimentFactors(final VariableList variables, final ExperimentModel experimentModel,
		final VariableTypeList variableTypes,
		final boolean hasVariableType) {
		if (experimentModel.getProperties() != null) {
			for (final ExperimentProperty property : experimentModel.getProperties()) {
				// Exclude the BLOCK_ID experiment property because it is not expected to included in the dataset variable list.
				if (!property.getTypeId().equals(TermId.BLOCK_ID.getId())) {
					final Variable var = this.createVariable(property, variableTypes, hasVariableType, PhenotypicType.TRIAL_DESIGN);
					if (var.getVariableType() != null) {
						variables.add(var);
					}
				}
			}
		}
	}

	protected Variable createVariable(final ExperimentProperty property, final VariableTypeList variableTypes, final PhenotypicType role) {
		final Variable variable = new Variable();
		variable.setVariableType(variableTypes.findById(property.getTypeId()));
		variable.setValue(property.getValue());
		variable.getVariableType().setRole(role);
		variable.getVariableType().getStandardVariable().setPhenotypicType(role);
		return variable;
	}

	protected Variable createVariable(final ExperimentProperty property, final VariableTypeList variableTypes,
		final boolean hasVariableType, final PhenotypicType role) {
		final Variable variable = new Variable();
		variable.setVariableType(variableTypes.findById(property.getTypeId()), hasVariableType);
		variable.setValue(property.getValue());
		variable.getVariableType().setRole(role);
		return variable;
	}

	public boolean hasFieldmap(final int datasetId) {
		return this.daoFactory.getExperimentDao().hasFieldmap(datasetId);
	}

	private Set<Integer> getGidsFromExperiments(final List<ExperimentModel> experimentModels) {
		return experimentModels.stream().map(experimentModel -> experimentModel.getStock().getGermplasm().getGid())
			.collect(Collectors.toSet());
	}

	private boolean checkVariableTypeIsGermplasmPassportOrAttribute(final VariableTypeList variableTypes) {
		return variableTypes.getVariableTypes()
			.stream()
			.anyMatch(variableType -> variableType.getVariableType() == VariableType.GERMPLASM_ATTRIBUTE ||
				variableType.getVariableType() == VariableType.GERMPLASM_PASSPORT);
	}

}
