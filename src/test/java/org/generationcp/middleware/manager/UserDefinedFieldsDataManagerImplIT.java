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

	public static final String CODE_3 = "code3";
	public static final String CODE_2 = "code2";
	public static final String CODE_1 = "code1";
	@Autowired
	private UserDefinedFieldsDataManager manager;

	@Test
	public void getByTableAndTypeWithoutList() throws Exception {
		List<String> names = Lists.newArrayList(CODE_1, CODE_2, CODE_3);

		List<UserDefinedField> list = manager.getNotCodeNamesFactor(names);
		for (UserDefinedField userDefinedField : list) {
			assertThat(userDefinedField.getFcode()).isNotIn(names);
		}

	}
}
