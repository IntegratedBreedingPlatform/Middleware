
package org.generationcp.middleware.service.impl.study;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.MultiKeyMap;
import org.generationcp.middleware.api.germplasmlist.GermplasmListService;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyEntryPropertyBatchUpdateRequest;
import org.generationcp.middleware.domain.study.StudyEntrySearchDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyEntryColumnDTO;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Transactional
public class StudyEntryServiceImpl implements StudyEntryService {

	@Resource
	private DatasetService datasetService;

	@Resource
	private OntologyDataManager ontologyDataManager;

	@Resource
	private GermplasmListService germplasmListService;

	@Resource
	private PedigreeService pedigreeService;

	@Resource
	private CrossExpansionProperties crossExpansionProperties;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Resource
	private PedigreeDataManager pedigreeDataManager;

	private final DaoFactory daoFactory;

	public StudyEntryServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StudyEntryDto> getStudyEntries(final int studyId) {
		// Get entry number term. Name will be used for sorting
		final Term entryNumberTerm = this.ontologyDataManager.getTermById(TermId.ENTRY_NO.getId());
		return this.getStudyEntries(studyId, null, new PageRequest(0, Integer.MAX_VALUE,
			new Sort(Sort.Direction.ASC, entryNumberTerm.getName())));
	}

	@Override
	public long countFilteredStudyEntries(final int studyId, final StudyEntrySearchDto.Filter filter) {
		final List<MeasurementVariable> entryVariables = this.getStudyVariables(studyId);
		if(filter != null){
			this.addPreFilteredGids(filter);
		}

		return this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(new StudyEntrySearchDto(studyId, filter), entryVariables);
	}

	private void addPreFilteredGids(final StudyEntrySearchDto.Filter filter) {
		Set<String> textKeys = filter.getFilteredTextValues().keySet();
		if(textKeys.contains(String.valueOf(TermId.FEMALE_PARENT_GID.getId())) ||
			textKeys.contains(String.valueOf(TermId.FEMALE_PARENT_NAME.getId())) ||
			textKeys.contains(String.valueOf(TermId.MALE_PARENT_GID.getId())) ||
			textKeys.contains(String.valueOf(TermId.MALE_PARENT_NAME.getId()))
		){
			filter.setPreFilteredGids(this.daoFactory.getStudyEntrySearchDAO().addPreFilteredGids(filter));
		}

	}

	@Override
	public List<StudyEntryDto> getStudyEntries(final int studyId, final StudyEntrySearchDto.Filter filter, final Pageable pageable) {
		final List<MeasurementVariable> entryVariables = this.getStudyVariables(studyId);

		// FIXME: It was implemented pre-filters to FEMALE and MALE Parents by NAME or GID.
		//  Is need a workaround solution to implement filters into the query if possible.
		if (filter != null) {
			this.addPreFilteredGids(filter);
		}

		final List<StudyEntryDto> studyEntries =
			this.daoFactory.getStudyEntrySearchDAO()
				.getStudyEntries(new StudyEntrySearchDto(studyId, filter), entryVariables, pageable);

		if (entryVariables.stream().anyMatch(this::entryVariablesHasParent)) {
			final Set<Integer> gids = studyEntries.stream().map(s -> s.getGid()).collect(Collectors.toSet());
			this.addParentsFromPedigreeTable(gids, studyEntries);
		}

		return studyEntries;
	}

	@Override
	public Integer getNextEntryNumber(final Integer studyId) {
		return this.daoFactory.getStockDao().getNextEntryNumber(studyId);
	}

	@Override
	public Map<Integer, StudyEntryDto> getPlotEntriesMap(final int studyBusinessIdentifier, final Set<Integer> plotNos) {
		return this.daoFactory.getStockDao().getPlotEntriesMap(studyBusinessIdentifier, plotNos);
	}

	@Override
	public long countStudyEntries(final int studyId) {
		return this.daoFactory.getStockDao().countStocksForStudy(studyId);
	}

