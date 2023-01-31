
package org.generationcp.middleware;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GermplasmTestDataGenerator {
	private static final Integer TEST_METHOD_ID = 101;
	public static final String TEST_METHOD_NAME = "Single cross";

	private final HibernateSessionProvider sessionProvider;
	private final DaoFactory daoFactory;

	public GermplasmTestDataGenerator(final HibernateSessionProvider sessionProvider,
		final DaoFactory daoFactory) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = daoFactory;
	}

	public Integer addGermplasm(final Germplasm germplasm, final Name preferredName, final CropType cropType) {
		if (preferredName.getNstat() == null) {
			preferredName.setNstat(1);
		}
		GermplasmGuidGenerator.generateGermplasmGuids(cropType, Arrays.asList(germplasm));
		preferredName.setGermplasm(germplasm);
		germplasm.getNames().clear();
		germplasm.getNames().add(preferredName);
		this.daoFactory.getGermplasmDao().save(germplasm);

		this.sessionProvider.getSession().flush();

		return germplasm.getGid();
	}

	public Germplasm createGermplasmWithPreferredAndNonpreferredNames() {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Name preferredName = germplasm.getPreferredName();
		preferredName.setGermplasm(germplasm);

		this.daoFactory.getGermplasmDao().save(germplasm);
		this.sessionProvider.getSession().flush();
		this.daoFactory.getGermplasmDao().refresh(germplasm);
		this.daoFactory.getNameDao().save(preferredName);

		final UserDefinedField attributeField =
			new UserDefinedField(null, "NAMES", "NAME", RandomStringUtils.randomAlphanumeric(10), "", "", "", 0, 0, 0, 0);
		this.daoFactory.getUserDefinedFieldDAO().saveOrUpdate(attributeField);

		final Name otherName = GermplasmTestDataInitializer.createGermplasmName(germplasm.getGid(), "Other Name ", attributeField.getFldno());
		otherName.setNstat(0);
		this.daoFactory.getNameDao().save(otherName);
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
		germplasm.setMethod(new Method(GermplasmTestDataGenerator.TEST_METHOD_ID));

		this.daoFactory.getGermplasmDao().save(germplasm);
		this.sessionProvider.getSession().flush();
		this.daoFactory.getGermplasmDao().refresh(germplasm);
		this.daoFactory.getNameDao().save(preferredName);

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
			this.daoFactory.getGermplasmDao().save(germplasm);

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
			this.daoFactory.getGermplasmDao().save(germplasm);

			germplasms.add(germplasm);
		}
		return germplasms;
	}

	public Germplasm createGermplasm(final String prefix) throws MiddlewareQueryException {
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasmWithPreferredName(prefix);
		this.daoFactory.getGermplasmDao().save(germplasm);

		return germplasm;
	}
}
