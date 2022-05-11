/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO class for {@link StockModel}.
 */
public class StockDao extends GenericDAO<StockModel, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(StockDao.class);
	private static final String IN_STOCK_DAO = " in StockDao: ";

	public long countStudiesByGids(final List<Integer> gids) {

		try {
			final SQLQuery query = this.getSession()
				.createSQLQuery("select count(distinct p.project_id) " + "FROM stock s "
					+ "INNER JOIN project p ON s.project_id = p.project_id "
					+ "WHERE s.dbxref_id IN (:gids) "
					+ " AND p.deleted = 0");
			query.setParameterList("gids", gids);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error in countStudiesByGids=" + gids + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countPlotsByGids(final List<Integer> gids) {

		try {
			final SQLQuery query = this.getSession()
				.createSQLQuery("select count(distinct e.nd_experiment_id) " + "FROM stock s "
					+ "INNER JOIN project p ON s.project_id = p.project_id "
					+ "INNER JOIN nd_experiment e on e.stock_id = s.stock_id "
					+ "WHERE s.dbxref_id IN (:gids) "
					+ " AND p.deleted = 0");
			query.setParameterList("gids", gids);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error in countStudiesByGids=" + gids + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public boolean hasUnassignedEntries(final int studyId) {
		try {
			final SQLQuery query = this.getSession()
				.createSQLQuery("SELECT COUNT(*) FROM stock s "
					+ "WHERE s.project_id= :studyId AND NOT EXISTS (SELECT 1 FROM nd_experiment e WHERE e.stock_id=s.stock_id)");
			query.setParameter("studyId", studyId);
			return ((BigInteger) query.uniqueResult()).longValue() > 0;

		} catch (final HibernateException e) {
			final String errorMessage = "Error in hasUnassignedEntries=" + studyId + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByGid(final int gid) {
		final List<StudyReference> studyReferences = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession()
				.createSQLQuery("select distinct p.project_id, p.name, p.description, "
					+ "st.study_type_id, st.label, st.name as studyTypeName, st.visible, st.cvterm_id, p.program_uuid, p.locked, "
					+ "p.created_by "
					+ "FROM stock s "
					+ "INNER JOIN project p ON s.project_id = p.project_id "
					+ "INNER JOIN study_type st ON p.study_type_id = st.study_type_id "
					+ " WHERE s.dbxref_id = " + gid + " AND p.deleted = 0");
			query.addScalar("project_id").addScalar("name").addScalar("description").addScalar("study_type_id").addScalar("label")
				.addScalar("studyTypeName").addScalar("visible").addScalar("cvterm_id").addScalar("program_uuid").addScalar("locked")
				.addScalar("created_by");

			final List<Object[]> results = query.list();
			for (final Object[] row : results) {
				if (row[0] == null) {
					continue;
				}
				final Integer studyTypeId = (Integer) row[3];
				final String label = (String) row[4];
				final String studyTypeName = (String) row[5];
				final boolean visible = ((Byte) row[6]) == 1;
				final Integer cvtermId = (Integer) row[7];
				final String programUUID = (String) row[8];
				final Boolean isLocked = (Boolean) row[9];
				final String ownerId = (String) row[10];

				final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, label, studyTypeName, cvtermId, visible);
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2], programUUID, studyTypeDto,
					isLocked, Integer.valueOf(ownerId)));
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error in getStudiesByGid=" + gid + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return studyReferences;
	}

	public List<StockModel> getStocksForStudy(final int studyId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(StockModel.class);
			criteria.add(Restrictions.eq("project.projectId", studyId));
			// Return by ascending order of entry number. We need to perform cast first on uniquename since it's stored as string
			criteria.addOrder(new org.hibernate.criterion.Order("uniquename", true) {

				@Override
				public String toSqlString(final Criteria criteria, final CriteriaQuery criteriaQuery) throws HibernateException {
					return "CAST(uniquename AS UNSIGNED)";
				}
			});
			return criteria.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error in getStocksForStudy=" + studyId + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public void deleteStocksForStudy(final int studyId) {
		try {
			final Query query = this.getSession().createQuery("DELETE FROM StockModel sm WHERE sm.project.projectId = :studyId");
			query.setParameter("studyId", studyId);
			query.executeUpdate();
		} catch (final HibernateException e) {
			final String errorMessage = "Error in deleteStocksForStudy=" + studyId + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countStocksForStudy(final int studyId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(StockModel.class);
			criteria.add(Restrictions.eq("project.projectId", studyId));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String errorMessage = "Error in countStocksForStudy=" + studyId + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countStocksByStudyAndEntryTypeIds(final int studyId, final List<String> systemDefinedEntryTypeIds) {
		try {
			final Criteria criteria = this.getSession().createCriteria(StockModel.class);
			criteria.createAlias("properties", "properties");
			criteria.add(Restrictions.eq("project.projectId", studyId));
			criteria.add(Restrictions.and(Restrictions.eq("properties.typeId", TermId.ENTRY_TYPE.getId()),
				Restrictions.in("properties.value", systemDefinedEntryTypeIds)));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage =
				"Error with countStocksByStudyAndEntryTypeIds(studyId=" + studyId + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countStocks(final int datasetId, final int trialEnvironmentId, final int variateStdVarId) {
		try {

			final String sql = "select count(distinct e.stock_id) "
				+ "from nd_experiment e, phenotype p "
				+ "where e.nd_experiment_id = p.nd_experiment_id  "
				+ "  and e.nd_geolocation_id = " + trialEnvironmentId + "  and p.observable_id = " + variateStdVarId
				+ "  and e.project_id = " + datasetId;
			final Query query = this.getSession().createSQLQuery(sql);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error at countStocks=" + datasetId + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, List<StockModel>> getStocksByStudyIds(final List<Integer> studyIds) {
		try {

			final Map<Integer, List<StockModel>> stockMap = new HashMap<>();
			final Criteria criteria = this.getSession().createCriteria(StockModel.class);
			criteria.add(Restrictions.in("project.projectId", studyIds));
			criteria.addOrder(new org.hibernate.criterion.Order("uniquename", true) {

				@Override
				public String toSqlString(final Criteria criteria, final CriteriaQuery criteriaQuery) throws HibernateException {
					return "CAST(uniquename AS UNSIGNED)";
				}
			});

			final List<StockModel> stocks = criteria.list();
			for (final StockModel stockModel : stocks) {
				final Integer projectId = stockModel.getProject().getProjectId();
				stockMap.putIfAbsent(projectId, new ArrayList<>());
				stockMap.get(projectId).add(stockModel);
			}

			return stockMap;

		} catch (final HibernateException e) {
			final String errorMessage = "Error in getStocksByStudyIds=" + studyIds + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public Map<Integer, StudyEntryDto> getPlotEntriesMap(final Integer studyId, final Set<Integer> plotNos) {
		try {

			final String queryString =
				"select distinct(nd_ep.value) AS position, s.stock_id AS entryId, s.name AS designation, s.dbxref_id AS germplasmId "
					+ " FROM nd_experiment e "
					+ " INNER JOIN nd_experimentprop nd_ep ON e.nd_experiment_id = nd_ep.nd_experiment_id AND nd_ep.type_id IN (:PLOT_NO_TERM_IDS)"
					+ " INNER JOIN stock s ON s.stock_id = e.stock_id "
					+ " INNER JOIN project p ON e.project_id = p.project_id "
					+ " WHERE p.dataset_type_id = :DATASET_TYPE "
					+ " AND p.study_id = :STUDY_ID "
					+ " AND nd_ep.nd_experiment_id = e.nd_experiment_id ";

			final StringBuilder sb = new StringBuilder(queryString);
			if (!CollectionUtils.isEmpty(plotNos)) {
				sb.append(" AND nd_ep.value in (:PLOT_NOS) ");
			}
			final SQLQuery query = this.getSession().createSQLQuery(sb.toString());
			query.setParameter("STUDY_ID", studyId);
			if (!CollectionUtils.isEmpty(plotNos)) {
				query.setParameterList("PLOT_NOS", plotNos);
			}
			query.setParameter("DATASET_TYPE", DatasetTypeEnum.PLOT_DATA.getId());
			query.setParameterList("PLOT_NO_TERM_IDS",
				new Integer[] {TermId.PLOT_NO.getId(), TermId.PLOT_NNO.getId()});
			query.addScalar("position", new IntegerType());
			query.addScalar("entryId", new IntegerType());
			query.addScalar("designation", new StringType());
			query.addScalar("germplasmId", new IntegerType());

			final Map<Integer, StudyEntryDto> map = new HashMap<>();
			final List<Object[]> results = query.list();
			for (final Object[] row : results) {
				if (row[0] == null) {
					continue;
				}
				final Integer plotNumber = (Integer) row[0];
				final Integer entryId = (Integer) row[1];
				final String designation = (String) row[2];
				final Integer gid = (Integer) row[3];
				map.putIfAbsent(plotNumber, new StudyEntryDto(entryId, gid, designation));
			}
			return map;
		} catch (final HibernateException e) {
			final String errorMessage = "Error in getPlotEntriesMap " + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public void replaceExperimentStocks(final Integer oldStockId, final Integer newStockId) {
		try {
			final Query query =
				this.getSession().createQuery("UPDATE ExperimentModel SET stock.stockId = :newStockId WHERE stock.stockId = :oldStockId");
			query.setParameter("oldStockId", oldStockId);
			query.setParameter("newStockId", newStockId);
			query.executeUpdate();
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error in replaceExperimentStocks for oldStockId=" + oldStockId + ", newStockId=" + newStockId + StockDao.IN_STOCK_DAO + e
					.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<Integer> getGermplasmUsedInStudies(final List<Integer> gids, final boolean lockedStudiesOnly) {
		if (CollectionUtils.isEmpty(gids)) {
			return Collections.emptyList();
		}
		try {
			final Criteria criteria = this.getSession().createCriteria(StockModel.class);
			criteria.createAlias("project", "project");
			criteria.add(Restrictions.in("germplasm.gid", gids));
			criteria.add(Restrictions.eq("project.deleted", false));
			if (lockedStudiesOnly) {
				criteria.add(Restrictions.eq("project.locked", true));
			}
			criteria.setProjection(Projections.distinct(Projections.property("germplasm.gid")));
			return criteria.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error in getGermplasmUsedInStudies=" + gids + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public Integer getNextEntryNumber(final Integer studyId) {
		try {
			final String queryString =
				"SELECT IFNULL(MAX(Convert(s.uniquename, SIGNED)), 0) + 1 FROM stock s where s.project_id = :studyId";
			final SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.setParameter("studyId", studyId);
			return ((BigInteger) query.uniqueResult()).intValue();
		} catch (final HibernateException e) {
			final String errorMessage = "Error in getNextEntryNumber for studyId=" + studyId + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<GermplasmStudyDto> getGermplasmStudyDtos(final Integer gid) {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT p.project_id AS studyId, ");
			queryString.append("p.name AS name, ");
			queryString.append("p.description AS description, ");
			queryString.append("p.program_uuid AS programUUID ");
			queryString.append("FROM stock s ");
			queryString.append("INNER JOIN project p ON s.project_id = p.project_id ");
			queryString.append("WHERE s.dbxref_id = :gid AND p.deleted = 0");

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString.toString());
			sqlQuery.addScalar("studyId");
			sqlQuery.addScalar("name");
			sqlQuery.addScalar("description");
			sqlQuery.addScalar("programUUID");
			sqlQuery.setParameter("gid", gid);

			sqlQuery.setResultTransformer(new AliasToBeanResultTransformer(GermplasmStudyDto.class));
			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error at getGermplasmStudyDtos(gid=" + gid + ")" + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<StockModel> getStocksByGids(final List<Integer> gids) {
		try {
			final Criteria criteria = this.getSession().createCriteria(StockModel.class);
			criteria.createAlias("project", "project");
			criteria.add(Restrictions.in("germplasm.gid", gids));
			criteria.add(Restrictions.eq("project.deleted", false));
			return criteria.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error in getStocksByGids=" + gids + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<StockModel> getStocksByStudyAndEntryNumbers(final Integer studyId, final Set<String> entryNumbers) {
		final Criteria criteria = this.getSession().createCriteria(StockModel.class);
		criteria.createAlias("project", "project");
		criteria.add(Restrictions.eq("project.projectId", studyId));
		criteria.add(Restrictions.in("uniqueName", entryNumbers));
		return criteria.list();
	}

	public void createStudyEntries(final Integer studyId, final Integer listId) {
		final String insertStockQuery = "INSERT INTO stock(dbxref_id, name, uniquename, project_id, cross_value) "
			+ "SELECT ld.gid, (SELECT n.nval FROM names n WHERE nstat = 1 AND n.gid = ld.gid), ld.entryid, " + studyId + ", ld.grpname "
			+ " FROM listdata ld WHERE ld.listid = " + listId;
		this.getSession().createSQLQuery(insertStockQuery).executeUpdate();

		final String insertStockPropertyQuery = "INSERT INTO stockprop(stock_id, type_id, value, cvalue_id) "
			+ "SELECT s.stock_id, ld.variable_id, ld.value, ld.cvalue_id FROM list_data_details ld "
			+ "		INNER JOIN listdata l ON ld.lrecid = l.lrecid "
			+ "     INNER JOIN stock s ON l.entryid = s.uniquename "
			+ " WHERE l.listid = "  + listId + " AND s.project_id = " + studyId;
		this.getSession().createSQLQuery(insertStockPropertyQuery).executeUpdate();

		// Add entry type with a default value for those entries which don't have a entry type set
		final String insertEntryTypeProperty = "INSERT INTO stockprop(stock_id, type_id, value, cvalue_id) "
			+ "SELECT stock_id, " + TermId.ENTRY_TYPE.getId() + ", '" + SystemDefinedEntryType.TEST_ENTRY.getEntryTypeValue() + "', "
			+ SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId() + "  "
			+ "		FROM listdata ld "
			+ "		INNER JOIN stock s ON ld.entryid = s.uniquename "
			+ "		LEFT JOIN list_data_details ldd ON ldd.lrecid = ld.lrecid "
			+ " WHERE ld.listid = "  + listId + " AND s.project_id = " + studyId + " AND ld.entryid NOT IN "
			+ "		(SELECT ld.entryid FROM listdata ld "
			+ "				INNER JOIN list_data_details ldd ON ld.lrecid = ldd.lrecid "
			+ "         WHERE ld.listid = " + listId + " and ldd.variable_id = " + TermId.ENTRY_TYPE.getId() + ") "
			+ " GROUP BY ld.entryid";
		this.getSession().createSQLQuery(insertEntryTypeProperty).executeUpdate();
	}

	public void createStudyEntries(final Integer studyId, final Integer startingEntryNumber, final List<Integer> gids,
		final Integer entryTypeId, final String entryTypeValue) {
		final String gidsClause = gids.stream().map(Object::toString).collect(Collectors.joining(","));
		final String insertStockQuery = "INSERT INTO stock(dbxref_id, name, uniquename, project_id) "
			+ "SELECT g.gid, (SELECT n.nval FROM names n WHERE n.nstat = 1 AND n.gid = g.gid), (@entryNumber \\:= @entryNumber + 1), " + studyId
			+ " 	FROM germplsm g  "
			+ "		JOIN (SELECT @entryNumber \\:= " + (startingEntryNumber - 1) + ") entryNumber "
			+ "WHERE g.gid IN (" + gidsClause + ")";
		this.getSession().createSQLQuery(insertStockQuery).executeUpdate();

		final String insertEntryTypeProperty = "INSERT INTO stockprop(stock_id, type_id, value, cvalue_id) "
			+ "SELECT stock_id, " + TermId.ENTRY_TYPE.getId() + ", '" + entryTypeValue + "', "
			+ entryTypeId + "  "
			+ "		FROM stock WHERE project_id = " + studyId + " AND uniquename >= " + startingEntryNumber;
		this.getSession().createSQLQuery(insertEntryTypeProperty).executeUpdate();
	}

	public void deleteStocksForStudyAndVariable(final int studyId, final List<Integer> variableIds) {
		final Query query = this.getSession().createSQLQuery("DELETE sp FROM stockprop sp INNER JOIN stock s ON sp.stock_id = s.stock_id "
			+ " WHERE s.project_id = :studyId AND sp.type_id IN (:variableIds)");
		query.setParameter("studyId", studyId);
		query.setParameterList("variableIds", variableIds);
		query.executeUpdate();
	}

	public List<StockModel> getStocksByStudyAndEntryNumbersGreaterThanEqual(final Integer studyId, final Integer entryNumber) {
		return this.getSession()
			.createQuery("SELECT entry FROM StockModel entry WHERE entry.project.projectId = :studyId AND entry.uniqueName >= :entryNumber")
			.setParameter("studyId", studyId)
			.setParameter("entryNumber", entryNumber, IntegerType.INSTANCE)
			.list();
	}

}
