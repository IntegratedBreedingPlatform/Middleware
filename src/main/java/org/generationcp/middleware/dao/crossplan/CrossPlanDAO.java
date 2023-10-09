package org.generationcp.middleware.dao.crossplan;

import org.generationcp.middleware.api.crossplan.CrossPlanSearchRequest;
import org.generationcp.middleware.api.crossplan.CrossPlanSearchResponse;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.CrossPlan;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CrossPlanDAO extends GenericDAO<CrossPlan,Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(CrossPlanDAO.class);

    private static final String PROGRAM_UUID = "program_uuid";

    static final String GET_CHILDREN_OF_FOLDER =
            "SELECT subject.project_id AS project_id, "
                    + "subject.name AS name,  subject.description AS description, "
                    + "	(CASE WHEN subject.study_type_id IS NOT NULL THEN 1 ELSE 0 END) AS is_study, "
                    + "    subject.program_uuid AS program_uuid, "
                    + "    st.study_type_id AS studyType, st.label as label, st.name as studyTypeName, "
                    + "st.visible as visible, st.cvterm_id as cvtermId, subject.locked as isLocked, "
                    + "subject.created_by "
                    + "  FROM project subject "
                    + "  LEFT JOIN study_type st ON subject.study_type_id = st.study_type_id "
                    + " LEFT JOIN project parent ON subject.parent_project_id = parent.project_id "
                    + " WHERE subject.parent_project_id = :folderId "
                    + "   AND parent.study_type_id IS NULL "
                    + "   AND (:program_uuid IS NULL OR subject.program_uuid = :program_uuid OR subject.program_uuid IS NULL) "
                    + "   AND (:studyTypeId is null or subject.study_type_id = :studyTypeId or subject.study_type_id is null)"
                    // the OR here for value = null is required for folders.
                    + "	ORDER BY name";

    public CrossPlanDAO(Session session) {
        super(session);
    }

    public long countSearchCrossPlans(String programUUID, CrossPlanSearchRequest crossPlanSearchRequest) {
        final SQLQueryBuilder queryBuilder = CrossPlanSearchDAOQuery.getCountQuery(crossPlanSearchRequest);
        queryBuilder.setParameter("programUUID", programUUID);

        final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
        queryBuilder.addParamsToQuery(query);

        return ((BigInteger) query.uniqueResult()).longValue();
    }

    public List<CrossPlanSearchResponse> searchCrossPlans(String programUUID, CrossPlanSearchRequest crossPlanSearchRequest, Pageable pageable) {
        final SQLQueryBuilder queryBuilder = CrossPlanSearchDAOQuery.getSelectQuery(crossPlanSearchRequest, pageable);
        queryBuilder.setParameter("programUUID", programUUID);

        final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
        queryBuilder.addParamsToQuery(query);

        query.addScalar(CrossPlanSearchDAOQuery.CROSS_PLAN_ID_ALIAS);
        query.addScalar(CrossPlanSearchDAOQuery.CROSS_PLAN_NAME_ALIAS);
        query.addScalar(CrossPlanSearchDAOQuery.PARENT_FOLDER_NAME_ALIAS);
        query.addScalar(CrossPlanSearchDAOQuery.DESCRIPTION_ALIAS);
        query.addScalar(CrossPlanSearchDAOQuery.CROSS_PLAN_OWNER_ALIAS);
        query.addScalar(CrossPlanSearchDAOQuery.TYPE_ALIAS);
        query.addScalar(CrossPlanSearchDAOQuery.NOTES_ALIAS);
        query.addScalar(CrossPlanSearchDAOQuery.CREATION_DATE_ALIAS);
        query.setResultTransformer(Transformers.aliasToBean(CrossPlanSearchResponse.class));

        GenericDAO.addPaginationToSQLQuery(query, pageable);

        return (List<CrossPlanSearchResponse>) query.list();
    }

    public List<Reference> getChildrenOfFolder(final Integer folderId, final String programUUID) {
        final List<Reference> childrenNodes;

        try {
            final Query query =
                    this.getSession().createSQLQuery(CrossPlanDAO.GET_CHILDREN_OF_FOLDER).addScalar("id").addScalar("name")
                            .addScalar("description").addScalar("notes").addScalar("program_uuid").addScalar("is_cross_plan", new IntegerType())
                            .addScalar("created_by", new IntegerType());
            query.setParameter("folderId", folderId);
            query.setParameter(CrossPlanDAO.PROGRAM_UUID, programUUID);

            final List<Object[]> list = query.list();
            childrenNodes = this.getChildrenNodesList(list);

        } catch (final HibernateException e) {
            LOG.error(e.getMessage(), e);
            throw new MiddlewareQueryException(
                    "Error retrieving study folder tree, folderId=" + folderId + " programUUID=" + programUUID + ":" + e.getMessage(), e);
        }

        return childrenNodes;
    }

    private List<Reference> getChildrenNodesList(final List<Object[]> list) {
        final List<Reference> childrenNodes = new ArrayList<>();
        for (final Object[] row : list) {
            // project.id
            final Integer id = (Integer) row[0];
            // project.name
            final String name = (String) row[1];
            // project.description
            final String description = (String) row[2];
            // non-zero if a cross Plan, else a folder
            final Integer isCrossPlan = (Integer) row[3];
            // project.program_uuid
            final String projectUUID = (String) row[4];

            if (isCrossPlan.equals(1)) {
                final Integer studyTypeId = (Integer) row[5];
                final String label = (String) row[6];
                final String studyTypeName = (String) row[7];
                final boolean visible = ((Byte) row[8]) == 1;
                final Integer cvtermId = (Integer) row[9];
                final Boolean isLocked = (Boolean) row[10];
                final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, label, studyTypeName, cvtermId, visible);
                final Integer ownerId = (Integer) row[11];
                childrenNodes.add(new StudyReference(id, name, description, projectUUID, studyTypeDto, isLocked, ownerId));
            } else {
                childrenNodes.add(new FolderReference(id, name, description, projectUUID));
            }
        }

        return childrenNodes;
    }
}
