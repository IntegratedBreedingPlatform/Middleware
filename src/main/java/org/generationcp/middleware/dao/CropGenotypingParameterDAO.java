package org.generationcp.middleware.dao;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.pojos.workbench.CropGenotypingParameter;
import org.hibernate.SQLQuery;

public class CropGenotypingParameterDAO extends GenericDAO<CropGenotypingParameter, Integer> {

	private String secretPassphrase;

	public CropGenotypingParameterDAO(final String secretPassphrase) {
		this.secretPassphrase = secretPassphrase;
	}

	public CropGenotypingParameter getByCropName(final String cropName) {
		Preconditions.checkNotNull(cropName);
		Preconditions.checkArgument(StringUtils.isNotBlank(cropName), "Cropname cannot be blank");
		// Get the CropGenotypingParameter object along with the decrypted password.
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(
			"SELECT {c.*}, AES_DECRYPT(`password`, UNHEX(SHA2(:secretPassphrase,512))) as decryptedPassword "
				+ " FROM workbench.crop_genotyping_parameter c WHERE crop_name = :cropName");
		sqlQuery.addEntity("c", CropGenotypingParameter.class);
		sqlQuery.addScalar("decryptedPassword");
		sqlQuery.setParameter("cropName", cropName);
		sqlQuery.setParameter("secretPassphrase", this.secretPassphrase);
		final Object[] result = (Object[]) sqlQuery.uniqueResult();
		if (result != null) {
			final CropGenotypingParameter cropGenotypingParameter = (CropGenotypingParameter) result[0];
			final byte[] passwordBytes = (byte[]) result[1];
			// Set the decrypted genotyping password to the CropGenotypingParameter object.
			cropGenotypingParameter.setPassword(passwordBytes != null ? new String(passwordBytes) : StringUtils.EMPTY);
			return cropGenotypingParameter;
		} else {
			return null;
		}
	}

	@Override
	public CropGenotypingParameter save(final CropGenotypingParameter cropGenotypingParameter) {
		final CropGenotypingParameter savedCropGenotypingParameter = super.save(cropGenotypingParameter);
		// Manually update the password column with encrypted data
		this.getSession().flush(); // To ensure the record is already saved in the database before updating the password.
		this.updateEncryptedPassword(savedCropGenotypingParameter);
		return savedCropGenotypingParameter;
	}

	@Override
	public CropGenotypingParameter saveOrUpdate(final CropGenotypingParameter cropGenotypingParameter) {
		final CropGenotypingParameter savedCropGenotypingParameter = super.saveOrUpdate(cropGenotypingParameter);
		// Manually update genotyping_password column with encrypted data
		this.updateEncryptedPassword(savedCropGenotypingParameter);
		return savedCropGenotypingParameter;
	}

	private void updateEncryptedPassword(final CropGenotypingParameter cropGenotypingParameter) {
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(
			"UPDATE workbench.crop_genotyping_parameter "
				+ "SET `password` = AES_ENCRYPT(:clearTextPassword, UNHEX(SHA2(:secretPassphrase,512))) "
				+ "WHERE crop_name = :cropName");
		sqlQuery.setParameter("cropName", cropGenotypingParameter.getCropName());
		sqlQuery.setParameter("secretPassphrase", this.secretPassphrase);
		sqlQuery.setParameter("clearTextPassword", cropGenotypingParameter.getPassword());
		sqlQuery.executeUpdate();
	}

	protected String getSecretPassphrase() {
		return this.secretPassphrase;
	}

	protected void setSecretPassphrase(final String secretPassphrase) {
		this.secretPassphrase = secretPassphrase;
	}

}
