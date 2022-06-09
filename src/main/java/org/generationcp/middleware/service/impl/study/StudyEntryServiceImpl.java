
package org.generationcp.middleware.service.impl.study;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.api.germplasmlist.GermplasmListService;
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
import org.generationcp.middleware.pojos.Germplasm;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

	private final DaoFactory daoFactory;

	// TODO: remove ENTRY_NO
	private static final List<Integer> FIXED_GERMPLASM_DESCRIPTOR_IDS = Lists
		.newArrayList(TermId.DESIG.getId(), TermId.ENTRY_NO.getId(), TermId.GID.getId(), TermId.IMMEDIATE_SOURCE_NAME.getId());

	private static final List<Integer> REMOVABLE_GERMPLASM_DESCRIPTOR_IDS = Lists
		.newArrayList(TermId.DESIG.getId(), TermId.ENTRY_NO.getId(), TermId.GID.getId(), TermId.OBS_UNIT_ID.getId(), TermId.CROSS.getId(), TermId.IMMEDIATE_SOURCE_NAME.getId());

	public StudyEntryServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StudyEntryDto> getStudyEntries(final int studyId) {
		// Get entry number term. Name will be used for sorting
		final Term entryNumberTerm = this.ontologyDataManager.getTermById(Integer.valueOf(TermId.ENTRY_NO.getId()));
		return this.getStudyEntries(studyId, null, new PageRequest(0, Integer.MAX_VALUE,
			new Sort(Sort.Direction.ASC, entryNumberTerm.getName())));
	}

	@Override
	public long countFilteredStudyEntries(int studyId, StudyEntrySearchDto.Filter filter) {
		return this.daoFactory.getStudyEntrySearchDAO().countFilteredStudyEntries(studyId, filter);
	}

	@Override
	public List<StudyEntryDto> getStudyEntries(final int studyId, final StudyEntrySearchDto.Filter filter, final Pageable pageable) {

		final Integer plotDatasetId =
			this.datasetService.getDatasets(studyId, new HashSet<>(Collections.singletonList(DatasetTypeEnum.PLOT_DATA.getId()))).get(0)
				.getDatasetId();

		final List<MeasurementVariable> entryVariables =
			this.datasetService.getObservationSetVariables(plotDatasetId,
				Lists.newArrayList(VariableType.GERMPLASM_DESCRIPTOR.getId(), VariableType.ENTRY_DETAIL.getId()));

		final List<MeasurementVariable> fixedEntryVariables =
			entryVariables.stream().filter(d -> FIXED_GERMPLASM_DESCRIPTOR_IDS.contains(d.getTermId())).collect(
				Collectors.toList());

		//Remove the ones that are stored in stock and that in the future will not be descriptors
		final List<MeasurementVariable> variableEntryDescriptors =
			entryVariables.stream().filter(d -> !REMOVABLE_GERMPLASM_DESCRIPTOR_IDS.contains(d.getTermId())).collect(
				Collectors.toList());

		return
			this.daoFactory.getStudyEntrySearchDAO()
				.getStudyEntries(new StudyEntrySearchDto(studyId, fixedEntryVariables, variableEntryDescriptors, filter), pageable);
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
			.filter(projectProperty -> VariableType.ENTRY_DETAIL.getId().equals(projectProperty.getTypeId()) && (!projectProperty.getVariableId().equals(TermId.ENTRY_TYPE.getId()) &&
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
		final List<Variable> germplasmListVariables = this.germplasmListService.getGermplasmListVariables(null, listId, VariableType.ENTRY_DETAIL.getId());
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
		final Term entryType = this.ontologyDataManager.getTermById(Integer.valueOf(entryTypeId));
		final Integer nextEntryNumber = this.getNextEntryNumber(studyId);
		this.daoFactory.getStockDao().createStudyEntries(studyId, nextEntryNumber, gids, entryType.getId(), entryType.getName());

		final Integer crossGenerationLevel = this.getCrossGenerationLevel(studyId);
		final List<StockModel> entries = this.daoFactory.getStockDao().getStocksByStudyAndEntryNumbersGreaterThanEqual(studyId, nextEntryNumber);
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
			new StudyEntryPropertyData(null, stockProperty.getTypeId(), value.isPresent() ? value.get() : stockProperty.getValue(), stockProperty.getCategoricalValueId()))
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
	public List<StudyEntryColumnDTO> getStudyEntryColumns(final Integer studyId) {
		final DmsProject plotDataset = this.getPlotDataset(studyId);
		final List<StudyEntryColumnDTO> columns = new ArrayList<>();
		columns.add(this.buildStudyEntryColumnDTO(TermId.GID, plotDataset.getProperties()));
		columns.add(this.buildStudyEntryColumnDTO(TermId.GUID, plotDataset.getProperties()));
		columns.add(this.buildStudyEntryColumnDTO(TermId.DESIG.getId(), "DESIGNATION", plotDataset.getProperties()));
		columns.add(this.buildStudyEntryColumnDTO(TermId.CROSS, plotDataset.getProperties()));
		columns.add(this.buildStudyEntryColumnDTO(TermId.GROUPGID, plotDataset.getProperties()));
		columns.add(this.buildStudyEntryColumnDTO(TermId.IMMEDIATE_SOURCE_NAME, plotDataset.getProperties()));
		columns.add(this.buildStudyEntryColumnDTO(TermId.GROUP_SOURCE_NAME, plotDataset.getProperties()));
		return columns;
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

	private StudyEntryColumnDTO buildStudyEntryColumnDTO(final TermId termId, final List<ProjectProperty> projectProperties) {
		return this.buildStudyEntryColumnDTO(termId.getId(), termId.name(), projectProperties);
	}

	private StudyEntryColumnDTO buildStudyEntryColumnDTO(final Integer termId, final String name, final List<ProjectProperty> projectProperties) {
		return new StudyEntryColumnDTO(termId, name,
			projectProperties.stream().anyMatch(projectProperty -> projectProperty.getVariableId().equals(termId)));
	}

	private void setStudyGenerationLevel(final Integer listId, final Integer studyId) {
		final Integer generationLevel = this.germplasmListService.getGermplasmListById(listId).get().getGenerationLevel();
		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyId);
		study.setGenerationLevel(generationLevel);
		this.daoFactory.getDmsProjectDAO().save(study);
	}

}
