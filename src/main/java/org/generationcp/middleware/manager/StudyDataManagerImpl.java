/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.CharacterDataDAO;
import org.generationcp.middleware.dao.CharacterLevelDAO;
import org.generationcp.middleware.dao.FactorDAO;
import org.generationcp.middleware.dao.NumericDataDAO;
import org.generationcp.middleware.dao.NumericLevelDAO;
import org.generationcp.middleware.dao.OindexDAO;
import org.generationcp.middleware.dao.RepresentationDAO;
import org.generationcp.middleware.dao.StudyDAO;
import org.generationcp.middleware.dao.StudyEffectDAO;
import org.generationcp.middleware.dao.VariateDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.CharacterDataElement;
import org.generationcp.middleware.pojos.CharacterLevelElement;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.NumericDataElement;
import org.generationcp.middleware.pojos.NumericLevelElement;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.generationcp.middleware.pojos.Variate;
import org.generationcp.middleware.util.HibernateUtil;

public class StudyDataManagerImpl extends DataManager<Study> implements
	StudyDataManager {

    public StudyDataManagerImpl(HibernateUtil hibernateUtilForLocal,
	    HibernateUtil hibernateUtilForCentral) {
	super(hibernateUtilForLocal, hibernateUtilForCentral);
    }

    @Override
    public List<Integer> getGIDSByPhenotypicData(
	    List<TraitCombinationFilter> filters, int start, int numOfRows,
	    Database instance) throws QueryException {
	// TODO Local-Central: Verify if existing implementation for CENTRAL is
	// also applicable to LOCAL
	HibernateUtil hibernateUtil = getHibernateUtil(instance);

	if (hibernateUtil != null) {

	    NumericDataDAO dataNDao = new NumericDataDAO();
	    dataNDao.setSession(hibernateUtil.getCurrentSession());

	    CharacterDataDAO dataCDao = new CharacterDataDAO();
	    dataCDao.setSession(hibernateUtil.getCurrentSession());

	    Set<Integer> ounitIds = new HashSet<Integer>();

	    // first get the observation unit ids for the rows in datasets which
	    // has the data specified in the filter
	    // check numeric data
	    ounitIds.addAll(dataNDao
		    .getObservationUnitIdsByTraitScaleMethodAndValueCombinations(
			    filters, start, numOfRows));
	    // check character data
	    ounitIds.addAll(dataCDao
		    .getObservationUnitIdsByTraitScaleMethodAndValueCombinations(
			    filters, start, numOfRows));

	    // use the retrieved observation unit ids to get the GIDs being
	    // observed in the rows in datasets identified by the
	    // observation unit ids
	    if (!ounitIds.isEmpty()) {
		FactorDAO factorDao = new FactorDAO();
		factorDao.setSession(hibernateUtil.getCurrentSession());

		Set<Integer> gids = factorDao.getGIDSGivenObservationUnitIds(
			ounitIds, start, numOfRows * 2);
		List<Integer> toreturn = new ArrayList<Integer>();
		toreturn.addAll(gids);
		return toreturn;
	    } else {
		return new ArrayList<Integer>();
	    }

	} else {
	    return new ArrayList<Integer>();
	}

    }

    @Override
    public List<Study> findStudyByName(String name, int start, int numOfRows,
	    Operation op, Database instance) throws QueryException {

	StudyDAO dao = new StudyDAO();
	HibernateUtil hibernateUtil = getHibernateUtil(instance);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<Study>();
	}

	List<Study> studyList = null;
	if (op == Operation.EQUAL) {
	    studyList = dao.findByNameUsingEqual(name, start, numOfRows);
	} else if (op == Operation.LIKE) {
	    studyList = dao.findByNameUsingLike(name, start, numOfRows);
	}

	return studyList;

    }

    @Override
    public int countStudyByName(String name, Operation op, Database instance)
	    throws QueryException {

	StudyDAO dao = new StudyDAO();
	HibernateUtil hibernateUtil = getHibernateUtil(instance);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return 0;
	}

	return dao.countByName(name, op);

    }

    @Override
    public Study getStudyByID(Integer id) throws QueryException {
	StudyDAO dao = new StudyDAO();
	HibernateUtil hibernateUtil = getHibernateUtil(id);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return null;
	}

	Study study = dao.findById(id, false);
	return study;
    }

    @Override
    public List<Study> getAllTopLevelStudies(int start, int numOfRows,
	    Database instance) throws QueryException {
	StudyDAO dao = new StudyDAO();

	HibernateUtil hibernateUtil = getHibernateUtil(instance);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<Study>();
	}

	List<Study> topLevelStudies = dao.getTopLevelStudies(start, numOfRows);

	return topLevelStudies;
    }

    @Override
    public List<Study> getStudiesByParentFolderID(Integer parentFolderId,
	    int start, int numOfRows) throws QueryException {
	StudyDAO dao = new StudyDAO();

	HibernateUtil hibernateUtil = getHibernateUtil(parentFolderId);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<Study>();
	}

	List<Study> studies = dao.getByParentFolderID(parentFolderId, start,
		numOfRows);

	return studies;
    }

    @Override
    public List<Variate> getVariatesByStudyID(Integer studyId)
	    throws QueryException {

	VariateDAO variateDao = new VariateDAO();
	HibernateUtil hibernateUtil = getHibernateUtil(studyId);

	if (hibernateUtil != null) {
	    variateDao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<Variate>();
	}

	List<Variate> variates = variateDao.getByStudyID(studyId);
	return variates;

    }

    @Override
    public List<StudyEffect> getEffectsByStudyID(Integer studyId)
	    throws QueryException {

	StudyEffectDAO studyEffectDao = new StudyEffectDAO();
	HibernateUtil hibernateUtil = getHibernateUtil(studyId);

	if (hibernateUtil != null) {
	    studyEffectDao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<StudyEffect>();
	}

	List<StudyEffect> studyEffect = studyEffectDao.getByStudyID(studyId);
	return studyEffect;

    }

    @Override
    public List<Factor> getFactorsByStudyID(Integer studyId)
	    throws QueryException {

	FactorDAO factorDao = new FactorDAO();
	HibernateUtil hibernateUtil = getHibernateUtil(studyId);

	if (hibernateUtil != null) {
	    factorDao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<Factor>();
	}

	List<Factor> factors = factorDao.getByStudyID(studyId);
	return factors;
    }

    @Override
    public List<Representation> getRepresentationByEffectID(Integer effectId)
	    throws QueryException {
	RepresentationDAO representationDao = new RepresentationDAO();
	HibernateUtil hibernateUtil = getHibernateUtil(effectId);

	if (hibernateUtil != null) {
	    representationDao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<Representation>();
	}

	List<Representation> representations = representationDao
		.getRepresentationByEffectID(effectId);
	return representations;
    }

    @Override
    public List<Factor> getFactorsByRepresentationId(Integer representationId)
	    throws QueryException {
	FactorDAO dao = new FactorDAO();

	HibernateUtil hibernateUtil = getHibernateUtil(representationId);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<Factor>();
	}

	List<Factor> factors = dao.getByRepresentationID(representationId);

	return factors;
    }

    @Override
    public Long countOunitIDsByRepresentationId(Integer representationId)
	    throws QueryException {
	OindexDAO dao = new OindexDAO();

	HibernateUtil hibernateUtil = getHibernateUtil(representationId);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new Long(0);
	}

	Long ounitIdCount = dao
		.countOunitIDsByRepresentationId(representationId);

	return ounitIdCount;
    }

    @Override
    public List<Integer> getOunitIDsByRepresentationId(
	    Integer representationId, int start, int numOfRows)
	    throws QueryException {
	OindexDAO dao = new OindexDAO();

	HibernateUtil hibernateUtil = getHibernateUtil(representationId);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<Integer>();
	}

	List<Integer> ounitIDs = dao.getOunitIDsByRepresentationId(
		representationId, start, numOfRows);

	return ounitIDs;
    }

    @Override
    public List<Variate> getVariatesByRepresentationId(Integer representationId)
	    throws QueryException {
	VariateDAO dao = new VariateDAO();

	HibernateUtil hibernateUtil = getHibernateUtil(representationId);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<Variate>();
	}

	List<Variate> variates = dao.getByRepresentationId(representationId);

	return variates;
    }

    @Override
    public List<NumericDataElement> getNumericDataValuesByOunitIdList(
	    List<Integer> ounitIdList) throws QueryException {
	NumericDataDAO dao = new NumericDataDAO();

	// get 1st element from list to check whether the list is for the
	// Central instance or the Local instance
	Integer sampleId = ounitIdList.get(0);
	HibernateUtil hibernateUtil = getHibernateUtil(sampleId);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<NumericDataElement>();
	}

	List<NumericDataElement> numDataValues = dao
		.getValuesByOunitIDList(ounitIdList);

	return numDataValues;
    }

    @Override
    public List<CharacterDataElement> getCharacterDataValuesByOunitIdList(
	    List<Integer> ounitIdList) throws QueryException {
	CharacterDataDAO dao = new CharacterDataDAO();

	// get 1st element from list to check whether the list is for the
	// Central instance or the Local instance
	Integer sampleId = ounitIdList.get(0);
	HibernateUtil hibernateUtil = getHibernateUtil(sampleId);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<CharacterDataElement>();
	}

	List<CharacterDataElement> charDataValues = dao
		.getValuesByOunitIDList(ounitIdList);

	return charDataValues;
    }

    @Override
    public List<NumericLevelElement> getNumericLevelValuesByOunitIdList(
	    List<Integer> ounitIdList) throws QueryException {
	NumericLevelDAO dao = new NumericLevelDAO();

	// get 1st element from list to check whether the list is for the
	// Central instance or the Local instance
	Integer sampleId = ounitIdList.get(0);
	HibernateUtil hibernateUtil = getHibernateUtil(sampleId);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<NumericLevelElement>();
	}

	List<NumericLevelElement> numLevelValues = dao
		.getValuesByOunitIDList(ounitIdList);

	return numLevelValues;
    }

    @Override
    public List<CharacterLevelElement> getCharacterLevelValuesByOunitIdList(
	    List<Integer> ounitIdList) throws QueryException {
	CharacterLevelDAO dao = new CharacterLevelDAO();

	// get 1st element from list to check whether the list is for the
	// Central instance or the Local instance
	Integer sampleId = ounitIdList.get(0);
	HibernateUtil hibernateUtil = getHibernateUtil(sampleId);

	if (hibernateUtil != null) {
	    dao.setSession(hibernateUtil.getCurrentSession());
	} else {
	    return new ArrayList<CharacterLevelElement>();
	}

	List<CharacterLevelElement> charLevelValues = dao
		.getValuesByOunitIDList(ounitIdList);

	return charLevelValues;
    }

}