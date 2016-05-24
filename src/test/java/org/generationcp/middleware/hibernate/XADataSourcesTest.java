
package org.generationcp.middleware.hibernate;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class XADataSourcesTest {

	@Test
	public void testPostProcessBeanDefinitionRegistry() throws Exception {
		final XABeanDefinition mockXABeanDefinition = Mockito.mock(XABeanDefinition.class);
		final DatasourceUtilities mockXaDataSourceUtilities = Mockito.mock(DatasourceUtilities.class);
		final DataSourceProperties xaDataSourceProperties = Mockito.mock(DataSourceProperties.class);
		final XADataSources xaDataSources = new XADataSources(mockXaDataSourceUtilities, mockXABeanDefinition, xaDataSourceProperties);
		final DriverManagerDataSource mockWorkbenchDataSource = Mockito.mock(DriverManagerDataSource.class);
		Mockito.when(mockXaDataSourceUtilities.getWorkbenchDataSource(xaDataSourceProperties)).thenReturn(mockWorkbenchDataSource);
		final BeanDefinitionRegistry mockBeanDefinitionReqistry = Mockito.mock(BeanDefinitionRegistry.class);
		xaDataSources.postProcessBeanDefinitionRegistry(mockBeanDefinitionReqistry);

		Mockito.verify(mockXABeanDefinition).createAllXARelatedBeans(mockWorkbenchDataSource, mockBeanDefinitionReqistry,
				xaDataSourceProperties);
	}
}
