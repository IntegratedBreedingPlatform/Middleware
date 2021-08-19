package org.generationcp.middleware.util.uid;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.uid.UIDGenerator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

public class FileUIDGeneratorTest {

	@Test
	public void testGenerate_UID() {
		final String cropPrefixStr;
		final List<FileMetadata> list = new ArrayList<>();
		final FileMetadata file = new FileMetadata();
		final FileMetadata file2 = new FileMetadata();
		list.add(file);
		list.add(file2);
		final CropType crop = new CropType();
		crop.setCropName("maize");
		crop.setPlotCodePrefix(RandomStringUtils.randomAlphanumeric(4));
		crop.setUseUUID(false);
		FileUIDGenerator.generate(crop, list);
		Assert.assertThat("should have generated UID with crop prefix", list, hasItems(
			Matchers.<FileMetadata>hasProperty("fileUUID", startsWith(crop.getPlotCodePrefix()))
		));
	}

	@Test
	public void testGenerate_UUID() {
		final String cropPrefixStr;
		final List<FileMetadata> list = new ArrayList<>();
		final FileMetadata file = new FileMetadata();
		final FileMetadata file2 = new FileMetadata();
		list.add(file);
		list.add(file2);
		final CropType crop = new CropType();
		crop.setCropName("maize");
		crop.setPlotCodePrefix(RandomStringUtils.randomAlphanumeric(4));
		crop.setUseUUID(true);
		FileUIDGenerator.generate(crop, list);
		Assert.assertThat("should have generated UUID", list, hasItems(
			Matchers.<FileMetadata>hasProperty("fileUUID", not(startsWith(crop.getPlotCodePrefix()))),
			Matchers.<FileMetadata>hasProperty("fileUUID", hasLength(36))
		));
	}
}
