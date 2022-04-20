package org.generationcp.middleware.dao;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.uid.FileUIDGenerator;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FileMetadataDAOTest extends IntegrationTestBase {

	private FileMetadataDAO fileMetadataDAO;
	private CVTermDao cvTermDao;
	private GermplasmDAO germplasmDao;

	private FileMetadata fileMetadata1;
	private CVTerm cvterm1;
	private CropType cropType;
	private Germplasm germplasm1;
	private CVTerm cvterm2;
	private FileMetadata fileMetadata2;
	private FileMetadata fileMetadata3;

	@Before
	public void setup() {
		final Session session = this.sessionProvder.getSession();

		if (this.fileMetadataDAO == null) {
			this.fileMetadataDAO = new FileMetadataDAO(session);
		}
		if (this.cvTermDao == null) {
			this.cvTermDao = new CVTermDao();
			this.cvTermDao.setSession(session);
		}
		if (this.germplasmDao == null) {
			this.germplasmDao = new GermplasmDAO(session);
		}

		this.cvterm1 = new CVTerm(null, CvId.VARIABLES.getId(), randomAlphabetic(10), randomAlphabetic(10), null, 0, 0, false);
		this.cvTermDao.save(this.cvterm1);
		this.cvterm2 = new CVTerm(null, CvId.VARIABLES.getId(), randomAlphabetic(10), randomAlphabetic(10), null, 0, 0, false);
		this.cvTermDao.save(this.cvterm2);

		this.cropType = new CropType();
		this.cropType.setPlotCodePrefix(randomAlphanumeric(4));
		this.cropType.setCropName("maize");
		this.germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20180909, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		GermplasmGuidGenerator.generateGermplasmGuids(this.cropType, singletonList(this.germplasm1));

		this.germplasmDao.save(this.germplasm1);

		this.fileMetadata1 = new FileMetadata();
		final List<CVTerm> variables1 = new ArrayList<>();
		variables1.add(this.cvterm1);
		this.fileMetadata1.setVariables(variables1);
		this.fileMetadata1.setGermplasm(this.germplasm1);
		FileUIDGenerator.generate(this.cropType, singletonList(this.fileMetadata1));
		this.fileMetadataDAO.save(this.fileMetadata1);
		
		this.fileMetadata2 = new FileMetadata();
		final List<CVTerm> variables2 = new ArrayList<>();
		variables2.add(this.cvterm1);
		this.fileMetadata2.setVariables(variables2);
		this.fileMetadata2.setGermplasm(this.germplasm1);
		FileUIDGenerator.generate(this.cropType, singletonList(this.fileMetadata2));
		this.fileMetadataDAO.save(this.fileMetadata2);
		
		this.fileMetadata3 = new FileMetadata();
		final List<CVTerm> variables3 = new ArrayList<>();
		variables3.add(this.cvterm2);
		this.fileMetadata3.setVariables(variables3);
		this.fileMetadata3.setGermplasm(this.germplasm1);
		FileUIDGenerator.generate(this.cropType, singletonList(this.fileMetadata3));
		this.fileMetadataDAO.save(this.fileMetadata3);
		
		this.sessionProvder.getSession().flush();
	}

	@Test
	public void testDetachFiles() {
		final FileMetadata fileMetadata = this.fileMetadataDAO.getById(this.fileMetadata1.getFileId());
		final List<CVTerm> variables = fileMetadata.getVariables();
		assertThat(variables, hasSize(1));
		assertThat(variables, hasItems(
			hasProperty("cvTermId", is(this.cvterm1.getCvTermId()))
		));
		assertThat(fileMetadata.getGermplasm().getGermplasmUUID(), is(this.germplasm1.getGermplasmUUID()));

		this.fileMetadataDAO.detachFiles(singletonList(this.cvterm1.getCvTermId()), null, this.germplasm1.getGermplasmUUID(), null);
		this.sessionProvder.getSession().refresh(this.fileMetadata1);

		final FileMetadata fileMetadataWithoutVariables = this.fileMetadataDAO.getByFileUUID(this.fileMetadata1.getFileUUID());
		assertThat(fileMetadataWithoutVariables.getVariables(), empty());

		final FileMetadata fileUntouched = this.fileMetadataDAO.getByFileUUID(this.fileMetadata3.getFileUUID());
		assertThat(fileUntouched.getVariables(), hasItems(
			hasProperty("cvTermId", is(this.cvterm2.getCvTermId()))
		));
		assertThat(fileUntouched.getGermplasm().getGermplasmUUID(), is(this.germplasm1.getGermplasmUUID()));
	}

	@Test
	public void testRemoveFiles() {
		this.sessionProvder.getSession().refresh(this.fileMetadata1);
		final FileMetadata fileMetadata = this.fileMetadataDAO.getById(this.fileMetadata1.getFileId());
		final List<CVTerm> variables = fileMetadata.getVariables();
		assertThat(variables, hasSize(1));
		assertThat(variables, hasItems(
			hasProperty("cvTermId", is(this.cvterm1.getCvTermId()))
		));
		assertThat(fileMetadata.getGermplasm().getGermplasmUUID(), is(this.germplasm1.getGermplasmUUID()));

		this.fileMetadataDAO.removeFiles(singletonList(this.cvterm1.getCvTermId()), null, this.germplasm1.getGermplasmUUID(), null);

		final FileMetadata fileMetadataWithoutVariables = this.fileMetadataDAO.getByFileUUID(this.fileMetadata1.getFileUUID());
		assertThat(fileMetadataWithoutVariables, nullValue());

		final FileMetadata fileUntouched = this.fileMetadataDAO.getByFileUUID(this.fileMetadata3.getFileUUID());
		assertThat(fileUntouched.getVariables(), hasItems(
			hasProperty("cvTermId", is(this.cvterm2.getCvTermId()))
		));
		assertThat(fileUntouched.getGermplasm().getGermplasmUUID(), is(this.germplasm1.getGermplasmUUID()));
	}

	@Test
  public void testGetByGidsAndUpdateGid() {
		assertThat(this.fileMetadataDAO.getByGids(Arrays.asList(this.germplasm1.getGid())), hasSize(3));

		final Germplasm germplasm = GermplasmTestDataInitializer
				.createGermplasm(20180909, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmDao.save(germplasm);
		final List<String> targetFileUUIDs = new ArrayList();
		targetFileUUIDs.add(this.fileMetadata1.getFileUUID());
		targetFileUUIDs.add(this.fileMetadata2.getFileUUID());
		targetFileUUIDs.add(this.fileMetadata3.getFileUUID());
		this.fileMetadataDAO.updateGid(germplasm.getGid(), targetFileUUIDs);

		assertThat(this.fileMetadataDAO.getByGids(Arrays.asList(this.germplasm1.getGid())), hasSize(0));
		assertThat(this.fileMetadataDAO.getByGids(Arrays.asList(germplasm.getGid())), hasSize(3));
	}

}
