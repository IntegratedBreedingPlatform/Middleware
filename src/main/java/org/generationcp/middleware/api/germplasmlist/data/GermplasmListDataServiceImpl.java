package org.generationcp.middleware.api.germplasmlist.data;

import org.generationcp.middleware.api.germplasmlist.GermplasmListColumnDTO;
import org.generationcp.middleware.api.germplasmlist.GermplasmListMeasurementVariableDTO;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListColumnCategory;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListDataDefaultView;
import org.generationcp.middleware.pojos.GermplasmListDataDetail;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Transactional
@Service
public class GermplasmListDataServiceImpl implements GermplasmListDataService {

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	@Autowired
	private PedigreeDataManager pedigreeDataManager;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	private final DaoFactory daoFactory;

	private List<GermplasmListStaticColumns> defaultColumns;

	public GermplasmListDataServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmListDataSearchResponse> searchGermplasmListData(
		final Integer listId, final GermplasmListDataSearchRequest request, final Pageable pageable) {

		final List<GermplasmListDataView> view = this.daoFactory.getGermplasmListDataViewDAO().getByListId(listId);
		final List<GermplasmListDataViewModel> viewModel = this.getView(view);
		final List<GermplasmListDataSearchResponse> response =
			this.daoFactory.getGermplasmListDataSearchDAO().searchGermplasmListData(listId, viewModel, request, pageable);

		if (CollectionUtils.isEmpty(response)) {
			return response;
		}

		if (view.stream().anyMatch(this::viewHasParentData)) {
			final Set<Integer> gids = response
				.stream()
				.map(r -> (Integer) r.getData().get(GermplasmListStaticColumns.GID.name()))
				.collect(toSet());

			this.addParentsFromPedigreeTable(gids, response);
		}

		return response;
	}

	@Override
	public long countSearchGermplasmListData(final Integer listId, final GermplasmListDataSearchRequest request) {
		return this.daoFactory.getGermplasmListDataSearchDAO().countSearchGermplasmListData(listId, request);
	}

	@Override
	public List<GermplasmListColumnDTO> getGermplasmListColumns(final Integer listId, final String programUUID) {
		final List<GermplasmListDataView> view = this.daoFactory.getGermplasmListDataViewDAO().getByListId(listId);
		final List<Integer> selectedColumnIds = this.getSelectedGermplasmListColumns(view);
		final List<GermplasmListColumnDTO> columns = GermplasmListStaticColumns.getColumnsSortedByRank()
			.map(column -> new GermplasmListColumnDTO(column.getTermId(), column.getName(), GermplasmListColumnCategory.STATIC,
				selectedColumnIds.contains(column.getTermId())))
			.collect(toList());

		final List<Integer> gids = this.daoFactory.getGermplasmListDataDAO().getGidsByListId(listId);
		final Set<Integer> nameIds = view.stream().filter(germplasmListDataView -> germplasmListDataView.getNameFldno() != null).map(GermplasmListDataView::getNameFldno).collect(toSet());
		final List<UserDefinedField> nameTypes = this.daoFactory.getUserDefinedFieldDAO().getNameTypesByGIDList(gids);

		if (!CollectionUtils.isEmpty(nameIds)) {
			final List<UserDefinedField> existingNameTypes = this.daoFactory.getUserDefinedFieldDAO().getByFldnos(nameIds);
			columns.addAll(existingNameTypes.stream().map(nameType ->
					new GermplasmListColumnDTO(nameType.getFldno(), nameType.getFcode(), GermplasmListColumnCategory.NAMES, true))
				.collect(toList()));
		}

		if (!CollectionUtils.isEmpty(nameTypes)) {
			final List<GermplasmListColumnDTO> nameColumns = nameTypes.stream().filter((nameType) -> !nameIds.contains(nameType.getFldno()))
				.map(nameType -> new GermplasmListColumnDTO(nameType.getFldno(), nameType.getFcode(), GermplasmListColumnCategory.NAMES,
					selectedColumnIds.contains(nameType.getFldno())))
				.collect(toList());
			columns.addAll(nameColumns);
		}

		final List<Attribute> attributes = this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(gids);
		if (!CollectionUtils.isEmpty(attributes)) {
			final VariableFilter variableFilter = new VariableFilter();
			variableFilter.setProgramUuid(programUUID);
			attributes
				.stream()
				.map(Attribute::getTypeId)
				.forEach(variableFilter::addVariableId);
			final List<Variable> variables = this.ontologyVariableDataManager.getWithFilter(variableFilter);
			final List<GermplasmListColumnDTO> germplasmAttributeColumns = variables
				.stream()
				.sorted(this.getVariableComparator())
				.map(variable -> {
					Integer typeId = null;
					// get first value because germplasm attributes/passport are not combinables with other types
					if (!CollectionUtils.isEmpty(variable.getVariableTypes())) {
						typeId = variable.getVariableTypes().iterator().next().getId();
					}
					return new GermplasmListColumnDTO(variable.getId(), variable.getName(), variable.getAlias(), typeId,
						GermplasmListColumnCategory.VARIABLE, selectedColumnIds.contains(variable.getId()));
				})
				.collect(toList());
			columns.addAll(germplasmAttributeColumns);
		}
		return columns;
	}

