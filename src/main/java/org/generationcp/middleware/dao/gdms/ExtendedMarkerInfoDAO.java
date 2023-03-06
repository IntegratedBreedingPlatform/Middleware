
package org.generationcp.middleware.dao.gdms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.ExtendedMarkerInfo;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 9/4/2014 Time: 11:37 AM
 */
public class ExtendedMarkerInfoDAO extends GenericDAO<ExtendedMarkerInfo, Integer> {

	public static final String SELECT_STRING = "SELECT marker_id " + ", CONCAT(marker_type, '') " + ", CONCAT(marker_name, '')  "
			+ ", CONCAT(species, '')  " + ", db_accession_id " + ", reference " + ", CONCAT(genotype, '') " + ", ploidy " + ", motif "
			+ ", forward_primer " + ", reverse_primer " + ", product_size " + ", annealing_temp " + ", amplification "
			+ ", CONCAT(principal_investigator, '') " + ", contact " + ", institute " + ", genotypes_count " + ", no_of_repeats "
			+ ", CONCAT(motif_type, '') " + ", CONCAT(sequence, '') " + ", sequence_length " + ", min_allele " + ", max_allele "
			+ ", ssr_nr " + ", forward_primer_temp " + ", reverse_primer_temp " + ", elongation_temp " + ", fragment_size_expected "
			+ ", fragment_size_observed " + ", expected_product_size " + ", position_on_reference_sequence "
			+ ", CONCAT(restriction_enzyme_for_assay, '') ";

	public static final String FROM_STRING = "FROM gdms_marker_retrieval_info ";

	public static final String GET_BY_MARKER_TYPE = ExtendedMarkerInfoDAO.SELECT_STRING + ExtendedMarkerInfoDAO.FROM_STRING
			+ " where marker_type = :markerType";

	public static final String COUNT_BY_MARKER_TYPE = "SELECT COUNT(*) " + ExtendedMarkerInfoDAO.FROM_STRING
			+ " where marker_type = :markerType";

	public static final String GET_LIKE_MARKER_NAME = ExtendedMarkerInfoDAO.SELECT_STRING + ExtendedMarkerInfoDAO.FROM_STRING
			+ " where LOWER(marker_name) LIKE LOWER(:markerName)";

	public static final String GET_BY_MARKER_NAMES = ExtendedMarkerInfoDAO.SELECT_STRING + ExtendedMarkerInfoDAO.FROM_STRING
			+ " where marker_name in (:markerNames)";

	public ExtendedMarkerInfoDAO(final Session session) {
		super(session);
	}

	public List<ExtendedMarkerInfo> getByMarkerType(String type/* , int start, int numOfRows */) throws MiddlewareQueryException {
		if (type == null) {
			type = "SSR";
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(ExtendedMarkerInfoDAO.GET_BY_MARKER_TYPE);
			query.setParameter("markerType", type);
			List results = query.list();

			ArrayList<ExtendedMarkerInfo> toReturn = new ArrayList<ExtendedMarkerInfo>();
			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					toReturn.add(this.convertFromObject(result));
				}
			}
			return toReturn;

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getMarkerInfoByMarkerType(markerType=" + type + ") query from ExtendedMarkerInfo: " + e.getMessage(), e);
		}
		return new ArrayList<ExtendedMarkerInfo>();
	}

	public List<ExtendedMarkerInfo> getLikeMarkerName(String partialMarkerName) throws MiddlewareQueryException {
		if (partialMarkerName == null) {
			return new ArrayList<ExtendedMarkerInfo>();
		}

		String param = "%" + partialMarkerName + "%";

		try {
			SQLQuery query = this.getSession().createSQLQuery(ExtendedMarkerInfoDAO.GET_LIKE_MARKER_NAME);
			query.setParameter("markerName", param);
			List results = query.list();

			ArrayList<ExtendedMarkerInfo> toReturn = new ArrayList<ExtendedMarkerInfo>();
			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					toReturn.add(this.convertFromObject(result));
				}
			}
			return toReturn;

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getLikeMarkerName(markerName=" + partialMarkerName + ") query from MarkerInfo: " + e.getMessage(), e);
		}
		return new ArrayList<ExtendedMarkerInfo>();
	}

	public List<ExtendedMarkerInfo> getByMarkerNames(List<String> markerNames) throws MiddlewareQueryException {
		if (markerNames == null || markerNames.isEmpty()) {
			return new ArrayList<ExtendedMarkerInfo>();
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(ExtendedMarkerInfoDAO.GET_BY_MARKER_NAMES);
			query.setParameterList("markerNames", markerNames);
			List results = query.list();

			ArrayList<ExtendedMarkerInfo> toReturn = new ArrayList<ExtendedMarkerInfo>();
			for (Object o : results) {
				Object[] result = (Object[]) o;
				if (result != null) {
					toReturn.add(this.convertFromObject(result));
				}
			}
			return toReturn;

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getByMarkerNames(markerNames=" + markerNames + ") query from MarkerInfo: " + e.getMessage(), e);
		}
		return new ArrayList<ExtendedMarkerInfo>();
	}

	public long countByMarkerType(String markerType) throws MiddlewareQueryException {
		if (markerType == null) {
			markerType = "SSR";
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(ExtendedMarkerInfoDAO.COUNT_BY_MARKER_TYPE);
			query.setParameter("markerType", markerType);
			BigInteger result = (BigInteger) query.uniqueResult();
			if (result != null) {
				return result.longValue();
			}
			return 0;
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countByMarkerType(markerType=" + markerType + ") query from MarkerInfo: " + e.getMessage(), e);
		}

		return 0;
	}

	protected ExtendedMarkerInfo convertFromObject(Object[] result) {
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
		Integer numberOfRepeats = (Integer) result[18];
		String motifType = (String) result[19];
		String sequence = (String) result[20];
		Integer sequenceLength = (Integer) result[21];
		Integer minAllele = (Integer) result[22];
		Integer maxAllele = (Integer) result[23];
		Integer ssrNumber = (Integer) result[24];
		Float forwardPrimerTemp = (Float) result[25];
		Float reversePrimerTemp = (Float) result[26];
		Float elongationTemp = (Float) result[27];
		Integer fragmentSizeExpected = (Integer) result[28];
		Integer fragmentSizeObserved = (Integer) result[29];
		Integer expectedProductSize = (Integer) result[30];
		Integer positionOnSequence = (Integer) result[31];
		String restrictionEnzyme = (String) result[32];

		ExtendedMarkerInfo markerInfo =
				new ExtendedMarkerInfo(markerId, markerType, markerName2, species, accessionId, reference, genotype, ploidy, motif,
						forwardPrimer, reversePrimer, productSize, annealingTemp, amplification, principalInvestigator, contact, institute,
						genotypesCount, numberOfRepeats, motifType, sequence, sequenceLength, minAllele, maxAllele, ssrNumber,
						forwardPrimerTemp, reversePrimerTemp, elongationTemp, fragmentSizeExpected, fragmentSizeObserved,
						expectedProductSize, positionOnSequence, restrictionEnzyme);

		return markerInfo;
	}
}
