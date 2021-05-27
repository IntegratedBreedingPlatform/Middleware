package org.generationcp.middleware.hibernate.listener;

import org.generationcp.middleware.ContextHolder;
import org.hibernate.Query;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;

import javax.persistence.Table;
import java.util.Date;

public class CustomPreDeleteEventListener extends AbstractEventListener implements PreDeleteEventListener {

	/**
	 * Take in mind that if you return true, the entity will not be deleted because it was vetoed.
	 *
	 * @param event
	 * @return a boolean that indicates if the entity will be vetoed
	 */
	@Override
	public boolean onPreDelete(final PreDeleteEvent event) {
		if (this.checkEntityExtendsAbstractEntity(event.getEntity().getClass())) {
			String sql = new StringBuilder("UPDATE ")
				.append(event.getEntity().getClass().getAnnotation(Table.class).name())
				.append(" SET modified_by = :modifiedBy, modified_date = :modifiedDate WHERE ")
				.append(event.getPersister().getEntityMetamodel().getIdentifierProperty().getName())
				.append(" = :id").toString();
			final Query query = event.getSession().createSQLQuery(sql);
			query.setParameter("modifiedBy", ContextHolder.getLoggedInUserId());
			query.setParameter("modifiedDate", new Date());
			query.setParameter("id", event.getId());
			query.executeUpdate();
		}

		return false;
	}

}
