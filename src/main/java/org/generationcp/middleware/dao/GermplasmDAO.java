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

package org.generationcp.middleware.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 * DAO class for {@link Germplasm}.
 *
 */
public class GermplasmDAO extends GenericDAO<Germplasm, Integer> {

	private static final String STATUS_DELETED = "9";
	private static final String STOCK_IDS = "stockIDs";
	private static final String INVENTORY_ID = "inventoryID";
	private static final String GERMPLSM = "germplsm";
	private static final String Q_NO_SPACES = "qNoSpaces";
	private static final String Q_STANDARDIZED = "qStandardized";
	private static final String AVAIL_INV = "availInv";
	private static final String SEED_RES = "seedRes";

	@Override
	public Germplasm getById(Integer gid, boolean lock) throws MiddlewareQueryException {
		return this.getById(gid);
	}

	@Override
	public Germplasm getById(Integer gid) throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT g.* FROM germplsm g WHERE gid!=grplce AND gid=:gid LIMIT 1");

			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("gid", gid);
			query.addEntity("g", Germplasm.class);

			return (Germplasm) query.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getById(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByName(String name, Operation operation, Integer status, GermplasmNameType type, int start, int numOfRows)
			throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid "
					+ "WHERE g.gid!=g.grplce AND g.grplce = 0 ");

			if (operation == null || operation == Operation.EQUAL) {
				queryString.append("AND n.nval = :name ");
			} else if (operation == Operation.LIKE) {
				queryString.append("AND n.nval LIKE :name ");
			}

			if (status != null && status != 0) {
				queryString.append("AND n.nstat = :nstat ");
			} else {
				queryString.append("AND n.nstat != " + GermplasmDAO.STATUS_DELETED);
			}

			if (type != null) {
				queryString.append("AND n.ntype = :ntype ");
			}

			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("name", name);
			query.addEntity("g", Germplasm.class);

			if (status != null && status != 0) {
				query.setParameter("nstat", status);
			}

