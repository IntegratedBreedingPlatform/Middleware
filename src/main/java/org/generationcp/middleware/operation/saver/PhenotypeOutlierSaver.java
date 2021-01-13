
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PhenotypeOutlierSaver extends Saver {

	private DaoFactory daoFactory;

	private static final Logger LOG = LoggerFactory.getLogger(PhenotypeOutlierSaver.class);

	public PhenotypeOutlierSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		daoFactory = new DaoFactory(sessionProvider);
	}

	public void savePhenotypeOutliers(List<PhenotypeOutlier> phenotypeOutliers) {

		for (PhenotypeOutlier phenotypeOutlier : phenotypeOutliers) {
			this.daoFactory.getPhenotypeOutlierDao().save(phenotypeOutlier);
		}

	}

}
