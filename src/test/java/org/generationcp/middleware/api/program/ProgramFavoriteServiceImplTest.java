package org.generationcp.middleware.api.program;

import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ProgramFavoriteServiceImplTest {

  private static final String PROGRAM_UUID = UUID.randomUUID().toString();
  private static final ProgramFavorite.FavoriteType ENTITY_TYPE = ProgramFavorite.FavoriteType.LOCATION;

  @InjectMocks
  private ProgramFavoriteServiceImpl programFavoriteService;

  @Mock
  private DaoFactory daoFactory;

  @Mock
  private ProgramFavoriteDAO programFavoriteDAO;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    Mockito.when(this.daoFactory.getProgramFavoriteDao()).thenReturn(this.programFavoriteDAO);
    ReflectionTestUtils.setField(this.programFavoriteService, "daoFactory", this.daoFactory);
  }

  @Test
  public void shouldAddProgramFavorites() {
    final Integer entityId = new Random().nextInt();
    final Set<Integer> entityIds = new HashSet<>(Arrays.asList(entityId));

    final ProgramFavorite dummyProgramFavorite = this.createProgramFavoriteMock(entityId);
    Mockito.when(this.programFavoriteDAO.save(ArgumentMatchers.any(ProgramFavorite.class))).thenReturn(dummyProgramFavorite);

    final List<ProgramFavoriteDTO> programFavoriteDTOS =
        this.programFavoriteService.addProgramFavorites(PROGRAM_UUID, ENTITY_TYPE, entityIds);
    assertThat(programFavoriteDTOS, hasSize(1));
    final ProgramFavoriteDTO actualProgramFavoriteDTO = programFavoriteDTOS.get(0);
    assertNotNull(actualProgramFavoriteDTO.getProgramFavoriteId());
    assertThat(actualProgramFavoriteDTO.getProgramUUID(), is(PROGRAM_UUID));
    assertThat(actualProgramFavoriteDTO.getEntityType(), is(ENTITY_TYPE));
    assertThat(actualProgramFavoriteDTO.getEntityId(), is(entityId));

    final ArgumentCaptor<ProgramFavorite> programFavoriteArgumentCaptor = ArgumentCaptor.forClass(ProgramFavorite.class);
    Mockito.verify(this.programFavoriteDAO).save(programFavoriteArgumentCaptor.capture());
    final ProgramFavorite actualProgramFavorite = programFavoriteArgumentCaptor.getValue();
    assertNotNull(actualProgramFavorite);
    assertNull(actualProgramFavorite.getProgramFavoriteId());
    assertThat(actualProgramFavorite.getUniqueID(), is(PROGRAM_UUID));
    assertThat(actualProgramFavorite.getEntityType(), is(ENTITY_TYPE));
    assertThat(actualProgramFavorite.getEntityId(), is(entityId));
  }

  private ProgramFavorite createProgramFavoriteMock(final Integer entityId) {
    final ProgramFavorite programFavorite = Mockito.mock(ProgramFavorite.class);
    Mockito.when(programFavorite.getUniqueID()).thenReturn(PROGRAM_UUID);
    Mockito.when(programFavorite.getEntityType()).thenReturn(ENTITY_TYPE);
    Mockito.when(programFavorite.getEntityId()).thenReturn(entityId);
    Mockito.when(programFavorite.getProgramFavoriteId()).thenReturn(new Random().nextInt());
    return programFavorite;
  }

}
