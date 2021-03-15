package org.generationcp.middleware.hibernate.listener;

import org.generationcp.middleware.pojos.AbstractEntity;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

public class CustomPreUpdateEventListener implements PreUpdateEventListener {

	@Override
	public boolean onPreUpdate(final PreUpdateEvent event) {
		System.out.println("onPreUpdate");
		//Check if the entity extends from AbstractEntity
		//TODO: check isInstance of method
		if (AbstractEntity.class.isAssignableFrom(event.getEntity().getClass())) {
			final AbstractEntity abstractEntity = (AbstractEntity) event.getEntity();
			abstractEntity.update();
			return false;
		}

		return true;
	}
}
