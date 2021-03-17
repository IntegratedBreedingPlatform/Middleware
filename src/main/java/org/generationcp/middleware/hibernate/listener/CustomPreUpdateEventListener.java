package org.generationcp.middleware.hibernate.listener;

import org.generationcp.middleware.pojos.AbstractEntity;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

public class CustomPreUpdateEventListener implements PreUpdateEventListener {

	@Override
	public boolean onPreUpdate(final PreUpdateEvent event) {
		//Check if the entity extends from AbstractEntity
		if (AbstractEntity.class.isAssignableFrom(event.getEntity().getClass())) {
			final AbstractEntity abstractEntity = (AbstractEntity) event.getEntity();
			abstractEntity.update();
		}

		return false;
	}
}
