package org.generationcp.middleware.dao.germplasmlist;

import org.generationcp.middleware.api.germplasmlist.search.GermplasmListDataSearchRequest;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

public class GermplasmListDataSearchDAOQuery {

	enum SortColumn {

		ENTRY_NUMBER(ENTRY_NUMBER_ALIAS);

		private String value;

		SortColumn(final String value) {
			this.value = value;
		}

		static SortColumn getByValue(final String value) {
			return Arrays.stream(SortColumn.values())
				.filter(e -> e.name().equals(value))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("Unsupported sort value %s.", value)));
		}
	}

	static final String LIST_DATA_ID_ALIAS = "listDataId";
	static final String ENTRY_NUMBER_ALIAS = "entryNumber";

	private final static String BASE_QUERY = "SELECT %s " // usage of SELECT_EXPRESION / COUNT_EXPRESSION
		+ " FROM listdata listData "
		+ " %s " // usage of SELECT_JOINS
		+ " WHERE listData.listid = :listId "
		+ "		AND listData.lrstatus <> " + GermplasmListDataDAO.STATUS_DELETED + " ";

	private final static String SELECT_EXPRESSION = "listData.lrecid AS " + LIST_DATA_ID_ALIAS + ", "
		+ "listData.entryid AS " + ENTRY_NUMBER_ALIAS;

	private static final String COUNT_EXPRESSION = " COUNT(1) ";

	static SQLQueryBuilder getSelectQuery(final GermplasmListDataSearchRequest request, final Pageable pageable) {
		final String baseQuery = String.format(BASE_QUERY, SELECT_EXPRESSION, "");
		final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
		addFilters(sqlQueryBuilder, request);
		DAOQueryUtils.addOrder(input -> SortColumn.getByValue(input).value, sqlQueryBuilder, pageable);
		return sqlQueryBuilder;
	}

	static SQLQueryBuilder getCountQuery(final GermplasmListDataSearchRequest request) {
		final String baseQuery = String.format(BASE_QUERY, COUNT_EXPRESSION, "");
		final SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder(baseQuery);
		addFilters(sqlQueryBuilder, request);
		return sqlQueryBuilder;
	}

	private static void addFilters(final SQLQueryBuilder sqlQueryBuilder, final GermplasmListDataSearchRequest request) {
		// TODO: implement filters
	}

}
