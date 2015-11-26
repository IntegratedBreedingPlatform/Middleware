
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.study.ObservationDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.generationcp.middleware.service.api.study.StudySearchParameters;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.generationcp.middleware.service.api.study.TraitDto;
import org.generationcp.middleware.service.api.study.TraitService;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class StudyServiceImpl extends Service implements StudyService {

	private TraitService trialTraits;

	private StudyMeasurements studyMeasurements;

	private StudyGermplasmListService studyGermplasmListService;

	public StudyServiceImpl() {
		super();
	}

	public StudyServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		final Session currentSession = this.getCurrentSession();
		this.trialTraits = new TraitServiceImpl(currentSession);
		this.studyMeasurements = new StudyMeasurements(this.getCurrentSession());
		this.studyGermplasmListService = new StudyGermplasmListServiceImpl(this.getCurrentSession());
	}

	/**
	 * Only used for tests.
	 *
	 * @param trialTraits
	 * @param trialMeasurements
	 */
	StudyServiceImpl(final TraitService trialTraits, final StudyMeasurements trialMeasurements,
			final StudyGermplasmListService studyGermplasmListServiceImpl) {
		this.trialTraits = trialTraits;
		this.studyMeasurements = trialMeasurements;
		this.studyGermplasmListService = studyGermplasmListServiceImpl;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StudySummary> listAllStudies(final StudySearchParameters serchParameters) {

		final List<StudySummary> studySummaries = new ArrayList<StudySummary>();

		String sql =
				"SELECT DISTINCT " + "	p.project_id AS id, " + "	p.name AS name, " + "	p.description AS title, "
						+ "	p.program_uuid AS programUUID, " + "	ppType.value AS studyTypeId, " + "	ppObjective.value AS objective, "
						+ "	ppStartDate.value AS startDate, " + "	ppEndDate.value AS endDate " + " FROM " + "	project p "
						+ "	INNER JOIN projectprop ppType ON p.project_id = ppType.project_id AND ppType.type_id = "
						+ TermId.STUDY_TYPE.getId()
						+ "	LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id AND ppObjective.type_id = "
						+ TermId.STUDY_OBJECTIVE.getId()
						+ "	LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id AND ppStartDate.type_id = "
						+ TermId.START_DATE.getId()
						+ "	LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id AND ppEndDate.type_id = "
						+ TermId.END_DATE.getId() + "	WHERE NOT EXISTS "
						+ "	  (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id = " + TermId.STUDY_STATUS.getId()
						+ "         AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  " + TermId.DELETED_STUDY.getId() + ") ";

		if (!StringUtils.isEmpty(serchParameters.getProgramUniqueId())) {
			sql += " AND p.program_uuid = '" + serchParameters.getProgramUniqueId().trim() + "'";
		}
		sql += " ORDER BY p.name;";

		List<Object[]> list = null;
		try {
			final Query query =
					this.getCurrentSession().createSQLQuery(sql).addScalar("id").addScalar("name").addScalar("title")
					.addScalar("programUUID").addScalar("studyTypeId").addScalar("objective").addScalar("startDate")
					.addScalar("endDate");
			list = query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in listAllStudies() query in StudyServiceImpl: " + e.getMessage(), e);
		}

		if (list != null && !list.isEmpty()) {
			for (final Object[] row : list) {
				final Integer id = (Integer) row[0];
				final String name = (String) row[1];
				final String title = (String) row[2];
				final String programUUID = (String) row[3];
				final String studyTypeId = (String) row[4];
				final String objective = (String) row[5];
				final String startDate = (String) row[6];
				final String endDate = (String) row[7];

				final StudySummary studySummary =
						new StudySummary(id, name, title, objective, StudyType.getStudyTypeById(Integer.valueOf(studyTypeId)), startDate,
								endDate, programUUID);
				studySummaries.add(studySummary);
			}
		}
		return studySummaries;
	}

	@Override
	public List<ObservationDto> getObservations(final int studyIdentifier) {

		final List<TraitDto> traits = this.trialTraits.getTraits(studyIdentifier);

		return this.studyMeasurements.getAllMeasurements(studyIdentifier, traits);
	}

	@Override
	public List<ObservationDto> getSingleObservation(final int studyIdentifier, final int measurementIdentifier) {

		final List<TraitDto> traits = this.trialTraits.getTraits(studyIdentifier);

		return this.studyMeasurements.getMeasurement(studyIdentifier, traits, measurementIdentifier);

	}

	@Override
	public ObservationDto updataObservation(final Integer studyIdentifier, final ObservationDto middlewareMeasurement) {

		final Session currentSession = this.getCurrentSession();
		final Observations observations = new Observations(currentSession);
		try {
			final ObservationDto updatedMeasurement = observations.updataObsevationTraits(middlewareMeasurement);
			return updatedMeasurement;
		} catch (final RuntimeException e) {
			throw e; // or display error message
		}
	}

	@Override
	public List<StudyGermplasmDto> getStudyGermplasmList(final Integer studyIdentifer) {
		return this.studyGermplasmListService.getGermplasmList(studyIdentifer);
	}
}
