package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.template.TemplateDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ListMetadata;
import org.generationcp.middleware.pojos.ProgramLocationDefault;
import org.generationcp.middleware.pojos.Template;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import java.util.List;

public class TemplateDAO extends GenericDAO<Template, Integer> {

    public TemplateDAO(final Session session) {
        super(session);
    }

    public TemplateDTO getTemplateByIdAndProgramUUID(final Integer id, final String programUUID) {
        try {
            final String templateDTOQuery = "SELECT temp.template_id AS templateId, "
                    + " temp.template_name AS templateName,  "
                    + " temp.program_uuid AS programUUID, "
                    + " temp.template_type AS templateType "
                    + " FROM template temp "
                    + " WHERE temp.program_uuid = :programUUID AND temp.template_id = :id";
            final SQLQuery setResultTransformer = this.getSession().createSQLQuery(templateDTOQuery);
            setResultTransformer.setParameter("programUUID", programUUID);
            setResultTransformer.setParameter("id", id);
            setResultTransformer.addScalar("templateId", new IntegerType());
            setResultTransformer.addScalar("templateName", new StringType());
            setResultTransformer.addScalar("programUUID", new StringType());
            setResultTransformer.addScalar("templateType", new StringType());
            setResultTransformer.setResultTransformer(Transformers.aliasToBean(TemplateDTO.class));
            return (TemplateDTO) setResultTransformer.uniqueResult();
        } catch (final HibernateException e) {
            throw new MiddlewareQueryException(
                    "error in: TemplateDAO.getTemplateByIdAndProgramUUID(programUUID=" + programUUID + "): " + e.getMessage(), e);
        }
    }

    public TemplateDTO getTemplateByNameAndProgramUUID(final String name, final String programUUID) {
        try {
            final String templateDTOQuery = "SELECT temp.template_id AS templateId, "
                    + " temp.template_name AS templateName,  "
                    + " temp.program_uuid AS programUUID, "
                    + " temp.template_type AS templateType "
                    + " FROM template temp "
                    + " WHERE temp.program_uuid = :programUUID AND temp.template_name = :name";
            final SQLQuery setResultTransformer = this.getSession().createSQLQuery(templateDTOQuery);
            setResultTransformer.setParameter("programUUID", programUUID);
            setResultTransformer.setParameter("name", name);
            setResultTransformer.addScalar("templateId", new IntegerType());
            setResultTransformer.addScalar("templateName", new StringType());
            setResultTransformer.addScalar("programUUID", new StringType());
            setResultTransformer.addScalar("templateType", new StringType());
            setResultTransformer.setResultTransformer(Transformers.aliasToBean(TemplateDTO.class));
            return (TemplateDTO) setResultTransformer.uniqueResult();
        } catch (final HibernateException e) {
            throw new MiddlewareQueryException(
                    "error in: TemplateDAO.getTemplateByNameAndProgramUUID(programUUID=" + programUUID + "): " + e.getMessage(), e);
        }
    }

    public List<TemplateDTO> getTemplateDTOsByType(final String programUUID, final String type) {
        try {
            final String templateDTOQuery = "SELECT temp.template_id AS templateId, "
                    + " temp.template_name AS templateName,  "
                    + " temp.program_uuid AS programUUID, "
                    + " temp.template_type AS templateType "
                    + " FROM template temp "
                    + " WHERE temp.program_uuid = :programUUID AND temp.template_type = :type";
            final SQLQuery setResultTransformer = this.getSession().createSQLQuery(templateDTOQuery);
            setResultTransformer.setParameter("programUUID", programUUID);
            setResultTransformer.setParameter("type", type);
            setResultTransformer.addScalar("templateId", new IntegerType());
            setResultTransformer.addScalar("templateName", new StringType());
            setResultTransformer.addScalar("programUUID", new StringType());
            setResultTransformer.addScalar("templateType", new StringType());
            setResultTransformer.setResultTransformer(Transformers.aliasToBean(TemplateDTO.class));
            return setResultTransformer.list();

        } catch (final HibernateException e) {
            throw new MiddlewareQueryException(
                    "Error with getTemplateDTOsByType(programUUID=" + programUUID + ", type=" + type + ") query from TemplateDAO: " + e.getMessage(),
                    e);
        }
    }
}
