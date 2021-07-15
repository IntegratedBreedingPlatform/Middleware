package org.generationcp.middleware.dao.germplasmlist;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListSearchRequest;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.generationcp.middleware.util.StringUtil;
import org.generationcp.middleware.util.Util;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GermplasmListSearchDAOQuery {

	// TODO: move this constants
	private static final String PROGRAM_LISTS = "Program lists";
	private static final String CROP_LISTS = "Crop lists";

	enum SortColumn {

		LIST_NAME(LIST_NAME_ALIAS),
		PARENT_FOLDER_NAME(PARENT_FOLDER_NAME_ALIAS),
		DESCRIPTION(DESCRIPTION_ALIAS),
		LIST_OWNER(LIST_OWNER_ALIAS),
		LIST_TYPE(LIST_TYPE_ALIAS),
		NUMBER_OF_ENTRIES(NUMBER_OF_ENTRIES_ALIAS),
		STATUS(STATUS_ALIAS),
		NOTES(NOTES_ALIAS),
		LIST_DATE(LIST_DATE_ALIAS);

		private String value;

		SortColumn(final String value) {
			this.value = value;
		}

		public static SortColumn getByValue(final String value) {
			return Arrays.stream(SortColumn.values())
				.filter(e -> e.name().equals(value))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("Unsupported sort value %s.", value)));
		}
	}

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

	static final String LIST_ID_ALIAS = "listId";
	static final String LIST_NAME_ALIAS = "listName";
	static final String PARENT_FOLDER_NAME_ALIAS = "parentFolderName";
	static final String DESCRIPTION_ALIAS = "description";
	static final String LIST_OWNER_ALIAS = "listOwner";
	static final String LIST_TYPE_ALIAS = "listType";
	static final String NUMBER_OF_ENTRIES_ALIAS = "numberOfEntries";
	static final String STATUS_ALIAS = "status";
	static final String NOTES_ALIAS = "notes";
	static final String LIST_DATE_ALIAS = "listDate";

	private final static String BASE_QUERY = "SELECT %s " // usage of SELECT_EXPRESION / COUNT_EXPRESSION
		+ " FROM listnms list "
		+ " %s " // usage of SELECT_JOINS
		+ " WHERE list.liststatus NOT IN ('" + GermplasmList.Status.FOLDER.getCode() + "', '" + GermplasmList.Status.DELETED.getCode() + "') "
		+ "		AND list.program_uuid = :programUUID";

	private final static String SELECT_EXPRESSION = "list.listId AS " + LIST_ID_ALIAS + ", "
		+ " list.listname AS " + LIST_NAME_ALIAS + ", "
		+ " IF (list.lhierarchy IS NULL, "
		+ "			IF(list.program_uuid IS NULL, '" + CROP_LISTS + "', '" + PROGRAM_LISTS + "'), "
		+ "			inn.listname) AS " + PARENT_FOLDER_NAME_ALIAS + ", "
		+ " list.listdesc AS " + DESCRIPTION_ALIAS + ", "
		+ " user.uname AS " + LIST_OWNER_ALIAS + ", "
		+ " list.listtype AS " + LIST_TYPE_ALIAS + ", "
		+ " (SELECT count(1) FROM listdata l WHERE l.listid = list.listid) AS " + NUMBER_OF_ENTRIES_ALIAS + ", "
		+ " IF (list.liststatus = " + GermplasmList.Status.LOCKED_LIST.getCode() + ", 'LOCKED', 'UNLOCKED') AS " + STATUS_ALIAS + ", "
		+ " list.notes AS " + NOTES_ALIAS + ", "
		+ " cast(list.listdate as char) AS " + LIST_DATE_ALIAS;

	private final static String SELF_JOIN_QUERY = " LEFT JOIN listnms inn ON list.lhierarchy = inn.listid ";
	private final static String WORKBENCH_USER_JOIN_QUERY = " INNER JOIN workbench.users user ON user.userid = list.listuid ";

	private static final String COUNT_EXPRESSION = " COUNT(1) ";

	static SQLQueryBuilder getSelectQuery(final GermplasmListSearchRequest request, final Pageable pageable) {
		final String baseQuery = String.format(BASE_QUERY, SELECT_EXPRESSION, SELF_JOIN_QUERY + WORKBENCH_USER_JOIN_QUERY);
		final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
		addFilters(sqlQueryBuilder, request);
		addOrder(sqlQueryBuilder, pageable);
		return sqlQueryBuilder;
	}

	static SQLQueryBuilder getCountQuery(final GermplasmListSearchRequest request) {
		final String countQueryJoins = getCountQueryJoins(request);
		final String baseQuery = String.format(BASE_QUERY, COUNT_EXPRESSION, countQueryJoins);
		final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
		addFilters(sqlQueryBuilder, request);
		return sqlQueryBuilder;
	}

	private static String getCountQueryJoins(final GermplasmListSearchRequest request) {
		final StringBuilder joinBuilder = new StringBuilder();
		if (!StringUtils.isEmpty(request.getParentFolderName())) {
			joinBuilder.append(SELF_JOIN_QUERY);
		}
		if (!StringUtil.isEmpty(request.getOwnerName())) {
			joinBuilder.append(WORKBENCH_USER_JOIN_QUERY);
		}
		return joinBuilder.toString();
	}

	private static void addFilters(final SQLQueryBuilder sqlQueryBuilder, final GermplasmListSearchRequest request) {
		final SqlTextFilter listNameFilter = request.getListNameFilter();
		if (listNameFilter != null && !listNameFilter.isEmpty()) {
			final String value = listNameFilter.getValue();
			final SqlTextFilter.Type type = listNameFilter.getType();
			final String operator = GenericDAO.getOperator(type);
			sqlQueryBuilder.setParameter("listName", GenericDAO.getParameter(type, value));
			sqlQueryBuilder.append(" AND list.listname ").append(operator).append(":listName");
		}

		if (!StringUtils.isEmpty(request.getParentFolderName())) {
			sqlQueryBuilder.setParameter("parentFolderName", "%" + request.getParentFolderName() + "%");
			sqlQueryBuilder.append(" AND (inn.listname LIKE :parentFolderName "
				+ " 	OR (list.lhierarchy IS NULL AND list.program_uuid IS NOT NULL AND '" + PROGRAM_LISTS + "' LIKE :parentFolderName)"
				+ " 	OR (list.lhierarchy IS NULL AND list.program_uuid IS NULL AND '" + CROP_LISTS + "' LIKE :parentFolderName)"
				+ " )");
		}

		if (!StringUtils.isEmpty(request.getDescription())) {
			sqlQueryBuilder.setParameter("description", "%" + request.getDescription() + "%");
			sqlQueryBuilder.append(" AND list.listdesc LIKE :description ");
		}

		if (!StringUtils.isEmpty(request.getOwnerName())) {
			sqlQueryBuilder.setParameter("ownerName", "%" + request.getOwnerName() + "%");
			sqlQueryBuilder.append(" AND user.uname LIKE :ownerName ");
		}

		if (!CollectionUtils.isEmpty(request.getListTypes())) {
			sqlQueryBuilder.setParameter("types", request.getListTypes());
			sqlQueryBuilder.append(" AND list.listtype IN (:types) ");
		}

		if (request.getLocked() != null) {
			Integer status = request.getLocked() ? GermplasmList.Status.LOCKED_LIST.getCode() : GermplasmList.Status.LIST.getCode();
			sqlQueryBuilder.setParameter("status", status);
			sqlQueryBuilder.append(" AND list.liststatus = :status ");
		}

		if (!StringUtils.isEmpty(request.getNotes())) {
			sqlQueryBuilder.setParameter("notes", "%" + request.getNotes() + "%");
			sqlQueryBuilder.append(" AND list.notes LIKE :notes ");
		}

		if (request.getListDateFrom() != null) {
			sqlQueryBuilder.append(" and list.listdate >= :listDateFrom ");
			sqlQueryBuilder.setParameter("listDateFrom", DATE_FORMAT.format(request.getListDateFrom()));
		}

		if (request.getListDateTo() != null) {
			sqlQueryBuilder.append(" and list.listdate <= :listDateTo ");
			sqlQueryBuilder.setParameter("listDateTo", DATE_FORMAT.format(request.getListDateTo()));
		}

	}

	private static String addOrder(final SQLQueryBuilder sqlQueryBuilder, final Pageable pageable) {
		if (pageable != null && pageable.getSort() != null) {
			final String orderClause = StreamSupport.stream(pageable.getSort().spliterator(), false)
				.map(order -> String.format(" %s %s ", SortColumn.getByValue(order.getProperty()).value, order.getDirection()))
				.collect(Collectors.joining(", "));
			if (!StringUtils.isEmpty(orderClause)) {
				sqlQueryBuilder.append(" ORDER BY ").append(orderClause);
			}
		}

		return null;
	}

}
