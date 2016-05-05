
package org.generationcp.middleware.hibernate;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class XADataSourcesTest {

	@Test
	public void testPostProcessBeanDefinitionRegistry() throws Exception {
		final XABeanDefinition mockXABeanDefinition = Mockito.mock(XABeanDefinition.class);
		final DatasourceUtilities mockXaDataSourceUtilities = Mockito.mock(DatasourceUtilities.class);
		final XADataSourceProperties xaDataSourceProperties = Mockito.mock(XADataSourceProperties.class);
		final XADataSources xaDataSources = new XADataSources(mockXaDataSourceUtilities, mockXABeanDefinition, xaDataSourceProperties);
		final SingleConnectionDataSource mockSingleConnectionDataSource = Mockito.mock(SingleConnectionDataSource.class);
		Mockito.when(mockXaDataSourceUtilities.getSingleConnectionDataSource(xaDataSourceProperties)).thenReturn(
				mockSingleConnectionDataSource);
		final BeanDefinitionRegistry mockBeanDefinitionReqistry = Mockito.mock(BeanDefinitionRegistry.class);
		xaDataSources.postProcessBeanDefinitionRegistry(mockBeanDefinitionReqistry);

		Mockito.verify(mockXABeanDefinition).createAllXARelatedBeans(mockSingleConnectionDataSource, mockBeanDefinitionReqistry,
				xaDataSourceProperties);
	}
}
