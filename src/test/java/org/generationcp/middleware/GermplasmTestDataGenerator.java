
package org.generationcp.middleware;

import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;

public class GermplasmTestDataGenerator {
	public static final Integer TEST_METHOD_ID = 101;
	public static final String TEST_METHOD_NAME = "Single cross";

	GermplasmDataManager germplasmDataManager;

	public GermplasmTestDataGenerator(final GermplasmDataManager manager) {
		this.germplasmDataManager = manager;
	}

	public Germplasm createGermplasmWithPreferredAndNonpreferredNames() {
		final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName();
		final Name preferredName = germplasm.getPreferredName();
		preferredName.setGermplasmId(germplasm.getGid());
		this.germplasmDataManager.addGermplasm(germplasm, preferredName);

		final Name otherName = GermplasmTestDataInitializer.createGermplasmName(germplasm.getGid(), "Other Name ");
		otherName.setNstat(0);
		this.germplasmDataManager.addGermplasmName(otherName);

		return germplasm;
	}

	public Germplasm createChildGermplasm(final Germplasm parentGermplasm, final String name) {
		final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName(name);
		final Name preferredName = germplasm.getPreferredName();
		preferredName.setGermplasmId(germplasm.getGid());

		germplasm.setGpid1(parentGermplasm.getGid());
		germplasm.setGpid2(parentGermplasm.getGid());
		germplasm.setMethodId(GermplasmTestDataGenerator.TEST_METHOD_ID);

		this.germplasmDataManager.addGermplasm(germplasm, preferredName);

		return germplasm;
	}

	public Integer[] createChildrenGermplasm(final int numberOfChildGermplasm, final String prefix,
			final Germplasm parentGermplasm) throws MiddlewareQueryException {
		final Integer[] gids = new Integer[numberOfChildGermplasm];
		for (int i = 0; i < numberOfChildGermplasm; i++) {
			final String name = prefix + i;
			final Germplasm germplasm = this.createChildGermplasm(parentGermplasm, name);
			gids[i] = germplasm.getGid();
		}
		return gids;
	}

	public Integer[] createGermplasmRecords(final int numberOfGermplasm, final String prefix)
			throws MiddlewareQueryException {
		final Integer[] gids = new Integer[numberOfGermplasm];
		for (int i = 0; i < numberOfGermplasm; i++) {
			final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName(prefix + i);
			final Name preferredName = germplasm.getPreferredName();
			preferredName.setGermplasmId(germplasm.getGid());
			this.germplasmDataManager.addGermplasm(germplasm, preferredName);

			gids[i] = germplasm.getGid();
		}
		return gids;
	}

}
