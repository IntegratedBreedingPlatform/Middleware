package org.generationcp.middleware.api.germplasmlist.data;

import org.generationcp.middleware.api.germplasmlist.GermplasmListColumnDTO;
import org.generationcp.middleware.api.germplasmlist.GermplasmListMeasurementVariableDTO;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.dms.ValueReference;
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

		final boolean hasCrossData = view
			.stream()
			.anyMatch(c -> GermplasmListStaticColumns.CROSS.getTermId().equals(c.getStaticId()) || this.viewHasParentData(c));

		if (hasCrossData) {
			final Set<Integer> gids = response
				.stream()
				.map(r -> (Integer) r.getData().get(GermplasmListStaticColumns.GID.name()))
				.collect(Collectors.toSet());

			final Map<Integer, String> pedigreeStringMap =
				this.pedigreeService.getCrossExpansions(gids, null, this.crossExpansionProperties);

			response.forEach(r -> {
				final Integer gid = (Integer) r.getData().get(GermplasmListStaticColumns.GID.name());
				r.getData().put(GermplasmListStaticColumns.CROSS.getName(), pedigreeStringMap.get(gid));
			});

			final boolean hasParentsData = view
				.stream()
				.anyMatch(this::viewHasParentData);

			if (hasParentsData) {
				this.addParentsFromPedigreeTable(gids, response);
			}
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
			.collect(Collectors.toList());

		final List<Integer> gids = this.daoFactory.getGermplasmListDataDAO().getGidsByListId(listId);
		final List<UserDefinedField> nameTypes = this.daoFactory.getUserDefinedFieldDAO().getNameTypesByGIDList(gids);
		if (!CollectionUtils.isEmpty(nameTypes)) {
			final List<GermplasmListColumnDTO> nameColumns = nameTypes
				.stream()
				.map(nameType -> new GermplasmListColumnDTO(nameType.getFldno(), nameType.getFcode(), GermplasmListColumnCategory.NAMES,
					selectedColumnIds.contains(nameType.getFldno())))
				.collect(Collectors.toList());
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
				.collect(Collectors.toList());
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

		// Check if there are only entry details columns
		final List<Integer> entryDetailsColumnIds = this.getEntryDetailsColumnsIds(view);
		// The user only added entry details to the view, so we provide a default view with static columns plus these entry details columns
		if (view.size() == entryDetailsColumnIds.size()) {
			final List<GermplasmListMeasurementVariableDTO> defaultStaticColumns = this.transformDefaultStaticColumns();
			final List<GermplasmListMeasurementVariableDTO> variableColumns = this.getVariableColumns(entryDetailsColumnIds, programUUID);
			return Stream.concat(defaultStaticColumns.stream(), variableColumns.stream()).collect(Collectors.toList());
		}

		final List<GermplasmListMeasurementVariableDTO> header = new ArrayList<>();
		final List<Integer> staticIds = view
			.stream()
			.filter(GermplasmListDataView::isStaticColumn)
			.map(GermplasmListDataView::getStaticId)
			.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(staticIds)) {
			final List<GermplasmListMeasurementVariableDTO> staticColumns = staticIds
				.stream()
				.sorted(Comparator.comparing(staticId -> GermplasmListStaticColumns.getByTermId(staticId).getRank()))
				.map(id -> {
					final GermplasmListStaticColumns staticColumn = GermplasmListStaticColumns.getByTermId(id);
					return new GermplasmListMeasurementVariableDTO(staticColumn.getTermId(), staticColumn.getName(), staticColumn.name(),
						GermplasmListColumnCategory.STATIC);
				})
				.collect(Collectors.toList());
			header.addAll(staticColumns);
		}

		final List<Integer> nameTypeIds = view
			.stream()
			.filter(GermplasmListDataView::isNameColumn)
			.map(GermplasmListDataView::getNameFldno)
			.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(nameTypeIds)) {
			final List<UserDefinedField> nameTypes = this.daoFactory.getUserDefinedFieldDAO().filterByColumnValues("fldno", nameTypeIds);
			final List<GermplasmListMeasurementVariableDTO> nameColumns = nameTypes
				.stream()
				.sorted(Comparator.comparing(UserDefinedField::getFcode))
				.map(nameType -> new GermplasmListMeasurementVariableDTO(nameType.getFldno(), nameType.getFname(), nameType.getFcode(),
					GermplasmListColumnCategory.NAMES))
				.collect(Collectors.toList());
			header.addAll(nameColumns);
		}

		final List<Integer> variableIds = view
			.stream()
			.filter(GermplasmListDataView::isVariableColumn)
			.map(GermplasmListDataView::getCvtermId)
			.collect(Collectors.toList());
		final List<GermplasmListMeasurementVariableDTO> variableColumns = this.getVariableColumns(variableIds, programUUID);
		if (!CollectionUtils.isEmpty(variableColumns)) {
			header.addAll(variableColumns);
		}
		return header;
	}

	@Override
	public void updateGermplasmListDataView(final Integer listId, final List<GermplasmListDataUpdateViewDTO> view) {
		final GermplasmList germplasmList = this.daoFactory.getGermplasmListDAO().getById(listId);
		final List<GermplasmListDataView> entryDetailColumns = germplasmList.getView()
			.stream()
			.filter(GermplasmListDataView::isEntryDetailColumn)
			.collect(Collectors.toList());

		final List<GermplasmListDataView> updatedView = view
			.stream()
			.map(updateColumn -> GermplasmListDataViewFactory.create(germplasmList, updateColumn))
			.collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(entryDetailColumns)) {
			updatedView.addAll(entryDetailColumns);
		}
		germplasmList.setView(updatedView);
		this.daoFactory.getGermplasmListDAO().save(germplasmList);
	}

	@Override
	public List<GermplasmListDataDetail> getGermplasmListDataList(final Integer listId) {
		return this.daoFactory.getGermplasmListDataDetailDAO().getByListId(listId);
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
			view.stream().filter(GermplasmListDataView::isEntryDetailColumn).collect(Collectors.toList());
		// Check if there are only entry details in view
		if (view.size() == entryDetailsColumns.size()) {
			final List<GermplasmListDataViewModel> defaultView = this.transformDefaultView();
			final List<GermplasmListDataViewModel> entryDetailsModels =
				entryDetailsColumns.stream().map(GermplasmListDataViewModel::new).collect(Collectors.toList());
			return Stream.concat(defaultView.stream(), entryDetailsModels.stream()).collect(Collectors.toList());
		}

		return view
			.stream()
			.map(GermplasmListDataViewModel::new)
			.collect(Collectors.toList());
	}

	private List<GermplasmListDataViewModel> transformDefaultView() {
		return this.getDefaultColumns()
			.stream()
			.map(column -> GermplasmListDataViewModel.buildStaticGermplasmListDataViewModel(column.getTermId()))
			.collect(Collectors.toList());
	}

	private List<Integer> getSelectedGermplasmListColumns(final List<GermplasmListDataView> view) {
		// Return a default view if there is not a view defined yet
		if (CollectionUtils.isEmpty(view)) {
			return this.getDefaultColumnIds();
		}
		final List<Integer> selectedColumnIds = view
			.stream()
			.map(GermplasmListDataView::getColumnId)
			.collect(Collectors.toList());

		final List<Integer> entryDetailsColumnsIds = this.getEntryDetailsColumnsIds(view);
		// Check if there are only entry details added. If it's the case, add the default columns
		if (view.size() == entryDetailsColumnsIds.size()) {
			selectedColumnIds.addAll(this.getDefaultColumnIds());
		}
		return selectedColumnIds;
	}

	private List<Integer> getDefaultColumnIds() {
		return this.getDefaultColumns()
			.stream()
			.map(GermplasmListStaticColumns::getTermId)
			.collect(Collectors.toList());
	}

	private List<GermplasmListMeasurementVariableDTO> transformDefaultStaticColumns() {
		return this.getDefaultColumns()
			.stream()
			.map(column -> new GermplasmListMeasurementVariableDTO(column.getTermId(), column.getName(), column.name(),
				GermplasmListColumnCategory.STATIC))
			.collect(Collectors.toList());
	}

	private List<GermplasmListStaticColumns> getDefaultColumns() {
		if (this.defaultColumns == null) {
			this.defaultColumns = this.daoFactory.getGermplasmListDataDefaultViewDAO()
				.getAll()
				.stream()
				.map(GermplasmListDataDefaultView::getName)
				.sorted(Comparator.comparingInt(GermplasmListStaticColumns::getRank))
				.collect(Collectors.toList());
		}
		return this.defaultColumns;
	}

	private List<Integer> getEntryDetailsColumnsIds(final List<GermplasmListDataView> view) {
		return view
			.stream()
			.filter(GermplasmListDataView::isEntryDetailColumn)
			.map(GermplasmListDataView::getCvtermId)
			.collect(Collectors.toList());
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
			return Stream.concat(descriptorColumns.stream(), entryDetailsColumns.stream()).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	private Comparator<Variable> getVariableComparator() {
		return Comparator.comparing(Variable::getAlias, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(Variable::getName);
	}

}
