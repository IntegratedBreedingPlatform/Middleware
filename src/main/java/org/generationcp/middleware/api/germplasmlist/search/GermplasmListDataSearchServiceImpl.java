package org.generationcp.middleware.api.germplasmlist.search;

import org.generationcp.middleware.api.germplasmlist.GermplasmListStaticColumns;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmListColumnCategory;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class GermplasmListDataSearchServiceImpl implements GermplasmListDataSearchService {

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	@Autowired
	private PedigreeDataManager pedigreeDataManager;

	private final DaoFactory daoFactory;

	public GermplasmListDataSearchServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmListDataSearchResponse> searchGermplasmListData(final Integer listId,
		final GermplasmListDataSearchRequest request,
		final Pageable pageable) {

		final List<GermplasmListDataView> view = this.daoFactory.getGermplasmListDataViewDAO().getByListId(listId);
		// TODO: review this once we define what we are gonna do with the default view
		if (CollectionUtils.isEmpty(view)) {
			final List<GermplasmListDataView> defaultView = GermplasmListStaticColumns.getDefaultColumns()
				.stream()
				.map(column -> new GermplasmListDataView(null, GermplasmListColumnCategory.STATIC, null, column.getTermId()))
				.collect(Collectors.toList());
			view.addAll(defaultView);
		}
		final List<GermplasmListDataSearchResponse> response =
			this.daoFactory.getGermplasmListDataSearchDAO().searchGermplasmListData(listId, view, request, pageable);

		if (CollectionUtils.isEmpty(response)) {
			return response;
		}

		final boolean hasCrossData = view
			.stream()
			.anyMatch(c -> c.getVariableId().equals(GermplasmListStaticColumns.CROSS.getTermId()) || this.viewHasParentData(c));

		if (hasCrossData) {
			final Map<Integer, GermplasmListDataSearchResponse> rowsIndexedByGid = response
				.stream()
				.collect(Collectors.toMap(r -> (Integer) r.getData().get(GermplasmListStaticColumns.GID.name()), Function.identity()));

			final Map<Integer, String> pedigreeStringMap =
				this.pedigreeService.getCrossExpansions(new HashSet(rowsIndexedByGid.keySet()), null, this.crossExpansionProperties);

			rowsIndexedByGid.entrySet().stream().forEach(e -> {
				final Integer gid = e.getKey();
				final GermplasmListDataSearchResponse row = e.getValue();
				row.getData().put(GermplasmListStaticColumns.CROSS.getName(), pedigreeStringMap.get(gid));
			});

			final boolean hasParentsData = view
				.stream()
				.anyMatch(this::viewHasParentData);

			if (hasParentsData) {
				this.addParentsFromPedigreeTable(rowsIndexedByGid);
			}
		}

		return response;
	}

	@Override
	public long countSearchGermplasmListData(final Integer listId, final GermplasmListDataSearchRequest request) {
		return this.daoFactory.getGermplasmListDataSearchDAO().countSearchGermplasmListData(listId, request);
	}

	private void addParentsFromPedigreeTable(final Map<Integer, GermplasmListDataSearchResponse> rowsIndexedByGid) {

		final Integer level = this.crossExpansionProperties.getCropGenerationLevel(this.pedigreeService.getCropName());
		final com.google.common.collect.Table<Integer, String, Optional<Germplasm>> pedigreeTreeNodeTable =
			this.pedigreeDataManager.generatePedigreeTable(rowsIndexedByGid.keySet(), level, false);

		for (final Map.Entry<Integer, GermplasmListDataSearchResponse> entry : rowsIndexedByGid.entrySet()) {
			final Integer gid = entry.getKey();
			final GermplasmListDataSearchResponse row = entry.getValue();

			final Optional<Germplasm> femaleParent = pedigreeTreeNodeTable.get(gid, ColumnLabels.FGID.getName());
			femaleParent.ifPresent(value -> {
				final Germplasm germplasm = value;
				row.getData().put(GermplasmListStaticColumns.FEMALE_PARENT_GID.name(),
					germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN);
				row.getData().put(GermplasmListStaticColumns.FEMALE_PARENT_NAME.name(),
					germplasm.getPreferredName().getNval());
			});

			final Optional<Germplasm> maleParent = pedigreeTreeNodeTable.get(gid, ColumnLabels.MGID.getName());
			if (maleParent.isPresent()) {
				final Germplasm germplasm = maleParent.get();
				row.getData().put(GermplasmListStaticColumns.MALE_PARENT_GID.name(),
					germplasm.getGid() != 0 ? String.valueOf(germplasm.getGid()) : Name.UNKNOWN);
				row.getData().put(GermplasmListStaticColumns.MALE_PARENT_NAME.name(),
					germplasm.getPreferredName().getNval());
			}
		}
	}

	private boolean viewHasParentData(final GermplasmListDataView column) {
		return column.getVariableId().equals(GermplasmListStaticColumns.FEMALE_PARENT_GID.getTermId()) ||
		column.getVariableId().equals(GermplasmListStaticColumns.FEMALE_PARENT_NAME.getTermId()) ||
			column.getVariableId().equals(GermplasmListStaticColumns.MALE_PARENT_GID.getTermId()) ||
			column.getVariableId().equals(GermplasmListStaticColumns.MALE_PARENT_NAME.getTermId());
	}

}
