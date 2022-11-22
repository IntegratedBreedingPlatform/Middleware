package org.generationcp.middleware.dao.workbench;

import com.google.common.base.Joiner;
import org.generationcp.middleware.domain.workbench.RoleType;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ProgramMembersQuery {

	public static String USER_ID = "userId";
	public static String USERNAME = "username";
	public static String FIRST_NAME = "firstName";
	public static String LAST_NAME = "lastName";
	public static String EMAIL = "email";
	public static String ROLE_ID = "roleid";
	public static String ROLE_NAME = "rolename";
	public static String ROLE_DESCRIPTION = "roledescription";
	public static String ROLE_TYPE_ID = "roletypeid";
	public static String ROLE_TYPE_NAME = "roletype";
	public static String ROLE_ACTIVE = "roleactive";

	//User with access to a program is the union of:
	//1. Users with Instance role
	//2. Users with crop role and no program role assigned
	//3. Users with explicit access to the program via program role
	private static final String PROGRAM_MEMBERS_BASE_QUERY = " select %s from users u " //
		+ "       inner join crop_persons cp on cp.personid = u.personid " //
		+ "       inner join persons p on cp.personid = p.personid " //
		+ "       inner join users_roles ur on ur.userid = u.userid " //
		+ "       inner join role r on ur.role_id = r.id " //
		+ "        inner join role_type rt on r.role_type_id = rt.role_type_id " //
		+ "       left join workbench_project wp on ur.workbench_project_id = wp.project_id " //
		+ "where ((r.role_type_id = " + RoleType.INSTANCE.getId() + ") " //
		+ "  || (r.role_type_id = " + RoleType.CROP.getId()
		+ " and ur.crop_name = cp.crop_name and not exists(SELECT distinct p1.project_id " //
		+ "                                      FROM workbench_project p1 " //
		+ "                                             INNER JOIN " //
		+ "                                      users_roles ur1 ON ur1.workbench_project_id = p1.project_id " //
		+ "                                             INNER JOIN role r1 ON ur1.role_id = r1.id " //
		+ "                                      where r1.role_type_id = " + RoleType.PROGRAM.getId() //
		+ "                                        AND ur1.crop_name = cp.crop_name AND ur1.userid = u.userid)"
		+ " 									 AND NOT EXISTS(select ur1.id "
		+ "                   					  from workbench.users_roles ur1 "
		+ "                          			  inner join role r2 on ur1.role_id = r2.id "
		+ "                   					 where r2.role_type_id = " + RoleType.INSTANCE.getId() + " and ur1.userid = u.userid) ) "
		//
		+ "    || (r.role_type_id= " + RoleType.PROGRAM.getId() + " and wp.project_uuid = :programUUID and "
		+ "										not exists(select ur1.id " //
		+ "                                     from workbench.users_roles ur1 " //
		+ "                                     inner join role r2 on ur1.role_id = r2.id " //
		+ "                                      where r2.role_type_id = " + RoleType.INSTANCE.getId()
		+ " and ur1.userid = u.userid))) and " //
		+ "  u.ustatus = 0 and cp.crop_name = (select wpi.crop_type from workbench_project wpi where wpi.project_uuid = :programUUID) ";

	private static final String PROGRAM_MEMBERS_SELECT_CLAUSE =
		" distinct u.userid as " + USER_ID + ",  u.uname as " + USERNAME + ", p.fname as " + FIRST_NAME + ", " //
			+ "p.lname as " + LAST_NAME + ", p.pemail as " + EMAIL + ", " //
			+ "r.id as " + ROLE_ID + ", r.name as " + ROLE_NAME + ", r.description as " + ROLE_DESCRIPTION //
			+ ", rt.role_type_id as " + ROLE_TYPE_ID +
			", rt.name as " //
			+ ROLE_TYPE_NAME + ", r.active as " + ROLE_ACTIVE;

	private static final String DEFAULT_SORT_EXPRESSION = " order by rt.role_type_id asc, r.name asc ";

	private static final String COUNT_EXPRESSION = " COUNT(1) ";

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
			return DEFAULT_SORT_EXPRESSION;
		}
	}

	public static SQLQueryBuilder getSelectQuery(
		final Pageable pageable, final ProgramMembersSearchRequest searchRequest) {
		final SQLQueryBuilder queryBuilder = new SQLQueryBuilder(String.format(PROGRAM_MEMBERS_BASE_QUERY, PROGRAM_MEMBERS_SELECT_CLAUSE));
		addUserFilters(queryBuilder, searchRequest);
		queryBuilder.append(getSortClause(pageable));
		return queryBuilder;
	}

	public static SQLQueryBuilder getCountQuery(final ProgramMembersSearchRequest searchRequest) {
		final SQLQueryBuilder queryBuilder = new SQLQueryBuilder(String.format(PROGRAM_MEMBERS_BASE_QUERY, COUNT_EXPRESSION));
		addUserFilters(queryBuilder, searchRequest);
		return queryBuilder;
	}

	private static void addUserFilters(final SQLQueryBuilder builder, final ProgramMembersSearchRequest searchRequest) {
		if (searchRequest == null) {
			return;
		}
		final String username = searchRequest.getUsername();
		if (!isBlank(username)) {
			builder.append(" and u.uname like :username ");
			builder.setParameter("username", "%" + username + "%");
		}
		final String fullName = searchRequest.getFullName();
		if (!isBlank(fullName)) {
			builder.append(" and concat_ws(' ', p.fname, p.lname) like :fullName ");
			builder.setParameter("fullName", "%" + fullName + "%");
		}
		final String roleName = searchRequest.getRoleName();
		if (!isBlank(roleName)) {
			builder.append(" and r.name like :roleName ");
			builder.setParameter("roleName", "%" + roleName + "%");
		}
	}
}