			if (type != null) {
				query.setParameter("ntype", Integer.valueOf(type.getUserDefinedFieldID()));
			}

			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByName(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getIdsByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT n.gid FROM names n LEFT JOIN germplsm g ON g.gid = n.gid AND g.gid!=g.grplce  "
					+ "WHERE n.nval = :name ");

			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getIdsByName(name=" + name + ") query from GermplasmDAO: " + e.getMessage(), e);
		}

		return new ArrayList<Integer>();
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getGermplasmByIds(List<Integer> germplasmIds, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT {g.*} FROM germplsm g WHERE g.gid!=grplce " + "AND g.gid IN (:ids) ");

			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("ids", germplasmIds);
			query.addEntity("g", Germplasm.class);

			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getGermplasmByIds(ids=" + germplasmIds.toString() + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByName(List<String> names, Operation operation, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (names != null && !names.isEmpty()) {
				String originalName = names.get(0);
				String standardizedName = names.get(1);
				String noSpaceName = names.get(2);

				// Search using = by default
				SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_BY_NAME_ALL_MODES_USING_EQUAL);
				if (operation == Operation.LIKE) {
					query = this.getSession().createSQLQuery(Germplasm.GET_BY_NAME_ALL_MODES_USING_LIKE);
				}

				// Set the parameters
				query.setParameter("name", originalName);
				query.setParameter("noSpaceName", noSpaceName);
				query.setParameter("standardizedName", standardizedName);

				query.addEntity("g", Germplasm.class);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);

				return query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByName(names=" + names + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	public long countByName(String name, Operation operation, Integer status, GermplasmNameType type) throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
			queryString
					.append("SELECT COUNT(DISTINCT n.gid) FROM names n LEFT OUTER JOIN germplsm g ON g.gid = n.gid AND g.gid!=g.grplce AND g.grplce = 0 WHERE ");

			if (operation == null || operation == Operation.EQUAL) {
				queryString.append("n.nval = :name ");
			} else if (operation == Operation.LIKE) {
				queryString.append("n.nval LIKE :name ");
			}

			if (status != null && status != 0) {
				queryString.append("AND n.nstat = :nstat ");
			} else {
				queryString.append("AND n.nstat != " + GermplasmDAO.STATUS_DELETED);
			}

			if (type != null) {
				queryString.append("AND n.ntype = :ntype ");
			}

			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("name", name);

			if (status != null && status != 0) {
				query.setParameter("nstat", status);
			}

			if (type != null) {
				query.setParameter("ntype", Integer.valueOf(type.getUserDefinedFieldID()));
			}

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByName(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
		}
		return 0;
	}

	public long countByName(List<String> names, Operation operation) throws MiddlewareQueryException {
		try {
			if (names != null) {
				String originalName = names.get(0);
				String standardizedName = names.get(1);
				String noSpaceName = names.get(2);

				// Count using = by default
				SQLQuery query = this.getSession().createSQLQuery(Germplasm.COUNT_BY_NAME_ALL_MODES_USING_EQUAL);
				if (operation == Operation.LIKE) {
					query = this.getSession().createSQLQuery(Germplasm.COUNT_BY_NAME_ALL_MODES_USING_LIKE);
				}

				// Set the parameters
				query.setParameter("name", originalName);
				query.setParameter("noSpaceName", noSpaceName);
				query.setParameter("standardizedName", standardizedName);

				return ((BigInteger) query.uniqueResult()).longValue();
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByName(names=" + names + ") query from Germplasm: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByMethodNameUsingEqual(String name, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_METHOD_NAME_USING_EQUAL);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByMethodNameUsingEqual(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	public long countByMethodNameUsingEqual(String name) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_EQUAL);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByMethodNameUsingEqual(name=" + name + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByMethodNameUsingLike(String name, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_METHOD_NAME_USING_LIKE);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByMethodNameUsingLike(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	public long countByMethodNameUsingLike(String name) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_LIKE);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByMethodNameUsingLike(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationNameUsingEqual(String name, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_LOCATION_NAME_USING_EQUAL);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByLocationNameUsingEqual(name=" + name + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return new ArrayList<Germplasm>();
	}

	public long countByLocationNameUsingEqual(String name) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_EQUAL);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countByLocationNameUsingEqual(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationNameUsingLike(String name, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_LOCATION_NAME_USING_LIKE);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByLocationNameUsingLike(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	public long countByLocationNameUsingLike(String name) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_LIKE);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByLocationNameUsingLike(name=" + name + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public Germplasm getByGIDWithPrefName(Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_PREF_NAME);
				query.addEntity("g", Germplasm.class);
				query.addEntity("n", Name.class);
				query.setParameter("gid", gid);
				List results = query.list();

				if (!results.isEmpty()) {
					Object[] result = (Object[]) results.get(0);
					if (result != null) {
						Germplasm germplasm = (Germplasm) result[0];
						Name prefName = (Name) result[1];
						germplasm.setPreferredName(prefName);
						return germplasm;
					}
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByGIDWithPrefName(gid=" + gid + ") from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Germplasm getByGIDWithPrefAbbrev(Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_PREF_ABBREV);
				query.addEntity("g", Germplasm.class);
				query.addEntity("n", Name.class);
				query.addEntity("abbrev", Name.class);
				query.setParameter("gid", gid);
				List results = query.list();

				if (results.isEmpty()) {
					return null;
				}
				Object[] result = (Object[]) results.get(0);
				Germplasm germplasm = (Germplasm) result[0];
				Name prefName = (Name) result[1];
				Name prefAbbrev = (Name) result[2];
				germplasm.setPreferredName(prefName);
				if (prefAbbrev != null) {
					germplasm.setPreferredAbbreviation(prefAbbrev.getNval());
				}
				return germplasm;
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByGIDWithPrefAbbrev(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getProgenitorsByGIDWithPrefName(Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				List<Germplasm> progenitors = new ArrayList<Germplasm>();

				SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_PROGENITORS_BY_GID_WITH_PREF_NAME);
				query.addEntity("g", Germplasm.class);
				query.addEntity("n", Name.class);
				query.setParameter("gid", gid);
				List<Object[]> results = query.list();
				for (Object[] result : results) {
					Germplasm germplasm = (Germplasm) result[0];
					Name prefName = (Name) result[1];
					germplasm.setPreferredName(prefName);
					progenitors.add(germplasm);
				}

				return progenitors;
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getProgenitorsByGIDWithPrefName(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getGermplasmDescendantByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				Query query = this.getSession().getNamedQuery(Germplasm.GET_DESCENDANTS);
				query.setParameter("gid", gid);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getGermplasmDescendantByGID(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	public Germplasm getProgenitorByGID(Integer gid, Integer proNo) throws MiddlewareQueryException {
		try {
			if (gid != null & proNo != null) {
				String progenitorQuery = "";
				if (proNo == 1) {
					progenitorQuery = Germplasm.GET_PROGENITOR1;
				} else if (proNo == 2) {
					progenitorQuery = Germplasm.GET_PROGENITOR2;
				} else if (proNo > 2) {
					progenitorQuery = Germplasm.GET_PROGENITOR;
				}

				Query query = this.getSession().getNamedQuery(progenitorQuery);
				query.setParameter("gid", gid);

				if (proNo > 2) {
					query.setParameter("pno", proNo);
				}

				return (Germplasm) query.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getProgenitorByGID(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	public long countGermplasmDescendantByGID(Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				Query query = this.getSession().createSQLQuery(Germplasm.COUNT_DESCENDANTS);
				query.setParameter("gid", gid);
				return ((BigInteger) query.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countGermplasmDescendantByGID(gid=" + gid + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return 0;
	}

	public List<Germplasm> getManagementNeighbors(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
		List<Germplasm> toreturn = new ArrayList<Germplasm>();
		try {
			if (gid != null) {
				SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_MANAGEMENT_NEIGHBORS);
				query.addEntity("g", Germplasm.class);
				query.addEntity("n", Name.class);
				query.setParameter("gid", gid);

				query.setFirstResult(start);
				query.setMaxResults(numOfRows);

				for (Object resultObject : query.list()) {
					Object[] result = (Object[]) resultObject;
					Germplasm germplasm = (Germplasm) result[0];
					Name prefName = (Name) result[1];
					germplasm.setPreferredName(prefName);
					toreturn.add(germplasm);
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getManagementNeighbors(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return toreturn;
	}

	public long countManagementNeighbors(Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				SQLQuery query = this.getSession().createSQLQuery(Germplasm.COUNT_MANAGEMENT_NEIGHBORS);
				query.setParameter("gid", gid);
				BigInteger count = (BigInteger) query.uniqueResult();
				return count.longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countManagementNeighbors(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return 0;
	}

	public long countGroupRelatives(Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				SQLQuery query = this.getSession().createSQLQuery(Germplasm.COUNT_GROUP_RELATIVES);
				query.setParameter("gid", gid);
				BigInteger count = (BigInteger) query.uniqueResult();
				return count.longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countGroupRelatives(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return 0;
	}

	public List<Germplasm> getGroupRelatives(Integer gid, int start, int numRows) throws MiddlewareQueryException {
		List<Germplasm> toreturn = new ArrayList<Germplasm>();
		try {
			if (gid != null) {
				SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_GROUP_RELATIVES);
				query.addEntity("g", Germplasm.class);
				query.addEntity("n", Name.class);
				query.setParameter("gid", gid);

				query.setFirstResult(start);
				query.setMaxResults(numRows);

				for (Object resultObject : query.list()) {
					Object[] result = (Object[]) resultObject;
					Germplasm germplasm = (Germplasm) result[0];
					Name prefName = (Name) result[1];
					germplasm.setPreferredName(prefName);
					toreturn.add(germplasm);
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getGroupRelatives(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return toreturn;
	}

	public List<Germplasm> getChildren(Integer gid, char methodType) throws MiddlewareQueryException {
		List<Germplasm> toreturn = new ArrayList<Germplasm>();
		try {
			String queryString = methodType == 'D' ? Germplasm.GET_DERIVATIVE_CHILDREN : Germplasm.GET_MAINTENANCE_CHILDREN;
			SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.addEntity("g", Germplasm.class);
			query.addEntity("n", Name.class);
			query.setParameter("gid", gid);

			for (Object resultObject : query.list()) {
				Object[] result = (Object[]) resultObject;
				Germplasm germplasm = (Germplasm) result[0];
				Name prefName = (Name) result[1];
				germplasm.setPreferredName(prefName);
				toreturn.add(germplasm);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDerivativeChildren(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return toreturn;

	}

	public String getNextSequenceNumberForCrossName(String prefix) throws MiddlewareQueryException {
		String nextInSequence = "1";

		try {
			/*
			 * This query will generate next number will be queried on first "word" of cross name.
			 * 
			 * eg. input prefix: "IR" output: next number in "IRNNNNN -----" sequence
			 */

			SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_PREFIX3);
			query.setParameter("prefix", prefix);
			query.setParameter("prefixLike", prefix + "%");

			/*
			 * If the next number will be queried on second "word" of cross name. IMPORTANT: assumes that passed in prefix value has a
			 * whitespace at the end
			 * 
			 * eg. input prefix: "IR " output: next number in "IR NNNNN..." sequence
			 */

			BigInteger nextNumberInSequence = (BigInteger) query.uniqueResult();

			if (nextNumberInSequence != null) {
				nextInSequence = String.valueOf(nextNumberInSequence);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getNextSequenceNumberForCrossName(prefix=" + prefix + ") " + "query : " + e.getMessage(),
					e);
		}

		return nextInSequence;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationId(String name, int locationID) throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE ");
			queryString.append("n.nval = :name ");
			queryString.append("AND g.glocn = :locationID ");

			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("name", name);
			query.setParameter("locationID", locationID);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByLocationId(name=" + name + ", locationID=" + locationID + ") query from Germplasm: "
					+ e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	@SuppressWarnings("rawtypes")
	public Germplasm getByGIDWithMethodType(Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_METHOD_TYPE);
				query.addEntity("g", Germplasm.class);
				query.addEntity("m", Method.class);
				query.setParameter("gid", gid);
				List results = query.list();

				if (!results.isEmpty()) {
					Object[] result = (Object[]) results.get(0);
					if (result != null) {
						Germplasm germplasm = (Germplasm) result[0];
						Method method = (Method) result[1];
						germplasm.setMethod(method);
						return germplasm;
					}
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByGIDWithMethodType(gid=" + gid + ") from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByGIDRange(int startGID, int endGID) throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT {g.*} FROM germplsm g WHERE ");
			queryString.append("g.gid >= :startGID ");
			queryString.append("AND g.gid <= :endGID ");

			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("startGID", startGID);
			query.setParameter("endGID", endGID);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByGIDRange(startGID=" + startGID + ", endGID=" + endGID + ") query from Germplasm: "
					+ e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByGIDList(List<Integer> gids) throws MiddlewareQueryException {

		if (gids.isEmpty()) {
			return new ArrayList<Germplasm>();
		}

		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT {g.*} FROM germplsm g WHERE ");
			queryString.append("g.gid IN( :gids )");

			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gids);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByGIDList(gids=" + gids.toString() + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	/**
	 * Search for germplasms given a search term
	 * 
	 * @param q - the search term to be used
	 * @param o - like or equal
	 * @param includeParents boolean flag to denote whether parents will be included in search results
	 * @param withInventoryOnly - boolean flag to denote whether result will be filtered by those with inventories only
	 * @return List of Germplasms
	 * @throws MiddlewareQueryException
	 */
	public List<Germplasm> searchForGermplasms(String searchedString, Operation o, boolean includeParents, boolean withInventoryOnly)
			throws MiddlewareQueryException {
		String q = searchedString.trim();
		if ("".equals(q)) {
			return new ArrayList<Germplasm>();
		}
		try {

			Set<Germplasm> result = new LinkedHashSet<Germplasm>();
			Set<Germplasm> resultParents = new LinkedHashSet<Germplasm>();

			String additionalQuery = withInventoryOnly ? Germplasm.WHERE_WITH_INVENTORY : "";

			// find germplasms with GID = or like q
			if (q.matches("(-)?(%)?[(\\d+)(%|_)?]*(%)?")) {
				SQLQuery p1Query;
				if (o.equals(Operation.LIKE)) {
					p1Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GID_LIKE + additionalQuery);
					p1Query.setParameter("gid", q);
				} else {
					p1Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GID + additionalQuery);
					p1Query.setParameter("gidLength", q.length());
					p1Query.setParameter("gid", q);
				}

				p1Query.addEntity(GermplasmDAO.GERMPLSM, Germplasm.class);
				this.addInventoryInfo(p1Query);
				result.addAll(this.getSearchForGermplasmsResult(p1Query.list()));
			}
			// find germplasms with inventory_id = or like q
			result.addAll(this.searchForGermplasmsByInventoryId(q, o, additionalQuery));

			// find germplasms with nVal = or like q
			SQLQuery p2Query;
			if (o.equals(Operation.LIKE)) {
				p2Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GERMPLASM_NAME_LIKE + additionalQuery);
			} else {
				p2Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GERMPLASM_NAME + additionalQuery);
			}
			p2Query.setParameter("q", q);
			p2Query.setParameter(GermplasmDAO.Q_NO_SPACES, q.replaceAll(" ", ""));
			p2Query.setParameter(GermplasmDAO.Q_STANDARDIZED, GermplasmDataManagerUtil.standardizeName(q));
			p2Query.setParameter("deletedStatus", GermplasmDAO.STATUS_DELETED);
			p2Query.addEntity(GermplasmDAO.GERMPLSM, Germplasm.class);
			this.addInventoryInfo(p2Query);
			result.addAll(this.getSearchForGermplasmsResult(p2Query.list()));

			// Add parents to results if specified by "includeParents" flag
			if (includeParents) {
				for (Germplasm g : result) {
					List<Integer> parentGids = new ArrayList<Integer>();
					if (g.getGpid1() != null && g.getGpid1() != 0) {
						parentGids.add(g.getGpid1());
					}
					if (g.getGpid2() != null && g.getGpid2() != 0) {
						parentGids.add(g.getGpid2());
					}

					if (!parentGids.isEmpty()) {
						SQLQuery pQuery = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GIDS);
						pQuery.setParameterList("gids", parentGids);
						pQuery.addEntity(GermplasmDAO.GERMPLSM, Germplasm.class);
						this.addInventoryInfo(pQuery);
						resultParents.addAll(this.getSearchForGermplasmsResult(pQuery.list()));
					}
				}

				result.addAll(resultParents);
			}

			return new ArrayList<Germplasm>(result);

		} catch (Exception e) {
			this.logAndThrowException("Error with searchGermplasms(" + q + ") " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	private void addInventoryInfo(SQLQuery query) {
		query.addScalar(GermplasmDAO.STOCK_IDS);
		query.addScalar(GermplasmDAO.AVAIL_INV);
		query.addScalar(GermplasmDAO.SEED_RES);
	}

	private List<Germplasm> getSearchForGermplasmsResult(List<Object[]> result) {
		List<Germplasm> germplasms = new ArrayList<Germplasm>();
		if (result != null) {
			for (Object[] row : result) {
				germplasms.add(this.mapToGermplasm(row));
			}
		}
		return germplasms;
	}

	private Germplasm mapToGermplasm(Object[] row) {
		Germplasm germplasm = (Germplasm) row[0];
		GermplasmInventory inventoryInfo = new GermplasmInventory(germplasm.getGid());
		inventoryInfo.setStockIDs((String) row[1]);
		inventoryInfo.setActualInventoryLotCount(row[2] != null ? ((BigInteger) row[2]).intValue() : 0);
		inventoryInfo.setReservedLotCount(row[3] != null ? ((BigInteger) row[3]).intValue() : 0);
		germplasm.setInventoryInfo(inventoryInfo);
		return germplasm;
	}

	/**
	 * @param q - inventory / stock id to be searched
	 * @param o - operation (like, equal)
	 * @return list of germplasms
	 */
	@SuppressWarnings("unchecked")
	protected List<Germplasm> searchForGermplasmsByInventoryId(String q, Operation o, String additionalQuery) {
		SQLQuery p1Query;
		if (o.equals(Operation.LIKE)) {
			p1Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_INVENTORY_ID_LIKE + additionalQuery);
		} else {
			p1Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_INVENTORY_ID + additionalQuery);
		}
		p1Query.setParameter(GermplasmDAO.INVENTORY_ID, q);
		p1Query.addEntity(GermplasmDAO.GERMPLSM, Germplasm.class);
		this.addInventoryInfo(p1Query);
		return this.getSearchForGermplasmsResult(p1Query.list());
	}

	public Map<Integer, Integer> getGermplasmDatesByGids(List<Integer> gids) {
		Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_GERMPLASM_DATES_BY_GIDS);
		query.setParameterList("gids", gids);
		@SuppressWarnings("rawtypes")
		List results = query.list();
		for (Object result : results) {
			Object[] resultArray = (Object[]) result;
			Integer gid = (Integer) resultArray[0];
			Integer gdate = (Integer) resultArray[1];
			resultMap.put(gid, gdate);
		}
		return resultMap;
	}

	public Map<Integer, Integer> getMethodIdsByGids(List<Integer> gids) {
		Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_METHOD_IDS_BY_GIDS);
		query.setParameterList("gids", gids);
		@SuppressWarnings("rawtypes")
		List results = query.list();
		for (Object result : results) {
			Object[] resultArray = (Object[]) result;
			Integer gid = (Integer) resultArray[0];
			Integer methodId = (Integer) resultArray[1];
			resultMap.put(gid, methodId);
		}
		return resultMap;
	}

	/**
	 * Returns a Map with the names of parental germplasm for a given study. These names are returned in a Map, where the key is the
	 * germplasm identifier (gid) and the value is a list with all the names ({@link Name}) for such germplasm. This method optimizes data
	 * returned, because in a study is common that many entries have common parents, so those duplicated parents are omitted in returned
	 * Map.
	 * 
	 * @param studyId The ID of the study from which we need to get parents information. Usually this is the ID of a crossing block.
	 * @return
	 */
	public Map<Integer, Map<GermplasmNameType, Name>> getGermplasmParentNamesForStudy(int studyId) {

		SQLQuery queryNames = this.getSession().createSQLQuery(Germplasm.GET_PARENT_NAMES_BY_STUDY_ID);
		queryNames.setParameter("projId", studyId);

		List resultNames = queryNames.list();

		Germplasm g;
		Name name;
		Map<Integer, Map<GermplasmNameType, Name>> names = new HashMap<>();
		int i = 0;

		for (Object result : resultNames) {
			i++;
			Object resultArray[] = (Object[]) result;
			Integer gid = Integer.valueOf(resultArray[0].toString());
			Integer ntype = Integer.valueOf(resultArray[1].toString());
			String nval = resultArray[2].toString();
			Integer nid = Integer.valueOf(resultArray[3].toString());
			Integer nstat = Integer.valueOf(resultArray[4].toString());

			name = new Name(nid);
			name.setGermplasmId(gid);
			name.setNval(nval);
			name.setTypeId(ntype);
			name.setNstat(nstat);

			if (!names.containsKey(gid)) {
				names.put(gid, new HashMap<GermplasmNameType, Name>());
			}

			GermplasmNameType type = GermplasmNameType.valueOf(name.getTypeId());
			if (type == null) {
				type = GermplasmNameType.UNRESOLVED_NAME;
			}

			if (!names.get(gid).containsKey(type) || names.get(gid).get(type).getNstat() != 1) {
				names.get(gid).put(type, name);
			}

		}

		return names;
	}

	public List<Germplasm> getGermplasmParentsForStudy(int studyId) {
		SQLQuery queryGermplasms = this.getSession().createSQLQuery(Germplasm.GET_PARENT_GIDS_BY_STUDY_ID);
		queryGermplasms.setParameter("projId", studyId);

		List<Germplasm> germplasms = new ArrayList<>();
		Germplasm g;

		List resultGermplasms = queryGermplasms.list();
		for (Object result : resultGermplasms) {
			Object resultArray[] = (Object[]) result;
			g = new Germplasm(Integer.valueOf(resultArray[0].toString()));
			g.setGpid1(Integer.valueOf(resultArray[1].toString()));
			g.setGpid2(Integer.valueOf(resultArray[2].toString()));
			g.setGrplce(Integer.valueOf(resultArray[3].toString()));

			germplasms.add(g);
		}
		return germplasms;
	}

	public Germplasm getByLGid(Integer lgid) throws MiddlewareQueryException {
		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT g.* FROM germplsm g WHERE gid!=grplce AND lgid=:lgid LIMIT 1");

			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("lgid", lgid);
			query.addEntity("g", Germplasm.class);

			return (Germplasm) query.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByLGid(lgid=" + lgid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

}
