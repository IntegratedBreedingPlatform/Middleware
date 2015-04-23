package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.study.Measurement;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class StudyServiceImpl extends Service implements StudyService {

	final TraitServiceImpl trialTraits;

	final TrialMeasurements trialMeasurements;

	public StudyServiceImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		trialTraits = new TraitServiceImpl(getCurrentSession());
		trialMeasurements = new TrialMeasurements(getCurrentSession());
	}

	/**
	 * Only used for tests.
	 * @param trialTraits
	 * @param trialMeasurements
	 */
	StudyServiceImpl(final TraitServiceImpl trialTraits, final TrialMeasurements trialMeasurements) {
		this.trialTraits = trialTraits;
		this.trialMeasurements = trialMeasurements;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<StudySummary> listAllStudies(final String programUniqueId) throws MiddlewareQueryException {

		List<StudySummary> studySummaries = new ArrayList<StudySummary>();

		String sql = "SELECT DISTINCT "
				+ "	p.project_id AS id, "
				+ "	p.name AS name, "
				+ "	p.description AS title, "
				+ "	p.program_uuid AS programUUID, "
				+ "	ppType.value AS studyTypeId, "
				+ "	ppObjective.value AS objective, "
				+ "	ppStartDate.value AS startDate, "
				+ "	ppEndDate.value AS endDate "
				+ " FROM "
				+ "	project p "
				+ "	INNER JOIN projectprop ppType ON p.project_id = ppType.project_id AND ppType.type_id = " + TermId.STUDY_TYPE.getId()
				+ "	LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id AND ppObjective.type_id = " + TermId.STUDY_OBJECTIVE.getId()
				+ "	LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id AND ppStartDate.type_id = " + TermId.START_DATE.getId()
				+ "	LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id AND ppEndDate.type_id = " + TermId.END_DATE.getId() 
				+ "	WHERE NOT EXISTS "
				+ "	  (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id = "+ TermId.STUDY_STATUS.getId()
				+ "         AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  " + TermId.DELETED_STUDY.getId() + ") ";
		
		if (!StringUtils.isEmpty(programUniqueId)) {
			sql += " AND p.program_uuid = '" + programUniqueId.trim() + "'";
		}
		sql += " ORDER BY p.name;";

		List<Object[]> list = null;
		try {
			Query query = getCurrentSession().createSQLQuery(sql)
					.addScalar("id")
					.addScalar("name")
					.addScalar("title")
					.addScalar("programUUID")
					.addScalar("studyTypeId")
					.addScalar("objective")
					.addScalar("startDate")
					.addScalar("endDate");
			list = query.list();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException(
					"Error in listAllStudies() query in StudyServiceImpl: " + e.getMessage(), e);
		}

		if (list != null && !list.isEmpty()) {
			for (Object[] row : list) {
				Integer id = (Integer) row[0];
				String name = (String) row[1];
				String title = (String) row[2];
				String programUUID = (String) row[3];
				String studyTypeId = (String) row[4];
				String objective = (String) row[5];
				String startDate = (String) row[6];
				String endDate = (String) row[7];

				StudySummary studySummary = new StudySummary(id, name, title, objective,
						StudyType.getStudyTypeById(Integer.valueOf(studyTypeId)), startDate, endDate, programUUID);
				studySummaries.add(studySummary);
			}
		}
		return studySummaries;
	}

	@Override
	public List<Measurement> getMeasurements(final int studyIdentifier) {

		final List<String> traits = trialTraits.getTraits(studyIdentifier);

		return trialMeasurements.getAllMeasurements(studyIdentifier, traits);
	}

}
