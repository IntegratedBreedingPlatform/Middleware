package org.generationcp.middleware.dao;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.CropParameter;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class CropParameterDAO extends GenericDAO<CropParameter, String> {

	private static final String UPDATE_ENCRYPTED_VARIABLE_QUERY = "UPDATE crop_parameter "
		+ "SET `encrypted_value` = AES_ENCRYPT(:clearTextValue, UNHEX(SHA2(:secretPassphrase,512)))"
		+ "WHERE `key` = :key";

	private static final String DECRYPT_VARIABLE_QUERY =
		"SELECT {c.*}, AES_DECRYPT(`encrypted_value`, UNHEX(SHA2(:secretPassphrase,512))) as decryptedValue "
			+ " FROM crop_parameter c";

	private static final String SEARCH_FILTER_QUERY = " WHERE c.group_name = :groupName";
	private static final String KEY_FILTER_QUERY = " WHERE c.`key` = :key";
	public static final String SECRET_PASSPHRASE = "secretPassphrase";

	public CropParameterDAO(final Session session) {
		super(session);
	}

	public void updateEncryptedValue(final String key, final String encryptedValue, final String secretPassphrase) {
		this.getSession().flush();
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(UPDATE_ENCRYPTED_VARIABLE_QUERY);
		sqlQuery.setParameter("key", key);
		sqlQuery.setParameter(SECRET_PASSPHRASE, secretPassphrase);
		sqlQuery.setParameter("clearTextValue", encryptedValue);
		sqlQuery.executeUpdate();
	}

	public List<CropParameter> getAllCropParameters(final Pageable pageable, final String secretPassphrase) {
		return this.getCropParametersByGroupName(pageable, secretPassphrase, null);
	}

	public List<CropParameter> getCropParametersByGroupName(final Pageable pageable, final String secretPassphrase,
		final String groupFilter) {
		final boolean hasFilter = StringUtils.isNotBlank(groupFilter);
		// Get the CropParameter object along with the decrypted password.
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(DECRYPT_VARIABLE_QUERY
			+ (hasFilter ? SEARCH_FILTER_QUERY : StringUtils.EMPTY));
		sqlQuery.addEntity("c", CropParameter.class);
		sqlQuery.addScalar("decryptedValue");
		sqlQuery.setParameter(SECRET_PASSPHRASE, secretPassphrase);
		if (hasFilter) {
			sqlQuery.setParameter("groupName", groupFilter);
		}

		if (pageable != null) {
			sqlQuery.setFirstResult(pageable.getPageSize() * pageable.getPageNumber());
			sqlQuery.setMaxResults(pageable.getPageSize());
		}

		try {
			final List<Object[]> result = sqlQuery.list();
			final List<CropParameter> cropParameters = new ArrayList<>();

			for (final Object[] row : result) {
				final CropParameter cropParameter = (CropParameter) row[0];
				final byte[] passwordBytes = (byte[]) row[1];
				if (passwordBytes != null && cropParameter.isEncrypted()) {
					cropParameter.setEncryptedValue(new String(passwordBytes));
				}
				cropParameters.add(cropParameter);
			}
			return cropParameters;
		} catch (final HibernateException e) {
			final String message = "Error with getCropParametersByGroupName" + e.getMessage();
			throw new MiddlewareQueryException(message, e);
		}
	}

	public CropParameter getCropParameterByKey(final String key, final String secretPassphrase) {
		// Get the CropParameter object along with the decrypted password.
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(DECRYPT_VARIABLE_QUERY
			+ KEY_FILTER_QUERY);
		sqlQuery.addEntity("c", CropParameter.class);
		sqlQuery.addScalar("decryptedValue");
		sqlQuery.setParameter(SECRET_PASSPHRASE, secretPassphrase);
		sqlQuery.setParameter("key", key);

		final Object[] result = (Object[]) sqlQuery.uniqueResult();
		if (result != null) {
			final CropParameter cropParameter = (CropParameter) result[0];
			final byte[] passwordBytes = (byte[]) result[1];
			// Set the decrypted genotyping password to the CropGenotypingParameter object.
			cropParameter.setEncryptedValue(passwordBytes != null ? new String(passwordBytes) : StringUtils.EMPTY);
			return cropParameter;
		} else {
			return null;
		}
	}
}
