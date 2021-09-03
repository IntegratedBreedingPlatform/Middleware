package org.generationcp.middleware.api.file;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class FileMetadataServiceImplTest {

	public static final String HEX_REGEX = "[0-9a-fA-F]";

	@InjectMocks
	private FileMetadataServiceImpl fileMetadataService;

	@Test
	public void testGetFilePathForGermplasm_UUID() {
		final CropType cropType = new CropType();
		cropType.setCropName("maize");
		cropType.setPlotCodePrefix(randomAlphanumeric(4));
		cropType.setUseUUID(true);

		final List<Germplasm> germplasmList = new ArrayList<>();
		final Germplasm germplasm = new Germplasm();
		germplasmList.add(germplasm);
		final String fileName = randomAlphanumeric(20) + "." + randomAlphanumeric(3);

		GermplasmGuidGenerator.generateGermplasmGuids(cropType, germplasmList);

		final String germplasmUUID = germplasm.getGermplasmUUID();
		// e.g /germplasm/9/5/9/e/1/03efeb36-2783-42e1-82c3-1f776f10d3f1/myfile.png
		final Pattern pathPattern = compilePathPattern(fileName, germplasmUUID);

		final String path = fileMetadataService.getFilePathForGermplasm(germplasmUUID, fileName);
		assertThat(path, matchesPattern(pathPattern));
	}

	@Test
	public void testGetFilePathForGermplasm_CustomUID() {
		final CropType cropType = new CropType();
		cropType.setCropName("maize");
		cropType.setPlotCodePrefix(randomAlphanumeric(4));
		cropType.setUseUUID(false);

		final List<Germplasm> germplasmList = new ArrayList<>();
		final Germplasm germplasm = new Germplasm();
		germplasmList.add(germplasm);
		final String fileName = randomAlphanumeric(20) + "." + randomAlphanumeric(3);

		GermplasmGuidGenerator.generateGermplasmGuids(cropType, germplasmList);

		final String germplasmUUID = germplasm.getGermplasmUUID();
		// e.g /germplasm/9/5/9/e/1/X2GIGdNDukMIGaa/myfile.png
		final Pattern pathPattern = compilePathPattern(fileName, germplasmUUID);

		final String path = fileMetadataService.getFilePathForGermplasm(germplasmUUID, fileName);
		assertThat(path, matchesPattern(pathPattern));

		final String path2 = fileMetadataService.getFilePathForGermplasm(germplasmUUID, fileName);
		assertThat(path2, is(path));
	}

	private Pattern compilePathPattern(final String fileName, final String germplasmUUID) {
		return Pattern.compile(
			FileMetadataServiceImpl.FILE_PATH_GERMPLASM_ROOT
				+ "\\" + FileMetadataServiceImpl.FILE_PATH_SLASH + HEX_REGEX
				+ "\\" + FileMetadataServiceImpl.FILE_PATH_SLASH + HEX_REGEX
				+ "\\" + FileMetadataServiceImpl.FILE_PATH_SLASH + HEX_REGEX
				+ "\\" + FileMetadataServiceImpl.FILE_PATH_SLASH + HEX_REGEX
				+ "\\" + FileMetadataServiceImpl.FILE_PATH_SLASH + HEX_REGEX
				+ "\\" + FileMetadataServiceImpl.FILE_PATH_SLASH + FileMetadataServiceImpl.FILE_PATH_PREFIX_GERMPLASMUUID + germplasmUUID
				+ "\\" + FileMetadataServiceImpl.FILE_PATH_SLASH + fileName
		);
	}
}
