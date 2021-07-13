
package org.generationcp.middleware;

import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.ArrayList;
import java.util.List;

public class GermplasmTestDataGenerator {
	private static final Integer TEST_METHOD_ID = 101;
	public static final String TEST_METHOD_NAME = "Single cross";

	private final GermplasmDataManager germplasmDataManager;
	private final NameDAO nameDAO;

	public GermplasmTestDataGenerator(final GermplasmDataManager manager, final NameDAO nameDAO) {
		this.germplasmDataManager = manager;
		this.nameDAO = nameDAO;
	}

	public Germplasm createGermplasmWithPreferredAndNonpreferredNames() {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Name preferredName = germplasm.getPreferredName();
		preferredName.setGermplasm(germplasm);
		this.germplasmDataManager.addGermplasm(germplasm, preferredName, cropType);

		final Name otherName = GermplasmTestDataInitializer.createGermplasmName(germplasm.getGid(), "Other Name ");
		otherName.setNstat(0);
		this.nameDAO.save(otherName);

		return germplasm;
	}

	public Germplasm createChildGermplasm(final Germplasm parentGermplasm, final String name) {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasmWithPreferredName(name);
		final Name preferredName = germplasm.getPreferredName();
		preferredName.setGermplasm(germplasm);

		germplasm.setGpid1(parentGermplasm.getGid());
		germplasm.setGpid2(parentGermplasm.getGid());
		germplasm.setMethodId(GermplasmTestDataGenerator.TEST_METHOD_ID);

		this.germplasmDataManager.addGermplasm(germplasm, preferredName, cropType);

		return germplasm;
	}

	public Integer[] createChildrenGermplasm(final int numberOfChildGermplasm, final String prefix,
			final Germplasm parentGermplasm) throws MiddlewareQueryException {
		final Integer[] gids = new Integer[numberOfChildGermplasm];
		for (int i = 0; i < numberOfChildGermplasm; i++) {
			final String name = prefix + (i+1);
			final Germplasm germplasm = this.createChildGermplasm(parentGermplasm, name);
			gids[i] = germplasm.getGid();
		}
		return gids;
	}

	Integer[] createGermplasmRecords(final int numberOfGermplasm, final String prefix)
			throws MiddlewareQueryException {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		final Integer[] gids = new Integer[numberOfGermplasm];
		for (int i = 0; i < numberOfGermplasm; i++) {
			final Germplasm germplasm = new GermplasmTestDataInitializer().createGermplasmWithPreferredName(prefix + i);
			final Name preferredName = germplasm.getPreferredName();
			preferredName.setGermplasm(germplasm);
			this.germplasmDataManager.addGermplasm(germplasm, preferredName, cropType);

			gids[i] = germplasm.getGid();
		}
		return gids;
	}

	public List<Germplasm> createGermplasmsList(final int numberOfGermplasm, final String prefix) throws MiddlewareQueryException {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);

		final List<Germplasm> germplasms = new ArrayList<>();

		for (int i = 0; i < numberOfGermplasm; i++) {
			final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasmWithPreferredName(prefix + i);
			final Name preferredName = germplasm.getPreferredName();
			preferredName.setGermplasm(germplasm);
			this.germplasmDataManager.addGermplasm(germplasm, preferredName, cropType);

			germplasms.add(germplasm);
		}
		return germplasms;
	}

	public Germplasm createGermplasm(final String prefix) throws MiddlewareQueryException {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasmWithPreferredName(prefix);
		final Name preferredName = germplasm.getPreferredName();
		this.germplasmDataManager.addGermplasm(germplasm, preferredName, cropType);

		return germplasm;
	}
}
