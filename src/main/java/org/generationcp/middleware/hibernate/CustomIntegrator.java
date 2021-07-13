package org.generationcp.middleware.hibernate;

import org.generationcp.middleware.hibernate.listener.CustomPreDeleteEventListener;
import org.generationcp.middleware.hibernate.listener.CustomPreUpdateEventListener;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * A file in META-INF/services/org.hibernate.integrator.spi.Integrator was added in order to be discovered by ServiceLoader (Java SPI). So, this integrator
 * will be used automatically when Hibernate starts up.
 */
public class CustomIntegrator implements Integrator {

	@Override
	public void integrate(final Configuration configuration, final SessionFactoryImplementor sessionFactory,
		final SessionFactoryServiceRegistry serviceRegistry) {

		final EventListenerRegistry service = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
		service.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(new CustomPreUpdateEventListener());
		service.getEventListenerGroup(EventType.PRE_DELETE).appendListener(new CustomPreDeleteEventListener());
	}

	@Override
	public void integrate(final MetadataImplementor metadata, final SessionFactoryImplementor sessionFactory,
		final SessionFactoryServiceRegistry serviceRegistry) {

	}

	@Override
	public void disintegrate(final SessionFactoryImplementor sessionFactory, final SessionFactoryServiceRegistry serviceRegistry) {

	}
}
