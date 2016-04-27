
package org.generationcp.middleware.operation.saver;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhenotypeOutlierSaver extends Saver {

	private static final Logger LOG = LoggerFactory.getLogger(PhenotypeOutlierSaver.class);

	public PhenotypeOutlierSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void savePhenotypeOutliers(List<PhenotypeOutlier> phenotypeOutliers) {

		for (PhenotypeOutlier phenotypeOutlier : phenotypeOutliers) {
			this.getPhenotypeOutlierDao().save(phenotypeOutlier);
		}

	}

}
