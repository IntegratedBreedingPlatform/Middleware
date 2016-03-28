package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.api.UserDefinedFieldsDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.beust.jcommander.internal.Lists;
import static org.fest.assertions.api.Assertions.assertThat;

public class UserDefinedFieldsDataManagerImplIT extends IntegrationTestBase {

	@Autowired
	private UserDefinedFieldsDataManager manager;

	@Test
	public void getByTableAndTypeWithoutList() throws Exception {
		List<Integer> ids = Lists.newArrayList(1,2,3,19,20);

		List<UserDefinedField> list = manager.getNotCodeNamesFactor(ids);
		for (UserDefinedField userDefinedField : list) {
			assertThat(userDefinedField.getFuid()).isNotIn(ids);
		}

	}
}
