package org.generationcp.middleware.api.nametype;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;

import java.util.List;

public class GermplasmNameTypeServiceImpl implements GermplasmNameTypeService {

    private final DaoFactory daoFactory;

    public GermplasmNameTypeServiceImpl(final HibernateSessionProvider sessionProvider) {
        this.daoFactory = new DaoFactory(sessionProvider);
    }

    @Override
    public List<GermplasmNameTypeDTO> searchNameTypes(final String name) {
        return this.daoFactory.getUserDefinedFieldDAO().searchNameTypes(name);
    }
}
