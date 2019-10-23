
package org.generationcp.middleware.service.impl.study;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Test;
import org.mockito.Mockito;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * The class <code>StudyGermplasmListServiceImplTest</code> contains tests for the class <code>{@link StudyGermplasmListServiceImpl}</code>.
 */
public class StudyGermplasmListServiceImplTest {

	/**
	 * Run the StudyGermplasmListServiceImpl(Session) constructor test.
	 */
	@Test
	public void getStudyGermplasmListFromDb() {

		final Session currentSession = Mockito.mock(Session.class);
		final Criteria mockCriteria = Mockito.mock(Criteria.class);
		when(currentSession.createCriteria(ListDataProject.class)).thenReturn(mockCriteria);
		when(mockCriteria.createAlias("list", "l")).thenReturn(mockCriteria);
		when(mockCriteria.add(any(SimpleExpression.class))).thenReturn(mockCriteria);

		final PodamFactory factory = new PodamFactoryImpl();

		final GermplasmList germplasmList = new GermplasmList();
		final ListDataProject listDataProject = new ListDataProject();

		factory.populatePojo(germplasmList, GermplasmList.class);
		factory.populatePojo(listDataProject, ListDataProject.class);

		listDataProject.setList(germplasmList);
		final List<ListDataProject> queryResults = new ArrayList<ListDataProject>();
		queryResults.add(listDataProject);

		when(mockCriteria.list()).thenReturn(queryResults);

		final StudyGermplasmDto expectedGermplasm = this.getResultingStudyGermplasmDto(germplasmList, listDataProject);

		final StudyGermplasmListServiceImpl studyGermplasmListServiceImpl = new StudyGermplasmListServiceImpl(currentSession);
		final List<StudyGermplasmDto> actualGermplasmList = studyGermplasmListServiceImpl.getGermplasmList(2013);

		assertEquals("The two lists must be equal.", Collections.<StudyGermplasmDto>singletonList(expectedGermplasm), actualGermplasmList);
	}

	private StudyGermplasmDto getResultingStudyGermplasmDto(final GermplasmList germplasmList, final ListDataProject listDataProject) {
		final StudyGermplasmDto studyGermplasmDto = new StudyGermplasmDto();
		studyGermplasmDto.setCross(listDataProject.getGroupName());
		studyGermplasmDto.setDesignation(listDataProject.getDesignation());
		studyGermplasmDto.setEntryCode(listDataProject.getEntryCode());
		studyGermplasmDto.setEntryNumber(listDataProject.getEntryId());
		studyGermplasmDto.setGermplasmId(listDataProject.getGermplasmId());
		studyGermplasmDto.setPosition("1");
		studyGermplasmDto.setSeedSource(listDataProject.getSeedSource());
		studyGermplasmDto.setEntryType(germplasmList.getType());
		studyGermplasmDto.setCheckType(listDataProject.getCheckType());
		studyGermplasmDto.setGroupId(listDataProject.getGroupId());
		studyGermplasmDto.setStockIds(listDataProject.getStockIDs());
		return studyGermplasmDto;
	}

}