	@Override
	public List<GermplasmListMeasurementVariableDTO> getGermplasmListDataTableHeader(final Integer listId, final String programUUID) {
		final List<GermplasmListDataView> view =
			this.daoFactory.getGermplasmListDataViewDAO().getByListId(listId);
		// Provide a default view
		if (view.isEmpty()) {
			return this.transformDefaultStaticColumns();
		}

		final List<GermplasmListMeasurementVariableDTO> header = new ArrayList<>();
		final List<Integer> staticIds = view
			.stream()
			.filter(GermplasmListDataView::isStaticColumn)
			.map(GermplasmListDataView::getStaticId)
			.collect(toList());
		if (!CollectionUtils.isEmpty(staticIds)) {
			final List<GermplasmListMeasurementVariableDTO> staticColumns = staticIds
				.stream()
				.sorted(Comparator.comparing(staticId -> GermplasmListStaticColumns.getByTermId(staticId).getRank()))
				.map(id -> {
					final GermplasmListStaticColumns staticColumn = GermplasmListStaticColumns.getByTermId(id);
					return new GermplasmListMeasurementVariableDTO(staticColumn.getTermId(), staticColumn.getName(), staticColumn.name(),
						GermplasmListColumnCategory.STATIC);
				})
				.collect(toList());
			header.addAll(staticColumns);
		}

		final List<Integer> nameTypeIds = view
			.stream()
			.filter(GermplasmListDataView::isNameColumn)
			.map(GermplasmListDataView::getNameFldno)
			.collect(toList());
		if (!CollectionUtils.isEmpty(nameTypeIds)) {
			final List<UserDefinedField> nameTypes = this.daoFactory.getUserDefinedFieldDAO().filterByColumnValues("fldno", nameTypeIds);
			final List<GermplasmListMeasurementVariableDTO> nameColumns = nameTypes
				.stream()
				.sorted(Comparator.comparing(UserDefinedField::getFcode))
				.map(nameType -> new GermplasmListMeasurementVariableDTO(nameType.getFldno(), nameType.getFname(), nameType.getFcode(),
					GermplasmListColumnCategory.NAMES))
				.collect(toList());
			header.addAll(nameColumns);
		}

		final List<Integer> variableIds = view
			.stream()
			.filter(GermplasmListDataView::isVariableColumn)
			.map(GermplasmListDataView::getCvtermId)
			.collect(toList());
		final List<GermplasmListMeasurementVariableDTO> variableColumns = this.getVariableColumns(variableIds, programUUID);
		if (!CollectionUtils.isEmpty(variableColumns)) {
			final Map<Integer, GermplasmListMeasurementVariableDTO> columnsIndexedByTermId =
				variableColumns.stream().collect(Collectors.toMap(GermplasmListMeasurementVariableDTO::getTermId, standardVariable -> standardVariable));
			header.add(0, columnsIndexedByTermId.remove(TermId.ENTRY_NO.getId()));
			header.addAll(columnsIndexedByTermId.values());
		}
		return header;
	}

	@Override
	public void updateGermplasmListDataView(final Integer listId, final List<GermplasmListDataUpdateViewDTO> view) {
		final GermplasmList germplasmList = this.daoFactory.getGermplasmListDAO().getById(listId);
		final List<GermplasmListDataView> entryDetailColumns = germplasmList.getView()
			.stream()
			.filter(GermplasmListDataView::isEntryDetailColumn)
			.collect(toList());

		final List<GermplasmListDataView> updatedView = view
			.stream()
			.map(updateColumn -> GermplasmListDataViewFactory.create(germplasmList, updateColumn))
			.collect(toList());
		if (!CollectionUtils.isEmpty(entryDetailColumns)) {
			updatedView.addAll(entryDetailColumns);
		}
		germplasmList.setView(updatedView);
		this.daoFactory.getGermplasmListDAO().save(germplasmList);
	}

