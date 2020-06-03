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

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO class for {@link StockModel}.
 *
 */
public class StockDao extends GenericDAO<StockModel, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(StockDao.class);
	private static final String IN_STOCK_DAO = " in StockDao: ";
	static final String DBXREF_ID = "dbxrefId";

	@SuppressWarnings("unchecked")
	public List<Integer> getStockIdsByProperty(final String columnName, final String value)  {
		final List<Integer> stockIds;
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			if (DBXREF_ID.equals(columnName)) {
				criteria.add(Restrictions.eq("germplasm.gid", Integer.valueOf(value)));
			} else {
				criteria.add(Restrictions.eq(columnName, value));
			}
			criteria.setProjection(Projections.property("stockId"));

			stockIds = criteria.list();

		} catch (final HibernateException e) {
			final String errorMessage = "Error in getStockIdsByProperty=" + value + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return stockIds;
	}

	public long countStudiesByGid(final int gid)  {

		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("select count(distinct p.study_id) " + "FROM stock s "
							+ "LEFT JOIN nd_experiment e on e.stock_id = s.stock_id "
							+ "LEFT JOIN project p ON e.project_id= p.project_id " + "WHERE s.dbxref_id = " + gid
							+ " AND p.deleted = 0");
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error in countStudiesByGid=" + gid + StockDao.IN_STOCK_DAO + e.getMessage();
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
				+ "LEFT JOIN nd_experiment e on e.stock_id = s.stock_id "
				+ "LEFT JOIN project ds ON ds.project_id = e.project_id "
				+ "LEFT JOIN project p ON ds.study_id = p.project_id "
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
			final String errorMessage = "Error with countStocksByStudyAndEntryTypeIds(studyId=" + studyId + StockDao.IN_STOCK_DAO + e.getMessage();
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
	public Map<Integer, StockModel> getStocksByIds(final List<Integer> ids)  {
		final Map<Integer, StockModel> stockModels = new HashMap<>();
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("stockId", ids));
			final List<StockModel> stocks = criteria.list();

			for (final StockModel stock : stocks) {
				stockModels.put(stock.getStockId(), stock);
			}

		} catch (final HibernateException e) {
			final String errorMessage = "Error in getStocksByIds=" + ids + StockDao.IN_STOCK_DAO + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}

		return stockModels;
	}

	public List<StudyGermplasmDto> getStudyGermplasmDtoList(final Integer studyId, final Set<Integer> plotNos) {
		try {
			final String queryString = "select  distinct(nd_ep.value) AS position, s.name AS designation, s.dbxref_id AS germplasmId "
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
				new Integer[] { TermId.PLOT_NO.getId(), TermId.PLOT_NNO.getId() });
			query.addScalar("position", new StringType());
			query.addScalar("designation", new StringType());
			query.addScalar("germplasmId", new IntegerType());
			query.setResultTransformer(Transformers.aliasToBean(StudyGermplasmDto.class));
			return query.list();
		} catch (HibernateException e) {
			final String errorMessage = "Error in getStudyGermplasmDtoList " + e.getMessage();
			LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}


}
