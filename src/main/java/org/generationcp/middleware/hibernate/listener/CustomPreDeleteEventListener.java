package org.generationcp.middleware.hibernate.listener;

import org.generationcp.middleware.ContextHolder;
import org.hibernate.Query;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
		final Class<?> entityClass = event.getEntity().getClass();
		if (this.checkEntityExtendsAbstractEntity(entityClass)) {
			final String sql = new StringBuilder("UPDATE ")
				.append(entityClass.getAnnotation(Table.class).name())
				.append(" SET modified_by = :modifiedBy, modified_date = :modifiedDate WHERE ")
				.append(getPKColumnName(entityClass))
				.append(" = :id").toString();
			final Query query = event.getSession().createSQLQuery(sql);
			query.setParameter("modifiedBy", ContextHolder.getLoggedInUserId());
			query.setParameter("modifiedDate", new Date());
			query.setParameter("id", event.getId());
			query.executeUpdate();
		}

		return false;
	}

	/**
	 * Returns the column name as in the table. e.g file_id in
	 * <pre>
	 * Column(name = "file_id")
	 * private Integer fileId;
	 * </pre>
	 * FIXME is there a better way?
	 */
	static String getPKColumnName(final Class<?> entityClass) {

		if (entityClass == null) {
			return null;
		}

		String name = null;

		for (final Field f : entityClass.getDeclaredFields()) {
			Id id = null;
			Column column = null;

			final Annotation[] as = f.getAnnotations();
			for (final Annotation a : as) {
				if (a.annotationType() == Id.class) {
					id = (Id) a;
				} else if (a.annotationType() == Column.class) {
					column = (Column) a;
				}
			}

			if (id != null && column != null){
				name = column.name();
				break;
			}
		}

		return name;
	}

}
