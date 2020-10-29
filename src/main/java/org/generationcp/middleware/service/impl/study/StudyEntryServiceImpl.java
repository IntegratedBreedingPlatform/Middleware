
package org.generationcp.middleware.service.impl.study;

import com.google.common.collect.Lists;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyEntrySearchDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
public class StudyEntryServiceImpl implements StudyEntryService {

	@Resource
	private DatasetService datasetService;

	private final DaoFactory daoFactory;

	private static List<Integer> FIXED_GERMPLASM_DESCRIPTOR_IDS = Lists
		.newArrayList(TermId.ENTRY_CODE.getId(), TermId.DESIG.getId(), TermId.ENTRY_NO.getId(), TermId.GID.getId());

	private static List<Integer> REMOVABLE_GERMPLASM_DESCRIPTOR_IDS = Lists
		.newArrayList(TermId.ENTRY_CODE.getId(), TermId.DESIG.getId(), TermId.ENTRY_NO.getId(), TermId.GID.getId(),
			TermId.OBS_UNIT_ID.getId(), TermId.STOCKID.getId());

	public StudyEntryServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StudyEntryDto> getStudyEntries(final int studyId) {
		// Return by ascending order of entry number. We need to perform cast first on uniquename since it's stored as string
		return this.getStudyEntries(studyId, null, new PageRequest(0, Integer.MAX_VALUE,
				new Sort(Sort.Direction.ASC, String.valueOf(TermId.ENTRY_NO.getId()))));
	}

	@Override
	public List<StudyEntryDto> getStudyEntries(final int studyId, final StudyEntrySearchDto.Filter filter, final Pageable pageable) {

		final Integer plotDatasetId =
			datasetService.getDatasets(studyId, new HashSet<>(Collections.singletonList(DatasetTypeEnum.PLOT_DATA.getId()))).get(0)
				.getDatasetId();

		final List<MeasurementVariable> entryDescriptors =
			this.datasetService.getObservationSetVariables(plotDatasetId, Lists
				.newArrayList(VariableType.GERMPLASM_DESCRIPTOR.getId()));

		final List<MeasurementVariable> fixedEntryDescriptors =
			entryDescriptors.stream().filter(d -> FIXED_GERMPLASM_DESCRIPTOR_IDS.contains(d.getTermId())).collect(
				Collectors.toList());

		//Remove the ones that are stored in stock and that in the future will not be descriptors
		final List<MeasurementVariable> variableEntryDescriptors =
			entryDescriptors.stream().filter(d -> !REMOVABLE_GERMPLASM_DESCRIPTOR_IDS.contains(d.getTermId())).collect(
				Collectors.toList());

		return
			this.daoFactory.getStockDao()
				.getStudyEntries(new StudyEntrySearchDto(studyId, fixedEntryDescriptors, variableEntryDescriptors, filter), pageable);
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
	}

	@Override
	public List<StudyEntryDto> saveStudyEntries(final Integer studyId, final List<StudyEntryDto> studyEntryDtoList) {
		final List<Integer> entryIds = new ArrayList<>();
		for (final StudyEntryDto studyEntryDto : studyEntryDtoList) {
			final StockModel entry = new StockModel(studyId, studyEntryDto);
			this.daoFactory.getStockDao().saveOrUpdate(entry);
			entryIds.add(entry.getStockId());
		}
		final StudyEntrySearchDto.Filter filter = new StudyEntrySearchDto.Filter();
		filter.setEntryIds(entryIds);
		return this.getStudyEntries(studyId, filter, new PageRequest(0, Integer.MAX_VALUE));
	}

	@Override
	public long countStudyGermplasmByEntryTypeIds(final int studyId, final List<String> systemDefinedEntryTypeIds) {
		return this.daoFactory.getStockDao().countStocksByStudyAndEntryTypeIds(studyId, systemDefinedEntryTypeIds);
	}

	@Override
	public StudyEntryDto replaceStudyEntry(final int studyId, final int entryId, final int gid, final String crossExpansion) {
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
		// Copy from old entry: entry #, entry code
		final StudyEntryDto studyEntryDto = new StudyEntryDto();
		studyEntryDto.setEntryNumber(Integer.valueOf(stock.getUniqueName()));
		studyEntryDto.setGid(gid);
		studyEntryDto.setEntryCode(stock.getValue());

		final Name preferredName = newGermplasm.getPreferredName();
		studyEntryDto.setDesignation(preferredName != null ? preferredName.getNval() : "");

		// If germplasm descriptors exist for previous entry, copy ENTRY_TYPE value and set cross expansion and MGID of new germplasm
		this.addStudyEntryPropertyDataIfApplicable(stock, studyEntryDto, TermId.ENTRY_TYPE.getId(), Optional.empty());
		this.addStudyEntryPropertyDataIfApplicable(stock, studyEntryDto, TermId.CROSS.getId(), Optional.of(crossExpansion));
		this.addStudyEntryPropertyDataIfApplicable(stock, studyEntryDto, TermId.GROUPGID.getId(), Optional.of(String.valueOf(newGermplasm.getMgid())));

		final StudyEntryDto savedStudyEntryDto = this.saveStudyEntries(studyId, Collections.singletonList(studyEntryDto)).get(0);
		stockDao.replaceExperimentStocks(entryId, savedStudyEntryDto.getEntryId());
		// Finally delete old study entry
		stockDao.makeTransient(stock);

		return savedStudyEntryDto;
	}

	private void addStudyEntryPropertyDataIfApplicable(final StockModel stock, final StudyEntryDto studyEntryDto, final Integer variableId, final Optional<String> value) {
		final Optional<StockProperty> entryType =
			stock.getProperties().stream().filter(prop -> variableId.equals(prop.getTypeId())).findFirst();
		entryType.ifPresent(stockProperty -> {
				studyEntryDto.getProperties().put(variableId,
					new StudyEntryPropertyData(null, stockProperty.getTypeId(), value.isPresent()? value.get() : stockProperty.getValue()));
			}
		);
	}

	@Override
	public void updateStudyEntryProperty(final int studyId, final StudyEntryPropertyData studyEntryPropertyData) {
		final StockProperty stockProperty = this.daoFactory.getStockPropertyDao().getById(studyEntryPropertyData.getStudyEntryPropertyId());
		if (stockProperty != null) {
			stockProperty.setValue(studyEntryPropertyData.getValue());
			this.daoFactory.getStockPropertyDao().saveOrUpdate(stockProperty);
		}
	}

	@Override
	public Optional<StudyEntryPropertyData> getStudyEntryPropertyData(final int studyEntryPropertyId) {
		final StockProperty stockProperty = this.daoFactory.getStockPropertyDao().getById(studyEntryPropertyId);
		if (stockProperty != null) {
			return Optional
				.of(new StudyEntryPropertyData(stockProperty.getStockPropId(), stockProperty.getTypeId(), stockProperty.getValue()));
		}
		return Optional.empty();
	}

}
