package org.generationcp.middleware.hibernate.listener;

import org.generationcp.middleware.pojos.AbstractEntity;

public abstract class AbstractEventListener {

	/**
	 * Check if the entity extends from AbstractEntity
	 *
	 * @param entityClass
	 * @return {@link boolean}
	 */
	protected boolean checkEntityExtendsAbstractEntity(final Class entityClass) {
		return AbstractEntity.class.isAssignableFrom(entityClass);
	}

}
