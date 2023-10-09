package org.generationcp.middleware.service.impl.crossplan;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.dao.crossplan.CrossPlanDAO;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.CrossPlan;
import org.generationcp.middleware.service.api.crossplan.CrossPlanTreeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CrossPlanTreeServiceImpl implements CrossPlanTreeService {

    private static final Logger LOG = LoggerFactory.getLogger(CrossPlanDAO.class);

    private final HibernateSessionProvider sessionProvider;
    private final DaoFactory daoFactory;



    public CrossPlanTreeServiceImpl(final HibernateSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
        this.daoFactory = new DaoFactory(sessionProvider);
    }

    @Override
    public Integer createCrossPlanTreeFolder(int parentId, String folderName, String programUUID) {
        final CrossPlan crossPlan = new CrossPlan();
        crossPlan.setProgramUUID(programUUID);
        crossPlan.setParent(this.daoFactory.getCrossPlanDAO().getById(parentId));
        crossPlan.setName(folderName);
        crossPlan.setDescription(folderName);
        crossPlan.setType("FOLDER");
        crossPlan.setCreatedBy(ContextHolder.getLoggedInUserId());
        return this.daoFactory.getCrossPlanDAO().save(crossPlan).getId();
    }

    @Override
    public Integer updateCrossPlanTreeFolder(int parentId, String newFolderName) {
        final CrossPlan crossPlan = this.daoFactory.getCrossPlanDAO().getById(parentId);
        crossPlan.setName(newFolderName);
        crossPlan.setDescription(newFolderName);
        return this.daoFactory.getCrossPlanDAO().update(crossPlan).getId();
    }

    @Override
    public void deleteCrossPlanFolder(Integer folderId) {
        final CrossPlan crossPlan = this.daoFactory.getCrossPlanDAO().getById(folderId);
        this.daoFactory.getCrossPlanDAO().makeTransient(crossPlan);
    }

    @Override
    public Integer moveCrossPlanNode(int itemId, int newParentFolderId) {
        final CrossPlan folderToMove = this.daoFactory.getCrossPlanDAO().getById(itemId);
        final CrossPlan newParentFolder = this.daoFactory.getCrossPlanDAO().getById(newParentFolderId);
        folderToMove.setParent(newParentFolder);

        this.daoFactory.getCrossPlanDAO().saveOrUpdate(folderToMove);
        this.sessionProvider.getSession().flush();

        return folderToMove.getId();
    }

    @Override
    public List<Reference> getChildrenOfFolder(int folderId, String programUUID) {
        return this.daoFactory.getCrossPlanDAO().getChildrenOfFolder(folderId, programUUID);
    }
}
