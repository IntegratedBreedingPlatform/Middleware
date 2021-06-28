package org.generationcp.middleware.dao.audit;

import org.generationcp.middleware.service.impl.audit.RevisionType;
import org.hibernate.type.CustomType;
import org.hibernate.type.EnumType;

import java.sql.Types;
import java.util.Properties;

public class RevisionTypeResolver {

	public static final CustomType INSTANCE = getResolver();

	private static CustomType getResolver() {
		Properties parameters = new Properties();
		parameters.put(EnumType.ENUM, RevisionType.class.getName());
		parameters.put(EnumType.NAMED, false);
		parameters.put(EnumType.TYPE, String.valueOf(Types.INTEGER));

		EnumType enumType = new EnumType();
		enumType.setParameterValues(parameters);
		return new CustomType(enumType);
	}

}