	@Override
	public void fillWithCrossExpansion(final Integer listId, final Integer level) {
		final GermplasmList germplasmList = this.daoFactory.getGermplasmListDAO().getById(listId);
		germplasmList.setGenerationLevel(level);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final List<GermplasmListData> germplasmListData = this.daoFactory.getGermplasmListDataDAO().getByListId(listId);
		final Set<Integer> gids = germplasmListData.stream().map(GermplasmListData::getGid).collect(toSet());

		final Map<Integer, String> pedigreeStringMap =
			this.pedigreeService.getCrossExpansionsBulk(gids, level, this.crossExpansionProperties);

		for (final GermplasmListData entry : germplasmListData) {
			entry.setGroupName(pedigreeStringMap.get(entry.getGid()));
			entry.truncateGroupNameIfNeeded();
			this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(entry);
		}
	}

	@Override
	public List<GermplasmListDataDetail> getGermplasmListDataDetailList(final Integer listId) {
		return this.daoFactory.getGermplasmListDataDetailDAO().getByListId(listId);
	}

	@Override
	public void reOrderEntries(final Integer listId, final List<Integer> selectedEntries, final Integer entryNumberPosition) {
		this.daoFactory.getGermplasmListDataDAO().reOrderEntries(listId, selectedEntries, entryNumberPosition);
	}

	@Override
	public long countByListId(final Integer listId) {
		return this.daoFactory.getGermplasmListDataDAO().countByListId(listId);
	}

	@Override
	public List<Integer> getGidsByListId(final Integer listId) {
		return this.daoFactory.getGermplasmListDataDAO().getGidsByListId(listId);
	}

	@Override
	public List<Integer> getListDataIdsByListId(final Integer listId) {
		return this.daoFactory.getGermplasmListDataDAO().getListDataIdsByListId(listId);
	}

	@Override
	public void saveDefaultView(final GermplasmList list) {
		// Forcing add ENTRY_NO variable.
		// It is particular case because is required after the migration of ENTRY_NO as a entry detail variable.
		final GermplasmListDataView germplasmListDataView =
			new GermplasmListDataView.GermplasmListDataVariableViewBuilder(list, TermId.ENTRY_NO.getId(),
				VariableType.ENTRY_DETAIL.getId()).build();
		this.daoFactory.getGermplasmListDataViewDAO().save(germplasmListDataView);

		this.getDefaultViewColumns()
			.stream()
			.forEach(column -> {
				final GermplasmListDataView view =
					new GermplasmListDataView.GermplasmListDataStaticViewBuilder(list, column.getTermId()).build();
				this.daoFactory.getGermplasmListDataViewDAO().save(view);
			});
	}

	@Override
	public void deleteNameTypeFromGermplasmList(final Integer nameTypeId) {
		this.daoFactory.getGermplasmListDataViewDAO().deleteByNameType(nameTypeId);
	}

	private void addParentsFromPedigreeTable(final Set<Integer> gids, final List<GermplasmListDataSearchResponse> response) {

		final Integer level = this.crossExpansionProperties.getCropGenerationLevel(this.pedigreeService.getCropName());
		final com.google.common.collect.Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable =
			this.pedigreeDataManager.generatePedigreeTable(gids, level, false);

		response.forEach(row -> {
			final Integer gid = (Integer) row.getData().get(GermplasmListStaticColumns.GID.name());

			final Optional<Germplasm> femaleParent = pedigreeTreeNodeTable.get(gid, ColumnLabels.FGID.getName());
			femaleParent.ifPresent(value -> {
				final Germplasm germplasm = value;
				row.getData().put(
					GermplasmListStaticColumns.FEMALE_PARENT_GID.name(),
					germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN);
				row.getData().put(
					GermplasmListStaticColumns.FEMALE_PARENT_NAME.name(),
					germplasm.getPreferredName().getNval());
			});

			final Optional<Germplasm> maleParent = pedigreeTreeNodeTable.get(gid, ColumnLabels.MGID.getName());
			if (maleParent.isPresent()) {
				final Germplasm germplasm = maleParent.get();
				row.getData().put(
					GermplasmListStaticColumns.MALE_PARENT_GID.name(),
					germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN);
				row.getData().put(
					GermplasmListStaticColumns.MALE_PARENT_NAME.name(),
					germplasm.getPreferredName().getNval());
			}
		});
	}

	private boolean viewHasParentData(final GermplasmListDataView column) {
		return column.isStaticColumn() && (GermplasmListStaticColumns.FEMALE_PARENT_GID.getTermId().equals(column.getStaticId()) ||
			GermplasmListStaticColumns.FEMALE_PARENT_NAME.getTermId().equals(column.getStaticId()) ||
			GermplasmListStaticColumns.MALE_PARENT_GID.getTermId().equals(column.getStaticId()) ||
			GermplasmListStaticColumns.MALE_PARENT_NAME.getTermId().equals(column.getStaticId()));
	}

