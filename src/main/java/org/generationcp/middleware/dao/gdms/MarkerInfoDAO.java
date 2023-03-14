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

package org.generationcp.middleware.dao.gdms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.MarkerInfo;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 * DAO class for {@link MarkerInfo}.
 *
 * @author Joyce Avestro
 */
public class MarkerInfoDAO extends GenericDAO<MarkerInfo, Integer> {

	/* Used by MarkerInfoDAO.getByMarkerName() */
	public static final String GET_BY_MARKER_NAME = "SELECT marker_id " + ", CONCAT(marker_type, '') " + ", CONCAT(marker_name, '')  "
			+ ", CONCAT(species, '')  " + ", db_accession_id " + ", reference " + ", CONCAT(genotype, '') " + ", ploidy " + ", motif "
			+ ", forward_primer " + ", reverse_primer " + ", product_size " + ", annealing_temp " + ", amplification "
			+ ", CONCAT(principal_investigator, '') " + ", contact " + ", institute " + ", genotypes_count "
			+ "FROM gdms_marker_retrieval_info " + "WHERE LOWER(marker_name) = LOWER(:markerName)";

	public static final String GET_BY_MARKER_ID = "SELECT DISTINCT marker_id " + ", CONCAT(marker_type, '') "
			+ ", CONCAT(marker_name, '')  " + ", CONCAT(species, '')  " + ", db_accession_id " + ", reference " + ", CONCAT(genotype, '') "
			+ ", ploidy " + ", motif " + ", forward_primer " + ", reverse_primer " + ", product_size " + ", annealing_temp "
			+ ", amplification " + ", CONCAT(principal_investigator, '') " + ", contact " + ", institute " + ", genotypes_count "
			+ "FROM gdms_marker_retrieval_info " + "WHERE marker_id in(:markerList)";

	/* Used by MarkerInfoDAO.countByMarkerName() */
	public static final String COUNT_BY_MARKER_NAME = "SELECT COUNT(*) " + "FROM gdms_marker_retrieval_info "
			+ "WHERE LOWER(marker_name) LIKE LOWER(:markerName)";

	/* Used by MarkerInfoDAO.getByGenotype() */
	public static final String GET_BY_GENOTYPE = "SELECT marker_id " + ", CONCAT(marker_type, '') " + ", CONCAT(marker_name, '')  "
			+ ", CONCAT(species, '')  " + ", db_accession_id " + ", reference " + ", CONCAT(genotype, '') " + ", ploidy " + ", motif "
			+ ", forward_primer " + ", reverse_primer " + ", product_size " + ", annealing_temp " + ", amplification "
			+ ", CONCAT(principal_investigator, '') " + ", contact " + ", institute " + ", genotypes_count "
			+ "FROM gdms_marker_retrieval_info " + "WHERE LOWER(genotype) LIKE LOWER(:genotype)";

	/* Used by MarkerInfoDAO.countByGenotype() */
	public static final String COUNT_BY_GENOTYPE = "SELECT COUNT(*) " + "FROM gdms_marker_retrieval_info "
			+ "WHERE LOWER(genotype) LIKE LOWER(:genotype)";

	/* Used by MarkerInfoDAO.getByDbAccessionId() */
	public static final String GET_BY_DB_ACCESSION_ID = "SELECT marker_id " + ", CONCAT(marker_type, '') " + ", CONCAT(marker_name, '')  "
			+ ", CONCAT(species, '')  " + ", db_accession_id " + ", reference " + ", CONCAT(genotype, '') " + ", ploidy " + ", motif "
			+ ", forward_primer " + ", reverse_primer " + ", product_size " + ", annealing_temp " + ", amplification "
			+ ", CONCAT(principal_investigator, '') " + ", contact " + ", institute " + ", genotypes_count "
			+ "FROM gdms_marker_retrieval_info " + "WHERE LOWER(db_accession_id) LIKE LOWER(:dbAccessionId)";

	/* Used by MarkerInfoDAO.countByDbAccessionId() */
	public static final String COUNT_BY_DB_ACCESSION_ID = "SELECT COUNT(*) " + "FROM gdms_marker_retrieval_info "
			+ "WHERE LOWER(db_accession_id) LIKE LOWER(:dbAccessionId)";

	public MarkerInfoDAO(final Session session) {
		super(session);
	}

