
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.h2h.GermplasmLocationInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BreedersQueryDao {

	private static final Logger LOG = LoggerFactory.getLogger(BreedersQueryDao.class);
	private final Session session;

	public BreedersQueryDao(Session session) {
		assert session != null : "Hibernate session is required to instantiare BreedersQueryDao.";
		this.session = session;
	}

	public List<GermplasmLocationInfo> getGermplasmLocationInfoByInstanceIds(Set<Integer> instanceIds)
			throws MiddlewareQueryException {

		List<GermplasmLocationInfo> result = new ArrayList<GermplasmLocationInfo>();
		if (instanceIds != null && !instanceIds.isEmpty()) {
			long startTime = System.nanoTime();
			try {
				SQLQuery query =
						this.session.createSQLQuery("SELECT gtd.envt_id, gtd.gid, gtd.entry_designation, tsl.locationName, tsl.isoabbr "
								+ " FROM germplasm_trial_details gtd " + " join trial_study_locations tsl on gtd.envt_id=tsl.envtId "
								+ " where gtd.envt_id in (:envIds) GROUP BY (gid);");
				query.setParameterList("envIds", instanceIds);

				@SuppressWarnings("rawtypes")
				List queryResult = query.list();

				for (Object qResult : queryResult) {
					Object[] row = (Object[]) qResult;
					result.add(new GermplasmLocationInfo((Integer) row[0], (Integer) row[1], (String) row[2], (String) row[3],
							(String) row[4]));
				}
			} catch (HibernateException he) {
				throw new MiddlewareQueryException(String.format("Hibernate error occured. Cause: %s", he.getCause().getMessage()));
			}

			long elapsedTime = System.nanoTime() - startTime;
			BreedersQueryDao.LOG.debug(String.format("Time taken: %f ms.", (double) elapsedTime / 1000000L));
		}
		return result;
	}

	public List<Integer> getTrialInstanceIdsForGermplasm(Set<Integer> gids) throws MiddlewareQueryException {
		List<Integer> result = new ArrayList<Integer>();
		if (gids != null && !gids.isEmpty()) {
			long startTime = System.nanoTime();
			try {
				SQLQuery query =
						this.session.createSQLQuery("SELECT DISTINCT gtd.envt_id FROM germplasm_trial_details gtd "
								+ "where gtd.gid in (:gids);");
				query.setParameterList("gids", gids);

				@SuppressWarnings("rawtypes")
				List queryResult = query.list();

				for (Object qResult : queryResult) {
					result.add((Integer) qResult);
				}
			} catch (HibernateException he) {
				throw new MiddlewareQueryException(String.format("Hibernate error occured. Cause: %s", he.getCause().getMessage()));
			}

			long elapsedTime = System.nanoTime() - startTime;
			BreedersQueryDao.LOG.debug(String.format("Time taken: %f ms.", (double) elapsedTime / 1000000L));
		}
		return result;
	}
}
