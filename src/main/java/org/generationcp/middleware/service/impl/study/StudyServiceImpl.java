package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.study.Measurement;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.StudySummary;
import org.generationcp.middleware.service.api.study.Trait;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.repackage.cglib.asm.Type;

public class StudyServiceImpl extends Service implements StudyService {

	public StudyServiceImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StudySummary> listAllStudies() throws MiddlewareQueryException {

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
				+ "	INNER JOIN projectprop ppType ON p.project_id = ppType.project_id AND ppType.type_id = "
				+ TermId.STUDY_TYPE.getId()
				+ "	LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id AND ppObjective.type_id = "
				+ TermId.STUDY_OBJECTIVE.getId()
				+ "	LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id AND ppStartDate.type_id = "
				+ TermId.START_DATE.getId()
				+ "	LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id AND ppEndDate.type_id = "
				+ TermId.END_DATE.getId() + "	WHERE NOT EXISTS "
				+ "	  (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id = "
				+ TermId.STUDY_STATUS.getId()
				+ "         AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  "
				+ TermId.DELETED_STUDY.getId() + ")" + "	ORDER BY p.name; ";

		List<Object[]> list = null;
		try {
			Query query = getCurrentSession().createSQLQuery(sql).addScalar("id").addScalar("name")
					.addScalar("title").addScalar("programUUID").addScalar("studyTypeId")
					.addScalar("objective").addScalar("startDate").addScalar("endDate");
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
						StudyType.getStudyTypeById(Integer.valueOf(studyTypeId)), startDate,
						endDate, programUUID);
				studySummaries.add(studySummary);
			}
		}
		return studySummaries;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measurement> listTrialData(final int projectBusinessIdentifier) {

		final MeasurementQuery measurementQuery = new MeasurementQuery();
		List<String> projectTraits = getTraits(projectBusinessIdentifier);
		String generateQuery = measurementQuery.generateQuery(projectTraits);
		System.out.println(generateQuery);
		SQLQuery createSQLQuery = getCurrentSession().createSQLQuery(generateQuery)
				.addScalar("nd_experiment_id").addScalar("TRIAL_INSTANCE").addScalar("ENTRY_TYPE")
				.addScalar("GID").addScalar("DESIGNATION").addScalar("ENTRY_NO")
				.addScalar("SEED_SOURCE").addScalar("REP_NO").addScalar("PLOT_NO");
		for (final String trait : projectTraits) {
			createSQLQuery.addScalar(trait);
			createSQLQuery.addScalar(trait+"_PhenotypeId");

		}
		int counter = 0;
		for (final String trait : projectTraits) {
			createSQLQuery.setParameter(counter++, trait);
		}
		createSQLQuery.setParameter(counter++, projectBusinessIdentifier);

		List<Object[]> list = createSQLQuery.list();

		final List<Measurement> traits = new ArrayList<Measurement>();

		if (list != null && !list.isEmpty()) {
			for (Object[] row : list) {

				final List<Trait> stuff = new ArrayList<Trait>();
				int counterTwo = 1;
				for (final String trait : projectTraits) {
					stuff.add(new Trait(trait, (Integer) row[(8 + counterTwo+1)], (String) row[(8 + counterTwo)]));
					counterTwo+=2;
				}
				Measurement measurement = new Measurement((Integer) row[0], (String) row[1],
						(String) row[2], (Integer) row[3], (String) row[4], (String) row[5],
						(String) row[6], (String) row[7], (String) row[7], stuff);
				traits.add(measurement);

			}
		}
		return traits;

	}

	private List<String> getTraits(final int projectBusinessIdentifier) {
		final TraitNamesQuery traitQuery = new TraitNamesQuery();
		final String traitsInProjectQuery = traitQuery.generateQuery();
		final SQLQuery createSQLQuery = getCurrentSession().createSQLQuery(traitsInProjectQuery)
				.addScalar("value");
		createSQLQuery.setParameter(0, projectBusinessIdentifier);
		final List<Object> list = createSQLQuery.list();
		final List<String> traits = new ArrayList<String>();
		if (list != null && !list.isEmpty()) {
			for (Object row : list) {
				traits.add((String) row);
			}
		}
		return traits;
	}
}