	/**
	 * Gets the list of marker info objects corresponding to the given marker name.
	 *
	 * @param markerName the marker name
	 * @param start the start row
	 * @param numOfRows the number of rows to retrieve
	 * @return the list of MarkerInfo objects by marker name
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("rawtypes")
	public List<MarkerInfo> getByMarkerName(String markerName, int start, int numOfRows) throws MiddlewareQueryException {

		if (markerName == null) {
			return new ArrayList<MarkerInfo>();
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerInfoDAO.GET_BY_MARKER_NAME);
			query.setParameter("markerName", markerName);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			List results = query.list();

			ArrayList<MarkerInfo> toReturn = new ArrayList<MarkerInfo>();
			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					toReturn.add(this.convertFromObject(result));
				}
			}
			return toReturn;

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByMarkerName(markerName=" + markerName + ") query from MarkerInfo: " + e.getMessage(),
					e);
		}
		return new ArrayList<MarkerInfo>();
	}

	@SuppressWarnings("rawtypes")
	public List<MarkerInfo> getByMarkerIds(List<Integer> markerList) throws MiddlewareQueryException {
		if (markerList == null || markerList.isEmpty()) {
			return new ArrayList<MarkerInfo>();
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerInfoDAO.GET_BY_MARKER_ID);
			query.setParameterList("markerList", markerList);

			List results = query.list();

			ArrayList<MarkerInfo> toReturn = new ArrayList<MarkerInfo>();
			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					toReturn.add(this.convertFromObject(result));
				}
			}
			return toReturn;

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByMarkerId() query from MarkerInfo: " + e.getMessage(), e);
		}
		return new ArrayList<MarkerInfo>();
	}

	protected MarkerInfo convertFromObject(Object[] result) {
		Integer markerId = (Integer) result[0];
		String markerType = (String) result[1];
		String markerName2 = (String) result[2];
		String species = (String) result[3];
		String accessionId = (String) result[4];
		String reference = (String) result[5];
		String genotype = (String) result[6];
		String ploidy = (String) result[7];
		String motif = (String) result[8];
		String forwardPrimer = (String) result[9];
		String reversePrimer = (String) result[10];
		String productSize = (String) result[11];
		Float annealingTemp = (Float) result[12];
		String amplification = (String) result[13];
		String principalInvestigator = (String) result[14];
		String contact = (String) result[15];
		String institute = (String) result[16];
		BigInteger genotypesCount = (BigInteger) result[17];
		MarkerInfo markerInfo =
				new MarkerInfo(markerId, markerType, markerName2, species, accessionId, reference, genotype, ploidy, motif, forwardPrimer,
						reversePrimer, productSize, annealingTemp, amplification, principalInvestigator, contact, institute, genotypesCount);

		return markerInfo;
	}

	/**
	 * Count the number of entries in marker info corresponding to the given marker name.
	 *
	 * @param markerName the marker name
	 * @return the count
	 * @throws MiddlewareQueryException
	 */
	public long countByMarkerName(String markerName) throws MiddlewareQueryException {

		if (markerName == null) {
			return 0;
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerInfoDAO.COUNT_BY_MARKER_NAME);
			query.setParameter("markerName", markerName);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			}
			return 0;
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countByMarkerName(markerName=" + markerName + ") query from MarkerInfo: " + e.getMessage(), e);
		}
		return 0;
	}

	/**
	 * Gets the list of marker info objects corresponding to the given genotype.
	 *
	 * @param genotype the genotype
	 * @param start the start row
	 * @param numOfRows the number of rows to retrieve
	 * @return the list of MarkerInfo objects by genotype
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("rawtypes")
	public List<MarkerInfo> getByGenotype(String genotype, int start, int numOfRows) throws MiddlewareQueryException {

		if (genotype == null) {
			return new ArrayList<MarkerInfo>();
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerInfoDAO.GET_BY_GENOTYPE);
			query.setParameter("genotype", genotype);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			List results = query.list();

			ArrayList<MarkerInfo> toReturn = new ArrayList<MarkerInfo>();
			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer markerId = (Integer) result[0];
					String markerType = (String) result[1];
					String markerName = (String) result[2];
					String species = (String) result[3];
					String accessionId = (String) result[4];
					String reference = (String) result[5];
					String genotype2 = (String) result[6];
					String ploidy = (String) result[7];
					String motif = (String) result[8];
					String forwardPrimer = (String) result[9];
					String reversePrimer = (String) result[10];
					String productSize = (String) result[11];
					Float annealingTemp = (Float) result[12];
					String amplification = (String) result[13];
					String principalInvestigator = (String) result[14];
					String contact = (String) result[15];
					String institute = (String) result[16];
					BigInteger genotypesCount = (BigInteger) result[17];
					MarkerInfo markerInfo =
							new MarkerInfo(markerId, markerType, markerName, species, accessionId, reference, genotype2, ploidy, motif,
									forwardPrimer, reversePrimer, productSize, annealingTemp, amplification, principalInvestigator,
									contact, institute, genotypesCount);
					toReturn.add(markerInfo);
				}
			}
			return toReturn;
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByGenotype(genotype=" + genotype + ") query from MarkerInfo: " + e.getMessage(), e);
		}
		return new ArrayList<MarkerInfo>();
	}

	/**
	 * Count the number of entries in marker info corresponding to the given genotype.
	 *
	 * @param genotype the genotype
	 * @return the count
	 * @throws MiddlewareQueryException
	 */
	public long countByGenotype(String genotype) throws MiddlewareQueryException {

		if (genotype == null) {
			return 0;
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerInfoDAO.COUNT_BY_GENOTYPE);
			query.setParameter("genotype", genotype);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			}
			return 0;
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByGenotype(genotype=" + genotype + ") query from MarkerInfo: " + e.getMessage(), e);
		}
		return 0L;
	}

	/**
	 * Gets the list of marker info objects corresponding to the given db accession id.
	 *
	 * @param dbAccessionId the db accession id
	 * @param start the start row
	 * @param numOfRows the number of rows to retrieve
	 * @return the list of MarkerInfo objects by db accession id
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("rawtypes")
	public List<MarkerInfo> getByDbAccessionId(String dbAccessionId, int start, int numOfRows) throws MiddlewareQueryException {

		if (dbAccessionId == null) {
			return new ArrayList<MarkerInfo>();
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerInfoDAO.GET_BY_DB_ACCESSION_ID);
			query.setParameter("dbAccessionId", dbAccessionId);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			List results = query.list();

			ArrayList<MarkerInfo> toReturn = new ArrayList<MarkerInfo>();
			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					Integer markerId = (Integer) result[0];
					String markerType = (String) result[1];
					String markerName = (String) result[2];
					String species = (String) result[3];
					String accessionId = (String) result[4];
					String reference = (String) result[5];
					String genotype = (String) result[6];
					String ploidy = (String) result[7];
					String motif = (String) result[8];
					String forwardPrimer = (String) result[9];
					String reversePrimer = (String) result[10];
					String productSize = (String) result[11];
					Float annealingTemp = (Float) result[12];
					String amplification = (String) result[13];
					String principalInvestigator = (String) result[14];
					String contact = (String) result[15];
					String institute = (String) result[16];
					BigInteger genotypesCount = (BigInteger) result[17];
					MarkerInfo markerInfo =
							new MarkerInfo(markerId, markerType, markerName, species, accessionId, reference, genotype, ploidy, motif,
									forwardPrimer, reversePrimer, productSize, annealingTemp, amplification, principalInvestigator,
									contact, institute, genotypesCount);
					toReturn.add(markerInfo);
				}
			}
			return toReturn;
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getByDbAccessionId(dbAccessionId=" + dbAccessionId + ") query from MarkerInfo: " + e.getMessage(), e);
		}
		return new ArrayList<MarkerInfo>();
	}

	/**
	 * Count the number of entries in marker info corresponding to the given db accession id.
	 *
	 * @param dbAccessionId the db accession id
	 * @return the count
	 * @throws MiddlewareQueryException
	 */
	public long countByDbAccessionId(String dbAccessionId) throws MiddlewareQueryException {

		if (dbAccessionId == null) {
			return 0;
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(MarkerInfoDAO.COUNT_BY_DB_ACCESSION_ID);
			query.setParameter("dbAccessionId", dbAccessionId);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			}
			return 0;
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByDbAccessionId(" + dbAccessionId + ") query from MarkerInfo: " + e.getMessage(), e);
		}
		return 0;
	}
}