	@Override
	public void deleteStudyEntries(final int studyId) {
		this.daoFactory.getStockDao().deleteStocksForStudy(studyId);

		final DmsProject plotDataDataset = this.getPlotDataset(studyId);
		final List<Integer> variableIds = plotDataDataset.getProperties().stream()
			.filter(projectProperty -> VariableType.ENTRY_DETAIL.getId().equals(projectProperty.getTypeId()) && (
				!projectProperty.getVariableId().equals(TermId.ENTRY_TYPE.getId()) &&
					!projectProperty.getVariableId().equals(TermId.ENTRY_NO.getId())))
			.map(ProjectProperty::getVariableId)
			.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(variableIds)) {
			this.daoFactory.getProjectPropertyDAO().deleteProjectVariables(plotDataDataset.getProjectId(), variableIds);
		}
	}

	@Override
	public void saveStudyEntries(final Integer studyId, final Integer listId) {
		final DmsProject plotDataDataset = this.getPlotDataset(studyId);

		// Filter entry details project variable except ENTRY_TYPE and ENTRY_NO because they are going to be added later
		final List<ProjectProperty> projectProperties = plotDataDataset.getProperties()
			.stream()
			.filter(projectProperty -> projectProperty.getVariableId().equals(TermId.ENTRY_TYPE.getId()) ||
				projectProperty.getVariableId().equals(TermId.ENTRY_NO.getId()) ||
				!VariableType.ENTRY_DETAIL.getId().equals(projectProperty.getTypeId()))
			.collect(Collectors.toList());

		this.setStudyGenerationLevel(listId, studyId);

		// Add germplasm list entry details as project properties
		final AtomicInteger projectPropertyInitialRank = new AtomicInteger(plotDataDataset.getNextPropertyRank());
		final List<Variable> germplasmListVariables =
			this.germplasmListService.getGermplasmListVariables(null, listId, VariableType.ENTRY_DETAIL.getId());
		final List<ProjectProperty> entryDetailsProjectProperties = germplasmListVariables.stream()
			.filter(variable -> TermId.ENTRY_TYPE.getId() != variable.getId() && TermId.ENTRY_NO.getId() != variable.getId())
			.map(variable -> new ProjectProperty(plotDataDataset, VariableType.ENTRY_DETAIL.getId(), null,
				projectPropertyInitialRank.getAndIncrement(), variable.getId(), variable.getName()))
			.collect(Collectors.toList());
		projectProperties.addAll(entryDetailsProjectProperties);

		plotDataDataset.setProperties(projectProperties);
		this.daoFactory.getDmsProjectDAO().save(plotDataDataset);

		this.daoFactory.getStockDao().createStudyEntries(studyId, listId);
	}

	@Override
	public void saveStudyEntries(final Integer studyId, final List<Integer> gids, final Integer entryTypeId) {
		final Term entryType = this.ontologyDataManager.getTermById(entryTypeId);
		final Integer nextEntryNumber = this.getNextEntryNumber(studyId);
		this.daoFactory.getStockDao().createStudyEntries(studyId, nextEntryNumber, gids, entryType.getId(), entryType.getName());

		final Integer crossGenerationLevel = this.getCrossGenerationLevel(studyId);
		final List<StockModel> entries =
			this.daoFactory.getStockDao().getStocksByStudyAndEntryNumbersGreaterThanEqual(studyId, nextEntryNumber);
		this.setCrossValues(entries, new HashSet<>(gids), crossGenerationLevel);
	}

	@Override
	public long countStudyGermplasmByEntryTypeIds(final int studyId, final List<Integer> systemDefinedEntryTypeIds) {
		return this.daoFactory.getStockDao().countStocksByStudyAndEntryTypeIds(studyId, systemDefinedEntryTypeIds);
	}

	@Override
	public void replaceStudyEntry(final int studyId, final int entryId, final int gid) {
		// Check first that new GID is valid
		final Germplasm newGermplasm = this.daoFactory.getGermplasmDao().getById(gid);
		if (newGermplasm == null) {
			throw new MiddlewareException("Invalid GID: " + gid);
		}
		final StockDao stockDao = this.daoFactory.getStockDao();
		final StockModel stock = stockDao.getById(entryId);
		if (stock == null) {
			throw new MiddlewareRequestException("", "invalid.entryid");
		} else if (studyId != stock.getProject().getProjectId()) {
			throw new MiddlewareRequestException("", "invalid.entryid.for.study");
		} else if (stock.getGermplasm().getGid() == gid) {
			throw new MiddlewareRequestException("", "new.gid.matches.old.gid");
		}

		final Integer crossGenerationLevel = this.getCrossGenerationLevel(studyId);
		final String crossExpansion = this.pedigreeService.getCrossExpansion(gid, crossGenerationLevel, this.crossExpansionProperties);
		this.replaceStocks(Lists.newArrayList(stock), gid, crossExpansion);
	}

	@Override
	public void replaceStudyEntries(final List<Integer> gidsToReplace, final Integer replaceWithGid, final String crossExpansion) {
		final List<StockModel> stocksToReplace = this.daoFactory.getStockDao().getStocksByGids(gidsToReplace);
		this.replaceStocks(stocksToReplace, replaceWithGid, crossExpansion);
	}

	private void replaceStocks(final List<StockModel> stocksToReplace, final Integer replaceWithGid,
		final String crossExpansion) {

		final GermplasmDto germplasm = this.daoFactory.getGermplasmDao().getGermplasmDtoByGid(replaceWithGid);
		final StockDao stockDao = this.daoFactory.getStockDao();
		for (final StockModel stockToReplace : stocksToReplace) {
			// Copy from old entry: entry #, entry code
			final StudyEntryDto studyEntryDto = new StudyEntryDto();
			studyEntryDto.setEntryNumber(Integer.valueOf(stockToReplace.getUniqueName()));
			studyEntryDto.setGid(germplasm.getGid());
			studyEntryDto.setDesignation(germplasm.getPreferredName());
			studyEntryDto.setCross(crossExpansion);

			// If germplasm descriptors exist for previous entry, copy ENTRY_TYPE value and set cross expansion and MGID of new germplasm
			this.addStudyEntryPropertyDataIfApplicable(stockToReplace, studyEntryDto, TermId.ENTRY_TYPE.getId(), Optional.empty());
			this.addStudyEntryPropertyDataIfApplicable(stockToReplace, studyEntryDto, TermId.GROUPGID.getId(),
				Optional.of(String.valueOf(germplasm.getGroupId())));

			final StockModel savedStock =
				stockDao.save(new StockModel(stockToReplace.getProject().getProjectId(), studyEntryDto));
			stockDao.replaceExperimentStocks(stockToReplace.getStockId(), savedStock.getStockId());
			stockDao.makeTransient(stockToReplace);
		}
	}

	private void addStudyEntryPropertyDataIfApplicable(final StockModel stock, final StudyEntryDto studyEntryDto, final Integer variableId,
		final Optional<String> value) {
		final Optional<StockProperty> entryType =
			stock.getProperties().stream().filter(prop -> variableId.equals(prop.getTypeId())).findFirst();
		entryType.ifPresent(stockProperty -> studyEntryDto.getProperties().put(variableId,
			new StudyEntryPropertyData(null, stockProperty.getTypeId(), value.isPresent() ? value.get() : stockProperty.getValue(),
				stockProperty.getCategoricalValueId()))
		);
	}

	@Override
	public void updateStudyEntriesProperty(final StudyEntryPropertyBatchUpdateRequest studyEntryPropertyBatchUpdateRequest) {
		final Term entryType = this.ontologyDataManager.getTermById(Integer.valueOf(studyEntryPropertyBatchUpdateRequest.getValue()));

		this.daoFactory.getStockPropertyDao().updateByStockIdsAndTypeId(
			new ArrayList<>(studyEntryPropertyBatchUpdateRequest.getSearchComposite().getItemIds()),
			studyEntryPropertyBatchUpdateRequest.getVariableId(), studyEntryPropertyBatchUpdateRequest.getValue(), entryType.getName());
	}

	@Override
	public Boolean hasUnassignedEntries(final int studyId) {
		return this.daoFactory.getStockDao().hasUnassignedEntries(studyId);
	}

	@Override
	public Optional<StockProperty> getByStockIdAndTypeId(final Integer stockId, final Integer typeId) {
		return this.daoFactory.getStockPropertyDao().getByStockIdAndTypeId(stockId, typeId);
	}

	@Override
	public StockProperty getByStockPropertyId(final Integer stockPropertyId) {
		return this.daoFactory.getStockPropertyDao().getById(stockPropertyId);
	}

	@Override
	public void fillWithCrossExpansion(final Integer studyId, final Integer level) {
		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		study.setGenerationLevel(level);
		this.daoFactory.getDmsProjectDAO().save(study);

		final List<StockModel> entries = this.daoFactory.getStockDao().getStocksForStudy(studyId);
		final Set<Integer> gids = entries.stream().map(stockModel -> stockModel.getGermplasm().getGid()).collect(toSet());
		this.setCrossValues(entries, gids, level);
	}

	@Override
	public Integer getCrossGenerationLevel(final Integer studyId) {
		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		return study.getGenerationLevel();
	}

	@Override
	public List<StudyEntryColumnDTO> getStudyEntryColumns(final Integer studyId, final String programUUID) {
		final DmsProject plotDataset = this.getPlotDataset(studyId);

		final List<Integer> projectVariableIds = plotDataset.getProperties().stream()
			.map(ProjectProperty::getVariableId)
			.collect(Collectors.toList());
		final List<StudyEntryColumnDTO> columns = StudyEntryGermplasmDescriptorColumns.getColumnsSortedByRank()
			.map(column -> new StudyEntryColumnDTO(column.getId(),
				column.getName(),
				null,
				VariableType.GERMPLASM_DESCRIPTOR.getId(),
				projectVariableIds.contains(column.getId())))
			.collect(Collectors.toList());

		final List<Integer> namTypeIds = plotDataset.getProperties().stream()
			.filter(projectProperty -> projectProperty.getTypeId() == null  &&
				projectProperty.getVariableId() == null  &&
				projectProperty.getNameFldno() != null)
			.map(ProjectProperty::getNameFldno)
			.collect(Collectors.toList());

		final List<StockModel> entries = this.daoFactory.getStockDao().getStocksForStudy(studyId);
		final List<Integer> gids = entries.stream().map(stockModel -> stockModel.getGermplasm().getGid()).collect(toList());
		final List<Attribute> attributes = this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(gids);
		final List<UserDefinedField> nameTypes = this.daoFactory.getUserDefinedFieldDAO().getNameTypesByGIDList(gids);
		if (!CollectionUtils.isEmpty(attributes)) {
			final VariableFilter variableFilter = new VariableFilter();
			variableFilter.setProgramUuid(programUUID);
			attributes
				.stream()
				.map(Attribute::getTypeId)
				.forEach(variableFilter::addVariableId);
			final List<Variable> variables = this.ontologyVariableDataManager.getWithFilter(variableFilter);
			final List<StudyEntryColumnDTO> germplasmAttributeColumns = variables
				.stream()
				.sorted(Comparator.comparing(Variable::getName))
				.map(variable -> {
					Integer typeId = null;
					// get first value because germplasm attributes/passport are not combinables with other types
					if (!org.springframework.util.CollectionUtils.isEmpty(variable.getVariableTypes())) {
						typeId = variable.getVariableTypes().iterator().next().getId();
					}
					return new StudyEntryColumnDTO(variable.getId(), variable.getName(), variable.getAlias(), typeId,
						projectVariableIds.contains(variable.getId()));
				})
				.collect(toList());
			columns.addAll(germplasmAttributeColumns);
		}

		if (!CollectionUtils.isEmpty(nameTypes)) {
			final List<StudyEntryColumnDTO> nameColumns =
				nameTypes.stream()
					.map(nameType ->
						new StudyEntryColumnDTO(nameType.getFldno(), nameType.getFcode(), null, null, namTypeIds.contains(nameType.getFldno())))
					.collect(toList());
			columns.addAll(nameColumns);
		}

		return columns;
	}

	private List<MeasurementVariable> getStudyVariables(final Integer studyId) {
		final Integer plotDatasetId =
			this.datasetService.getDatasets(studyId, new HashSet<>(Collections.singletonList(DatasetTypeEnum.PLOT_DATA.getId()))).get(0)
				.getDatasetId();

		final List<MeasurementVariable> variables =
			this.datasetService.getObservationSetVariables(plotDatasetId,
				Lists.newArrayList(VariableType.GERMPLASM_ATTRIBUTE.getId(),
					VariableType.GERMPLASM_PASSPORT.getId(),
					VariableType.GERMPLASM_DESCRIPTOR.getId(),
					VariableType.ENTRY_DETAIL.getId()));
		variables.removeIf(variable -> variable.getTermId() == TermId.OBS_UNIT_ID.getId());

		final List<MeasurementVariable> nameTypes = this.datasetService.getNameTypes(studyId, plotDatasetId);
		variables.addAll(nameTypes);
		return variables;
	}

	private void setCrossValues(final List<StockModel> entries, final Set<Integer> gids, final Integer level) {
		final Map<Integer, String> pedigreeStringMap =
			this.pedigreeService.getCrossExpansionsBulk(gids, level, this.crossExpansionProperties);
		entries.forEach(entry -> {
			entry.setCross(pedigreeStringMap.get(entry.getGermplasm().getGid()));
			entry.truncateCrossValueIfNeeded();
			this.daoFactory.getStockDao().save(entry);
		});
	}

	private DmsProject getPlotDataset(final Integer studyId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.PLOT_DATA.getId()).get(0);
	}

	private void setStudyGenerationLevel(final Integer listId, final Integer studyId) {
		final Optional<GermplasmList> germplasmListOptional = this.germplasmListService.getGermplasmListById(listId);
		germplasmListOptional.ifPresent(germplasmList -> {
			final Integer generationLevel = germplasmList.getGenerationLevel();
			final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
			study.setGenerationLevel(generationLevel);
			this.daoFactory.getDmsProjectDAO().save(study);
		});
	}

	@Override
	public List<Variable> getStudyEntryDetails(final String cropName, final String programUUID,
		final Integer studyId, final Integer variableTypeId) {

		final Integer plotDatasetId =
			this.datasetService.getDatasets(studyId, new HashSet<>(Collections.singletonList(DatasetTypeEnum.PLOT_DATA.getId()))).get(0)
				.getDatasetId();

		final List<MeasurementVariable> entryVariables =
			this.datasetService.getObservationSetVariables(plotDatasetId,
				Lists.newArrayList(variableTypeId));

		final List<Integer> variableIds = entryVariables.stream().filter(
				c -> (c.getVariableType().getId().equals(variableTypeId)
					|| variableTypeId == null)).map(MeasurementVariable::getTermId)
			.collect(Collectors.toList());
		return this.ontologyVariableDataManager.getVariablesByIds(variableIds, programUUID);
	}

	@Override
	public MultiKeyMap getStudyEntryStockPropertyMap(final Integer studyId, final List<StudyEntryDto> studyEntries) {
		final List<Integer> stockIds = studyEntries.stream().map(StudyEntryDto::getEntryId).collect(Collectors.toList());
		return this.daoFactory.getStockPropertyDao().getStockPropertiesMap(stockIds);
	}
	private void addParentsFromPedigreeTable(final Set<Integer> gids, final List<StudyEntryDto>  studyEntries) {
		final Integer level = this.crossExpansionProperties.getCropGenerationLevel(this.pedigreeService.getCropName());
		final com.google.common.collect.Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable =
			this.pedigreeDataManager.generatePedigreeTable(gids, level, false);

		studyEntries.forEach(studyEntry -> {
			final Integer gid = studyEntry.getGid();

			final Optional<Germplasm> femaleParent = pedigreeTreeNodeTable.get(gid, ColumnLabels.FGID.getName());
			femaleParent.ifPresent(value -> {
				final Germplasm germplasm = value;
				studyEntry.getProperties().put(
					TermId.FEMALE_PARENT_GID.getId(),
					new StudyEntryPropertyData(germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN));
				studyEntry.getProperties().put(
					TermId.FEMALE_PARENT_NAME.getId(),
					new StudyEntryPropertyData(germplasm.getPreferredName().getNval()));
			});

			final Optional<Germplasm> maleParent = pedigreeTreeNodeTable.get(gid, ColumnLabels.MGID.getName());
			if (maleParent.isPresent()) {
				final Germplasm germplasm = maleParent.get();
				studyEntry.getProperties().put(
					TermId.MALE_PARENT_GID.getId(),
					new StudyEntryPropertyData(germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN));
				studyEntry.getProperties().put(
					TermId.MALE_PARENT_NAME.getId(),
					new StudyEntryPropertyData(germplasm.getPreferredName().getNval()));
			}
		});
	}

	private boolean entryVariablesHasParent(final MeasurementVariable measurementVariable) {
		return measurementVariable.getTermId() == TermId.FEMALE_PARENT_GID.getId() ||
			measurementVariable.getTermId() == TermId.FEMALE_PARENT_NAME.getId()||
			measurementVariable.getTermId() ==  TermId.MALE_PARENT_GID.getId() ||
			measurementVariable.getTermId() ==  TermId.MALE_PARENT_NAME.getId();
	}
}