	private List<GermplasmListDataViewModel> getView(final List<GermplasmListDataView> view) {
		// Provide default view
		if (CollectionUtils.isEmpty(view)) {
			return this.transformDefaultView();
		}

		final List<GermplasmListDataView> entryDetailsColumns =
			view.stream().filter(GermplasmListDataView::isEntryDetailColumn).collect(toList());
		// Check if there are only entry details in view
		if (view.size() == entryDetailsColumns.size()) {
			final List<GermplasmListDataViewModel> defaultView = this.transformDefaultView();
			final List<GermplasmListDataViewModel> entryDetailsModels =
				entryDetailsColumns.stream().map(GermplasmListDataViewModel::new).collect(toList());
			return Stream.concat(defaultView.stream(), entryDetailsModels.stream()).collect(toList());
		}

		return view
			.stream()
			.map(GermplasmListDataViewModel::new)
			.collect(toList());
	}

	private List<GermplasmListDataViewModel> transformDefaultView() {
		return this.getDefaultViewColumns()
			.stream()
			.map(column -> GermplasmListDataViewModel.buildStaticGermplasmListDataViewModel(column.getTermId()))
			.collect(toList());
	}

	private List<Integer> getSelectedGermplasmListColumns(final List<GermplasmListDataView> view) {
		// Return a default view if there is not a view defined yet
		if (CollectionUtils.isEmpty(view)) {
			return this.getDefaultColumnIds();
		}
		return view.stream()
			.map(GermplasmListDataView::getColumnId)
			.collect(toList());
	}

	private List<Integer> getDefaultColumnIds() {
		return this.getDefaultViewColumns()
			.stream()
			.map(GermplasmListStaticColumns::getTermId)
			.collect(toList());
	}

	private List<GermplasmListStaticColumns> getDefaultViewColumns() {
		if (this.defaultColumns == null) {
			this.defaultColumns = this.daoFactory.getGermplasmListDataDefaultViewDAO()
				.getAll()
				.stream()
				.map(GermplasmListDataDefaultView::getName)
				.sorted(Comparator.comparingInt(GermplasmListStaticColumns::getRank))
				.collect(toList());
		}
		return this.defaultColumns;
	}

	private List<GermplasmListMeasurementVariableDTO> transformDefaultStaticColumns() {
		return this.getDefaultViewColumns()
			.stream()
			.map(column -> new GermplasmListMeasurementVariableDTO(column.getTermId(), column.getName(), column.name(),
				GermplasmListColumnCategory.STATIC))
			.collect(toList());
	}

	/**
	 * Returns a list ordered by name and grouped by germplasm descriptor first and then by entry detail
	 *
	 * @param variableIds
	 * @param programUUID
	 * @return
	 */
	private List<GermplasmListMeasurementVariableDTO> getVariableColumns(final List<Integer> variableIds, final String programUUID) {
		if (!CollectionUtils.isEmpty(variableIds)) {
			final Map<Integer, List<ValueReference>> categoricalVariablesMap =
				this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(variableIds);
			final VariableFilter variableFilter = new VariableFilter();
			variableFilter.setProgramUuid(programUUID);
			variableIds.forEach(variableFilter::addVariableId);
			final List<Variable> variables = this.ontologyVariableDataManager.getWithFilter(variableFilter);
			final List<GermplasmListMeasurementVariableDTO> descriptorColumns = new ArrayList<>();
			final List<GermplasmListMeasurementVariableDTO> entryDetailsColumns = new ArrayList<>();
			variables
				.stream()
				.sorted(this.getVariableComparator())
				.forEach(variable -> {
					VariableType variableType = null;
					if (!CollectionUtils.isEmpty(variable.getVariableTypes())) {
						variableType = variable.getVariableTypes().iterator().next();
					}
					final GermplasmListMeasurementVariableDTO measurementVariableDTO =
						new GermplasmListMeasurementVariableDTO(
							variable,
							variableType,
							GermplasmListColumnCategory.VARIABLE,
							categoricalVariablesMap.get(variable.getId())
						);
					if (variableType == VariableType.GERMPLASM_ATTRIBUTE || variableType == VariableType.GERMPLASM_PASSPORT) {
						descriptorColumns.add(measurementVariableDTO);
					} else {
						entryDetailsColumns.add(measurementVariableDTO);
					}
				});
			return Stream.concat(descriptorColumns.stream(), entryDetailsColumns.stream()).collect(toList());
		}
		return new ArrayList<>();
	}

	private Comparator<Variable> getVariableComparator() {
		return Comparator.comparing(Variable::getAlias, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(Variable::getName);
	}

}
