package org.generationcp.middleware.hibernate;

import org.generationcp.middleware.hibernate.listener.CustomPreUpdateEventListener;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * For the integrator to be automatically used when Hibernate starts up, it's added in META-INF/services/org.hibernate.integrator.spi.Integrator
 */
public class CustomIntegrator implements Integrator {

	@Override
	public void integrate(final Configuration configuration, final SessionFactoryImplementor sessionFactory,
		final SessionFactoryServiceRegistry serviceRegistry) {

		//TODO: check duplication strategy
//		EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
//		listenerRegistry.addDuplicationStrategy( EnversListenerDuplicationStrategy.INSTANCE );

		final EventListenerRegistry service = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
		service.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(new CustomPreUpdateEventListener());
	}

	@Override
	public void integrate(final MetadataImplementor metadata, final SessionFactoryImplementor sessionFactory,
		final SessionFactoryServiceRegistry serviceRegistry) {

	}

	@Override
	public void disintegrate(final SessionFactoryImplementor sessionFactory, final SessionFactoryServiceRegistry serviceRegistry) {

	}
}
