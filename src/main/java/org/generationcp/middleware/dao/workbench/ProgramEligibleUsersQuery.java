package org.generationcp.middleware.dao.workbench;

import com.google.common.base.Joiner;
import org.generationcp.middleware.domain.workbench.RoleType;
import org.generationcp.middleware.domain.workbench.UserSearchRequest;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ProgramEligibleUsersQuery {

	public static final String USER_ID = "id";
	public static final String USERNAME = "username";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String EMAIL = "email";

	//User with access to a program is the union of:
	//1. Users with Instance role
	//2. Users with crop role and no program role assigned
	//3. Users with explicit access to the program via program role
	public static final String GET_ACTIVE_USER_IDS_WITH_ACCESS_TO_A_PROGRAM =
		"select distinct u.userid " //
			+ "from users u " //
			+ "       inner join crop_persons cp on cp.personid = u.personid " //
			+ "       inner join users_roles ur on ur.userid = u.userid " //
			+ "       inner join role r on ur.role_id = r.id " //
			+ "       left join workbench_project wp on ur.workbench_project_id = wp.project_id " //
			+ "where ((role_type_id = " + RoleType.INSTANCE.getId() + ") " //
			+ "  || (role_type_id = " + RoleType.CROP.getId()
			+ " and ur.crop_name = cp.crop_name and not exists(SELECT distinct p1.project_id " //
			+ "                                      FROM workbench_project p1 " //
			+ "                                             INNER JOIN " //
			+ "                                      users_roles ur1 ON ur1.workbench_project_id = p1.project_id " //
			+ "                                             INNER JOIN role r1 ON ur1.role_id = r1.id " //
			+ "                                      where r1.role_type_id = " + RoleType.PROGRAM.getId() //
			+ "                                        AND ur1.crop_name = cp.crop_name AND ur1.userid = u.userid) "
			//
			+ "    || (role_type_id= " + RoleType.PROGRAM.getId() + " and wp.project_uuid = :programUUID))) and " //
			+ "  u.ustatus = 0 and cp.crop_name = (select wpi.crop_type from workbench_project wpi where wpi.project_uuid = :programUUID) ";

	private static final String BASE_SQL = "SELECT %s FROM users u " //
		+ "INNER JOIN persons p ON u.personid = p.personid " //
		+ "INNER JOIN crop_persons cp ON cp.personid = p.personid " //
		+ "WHERE u.ustatus = 0 AND cp.crop_name = (select i.crop_type from workbench_project i where i.project_uuid = :programUUID) " //
		+ "AND u.userid NOT IN (" + GET_ACTIVE_USER_IDS_WITH_ACCESS_TO_A_PROGRAM //
		+ "  ) ";

	private static final String SELECT_CLAUSE =
		"u.userid AS " + USER_ID + ", u.uname as " + USERNAME + ", p.fname as " + FIRST_NAME + ", p.lname as " + LAST_NAME
			+ ", p.pemail as " + EMAIL;

	private static final String DEFAULT_ORDER_EXPRESSION = " ORDER BY firstName, lastName";

	private static String getSortClause(final Pageable pageable) {
		if (pageable != null && pageable.getSort() != null) {
			final StringBuilder query = new StringBuilder();
			final List<String> sorts = new ArrayList<>();
			for (final Sort.Order order : pageable.getSort()) {
				sorts.add(order.getProperty() + " " + order.getDirection().toString());
			}
			if (!sorts.isEmpty()) {
				query.append(" ORDER BY ").append(Joiner.on(",").join(sorts));
			}
			return query.toString();
		} else {
			return DEFAULT_ORDER_EXPRESSION;
		}
	}

	public static SQLQueryBuilder getSelectQuery(final Pageable pageable, final UserSearchRequest userSearchRequest) {
		final SQLQueryBuilder queryBuilder = new SQLQueryBuilder(String.format(BASE_SQL, SELECT_CLAUSE));
		addUserFilters(queryBuilder, userSearchRequest);
		queryBuilder.append(getSortClause(pageable));
		return queryBuilder;
	}

	public static SQLQueryBuilder getCountQuery(final UserSearchRequest userSearchRequest) {
		final SQLQueryBuilder queryBuilder = new SQLQueryBuilder(String.format(BASE_SQL, "COUNT(1) "));
		addUserFilters(queryBuilder, userSearchRequest);
		return queryBuilder;
	}

	private static void addUserFilters(final SQLQueryBuilder builder, final UserSearchRequest userSearchRequest) {
		if (userSearchRequest == null) {
			return;
		}
		final String username = userSearchRequest.getUsername();
		if (!isBlank(username)) {
			builder.append(" and u.uname like :username ");
			builder.setParameter("username", "%" + username + "%");
		}
		final String fullName = userSearchRequest.getFullName();
		if (!isBlank(fullName)) {
			builder.append(" and concat_ws(' ', p.fname, p.lname) like :fullName ");
			builder.setParameter("fullName", "%" + fullName + "%");
		}
		final String email = userSearchRequest.getEmail();
		if (!isBlank(email)) {
			builder.append(" and p.pemail like :email ");
			builder.setParameter("email", "%" + email + "%");
		}
	}

}
