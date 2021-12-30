package org.generationcp.middleware.api.program;

import org.generationcp.middleware.pojos.dms.ProgramFavorite;

import java.util.function.Function;

public class ProgramFavoriteMapper implements Function<ProgramFavorite, ProgramFavoriteDTO> {

  public static final ProgramFavoriteMapper INSTANCE = new ProgramFavoriteMapper();

  @Override
  public ProgramFavoriteDTO apply(final ProgramFavorite programFavorite) {
    return new ProgramFavoriteDTO(programFavorite.getProgramFavoriteId(), programFavorite.getEntityType(),
        programFavorite.getEntityId(), programFavorite.getUniqueID());
  }

}
