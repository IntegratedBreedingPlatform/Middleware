package org.generationcp.middleware.service.impl.gdms;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.pojos.gdms.CharValueElement;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.generationcp.middleware.service.api.gdms.DatasetRetrieveDto;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by clarysabel on 11/23/17.
 */
public class DatasetRetrieveDtoBuilderTest {

	private String name;

	private String description;

	private String type;

	private String genus;

	private String remarks;

	private String dataType;

	private String missingData;

	private String method;

	private String score;

	private Integer userId;

	private String species;

	public DatasetRetrieveDtoBuilderTest() {
		name = RandomStringUtils.random(20);
		description = RandomStringUtils.random(20);
		type = RandomStringUtils.random(3);
		genus = RandomStringUtils.random(3);
		remarks = RandomStringUtils.random(3);
		dataType = RandomStringUtils.random(3);
		missingData = RandomStringUtils.random(30);
		method = RandomStringUtils.random(30);
		score = RandomStringUtils.random(30);
		userId = 1;
		species = RandomStringUtils.random(3);
	}

	public Dataset getDataset() {
		final Dataset dataset = new Dataset();
		dataset.setDatasetName(name);
		dataset.setDatasetDesc(description);
		dataset.setDatasetType(type);
		dataset.setGenus(genus);
		dataset.setRemarks(remarks);
		dataset.setDataType(dataType);
		dataset.setMissingData(missingData);
		dataset.setMethod(method);
		dataset.setScore(score);
		dataset.setSpecies(species);
		dataset.setUploadTemplateDate(new Date());
		final DatasetUsers datasetUsers = new DatasetUsers();
		datasetUsers.setUserId(userId);
		dataset.setDatasetUsers(datasetUsers);
		return dataset;
	}

	public List<CharValueElement> getCharValueElements() {
		final List<CharValueElement> charValueElements = new ArrayList<>();
		final CharValueElement element1 = new CharValueElement();
		element1.setSampleUID("UID1");
		element1.setMarkerName("m1");
		element1.setCharValue("A/C");
		final CharValueElement element2 = new CharValueElement();
		element2.setSampleUID("UID1");
		element2.setMarkerName("m2");
		element2.setCharValue("A/C");
		charValueElements.add(element1);
		charValueElements.add(element2);
		return charValueElements;
	}

	@Test
	public void build_Ok() {
		final Dataset dataset = getDataset();
		final List<CharValueElement> charValueElements = getCharValueElements();
		final DatasetRetrieveDto datasetRetrieveDto = DatasetRetrieveDtoBuilder.build(dataset, charValueElements);

		assertThat(dataset.getDatasetName(), is(equalTo(datasetRetrieveDto.getName())));
		assertThat(dataset.getDatasetDesc(), is(equalTo(datasetRetrieveDto.getDescription())));
		assertThat(dataset.getDataType(), is(equalTo(datasetRetrieveDto.getDataType())));
		assertThat(dataset.getDatasetType(), is(equalTo(datasetRetrieveDto.getType())));
		assertThat(dataset.getGenus(), is(equalTo(datasetRetrieveDto.getGenus())));
		assertThat(dataset.getRemarks(), is(equalTo(datasetRetrieveDto.getRemarks())));
		assertThat(dataset.getMissingData(), is(equalTo(datasetRetrieveDto.getMissingData())));
		assertThat(dataset.getMethod(), is(equalTo(datasetRetrieveDto.getMethod())));
		assertThat(dataset.getScore(), is(equalTo(datasetRetrieveDto.getScore())));
		assertThat(dataset.getSpecies(), is(equalTo(datasetRetrieveDto.getSpecies())));
		assertThat(dataset.getDatasetUsers().getUserId(), is(equalTo(datasetRetrieveDto.getUserId())));

		assertThat(datasetRetrieveDto.getCharValueDtos(), hasSize(1));
		assertThat(datasetRetrieveDto.getCharValueDtos().get(0).getCharValues(), hasKey("m1"));
		assertThat(datasetRetrieveDto.getCharValueDtos().get(0).getCharValues(), hasKey("m2"));

	}

}
