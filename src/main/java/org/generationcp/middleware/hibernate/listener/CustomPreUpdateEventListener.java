package org.generationcp.middleware.hibernate.listener;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.pojos.AbstractEntity;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;

public class CustomPreUpdateEventListener implements PreUpdateEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(CustomPreUpdateEventListener.class);

	/**
	 * Take in mind that if you return true, the entity will not be updated because it was vetoed.
	 *
	 * @param event
	 * @return a boolean that indicates if the entity will be vetoed
	 */
	@Override
	public boolean onPreUpdate(final PreUpdateEvent event) {
		//Check if the entity extends from AbstractEntity
		if (AbstractEntity.class.isAssignableFrom(event.getEntity().getClass())) {
			this.setValue(event.getPersister(), event.getState(), AbstractEntity.MODIFIED_DATE_FIELD_NAME, new Date());
			this.setValue(event.getPersister(), event.getState(), AbstractEntity.MODIFIED_BY_FIELD_NAME, ContextHolder.getLoggedInUserId());
		}

		return false;
	}

	private void setValue(final EntityPersister entityPersister, final Object[] state, final String propertyName, final Object value) {
		final int propertyIndexOf = Arrays.asList(entityPersister.getEntityMetamodel().getPropertyNames()).indexOf(propertyName);
		if (propertyIndexOf < 0) {
			LOG.debug("There is no property: [{}] in entity: [{}]", propertyName, entityPersister.getEntityName());
			return;
		}

		state[propertyIndexOf] = value;
	}
}
