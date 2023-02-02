package org.generationcp.middleware.audit;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.operation.saver.ExperimentModelSaver;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Table;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PhenotypeAuditIntegrationTest extends AuditIntegrationTestBase {

	private static final String PRIMARY_KEY_FIELD = "phenotype_id";

	private DaoFactory daoFactory;
	private ExperimentModelSaver experimentModelSaver;
	private CropType crop;
	private Integer locationId;

	public PhenotypeAuditIntegrationTest() {
		super(Phenotype.class.getAnnotation(Table.class).name(), PRIMARY_KEY_FIELD);
	}

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);

		if (this.crop == null) {
			this.crop = new CropType();
			this.crop.setUseUUID(true);
		}

		if (this.experimentModelSaver == null) {
			this.experimentModelSaver = new ExperimentModelSaver(this.sessionProvder);
		}
		final VariableList factors = new VariableList();
		factors.add(DMSVariableTestDataInitializer.createVariable(1001, "999", DataType.NUMERIC_VARIABLE.getId(), VariableType.TRAIT));
		final ExperimentValues values = new ExperimentValues();
		values.setVariableList(factors);
		values.setLocationId(this.experimentModelSaver.createNewGeoLocation().getLocationId());
		values.setGermplasmId(1);

		this.experimentModelSaver.addExperiment(this.crop, 1, ExperimentType.TRIAL_ENVIRONMENT, values);
		this.locationId = values.getLocationId();
	}

	@Test
	public void shouldTriggersExists() {
		this.checkTriggerExists("trigger_phenotype_aud_insert", "INSERT");
		this.checkTriggerExists("trigger_phenotype_aud_update", "UPDATE");
		this.checkTriggerExists("trigger_phenotype_aud_delete", "DELETE");
	}

	@Test
	public void shouldAuditInsertAndUpdate() {
		this.enableEntityAudit();

		final List<CVTerm> cvTerm = this.daoFactory.getCvTermDao().getTermsByCvId(CvId.VARIABLES, 0, 2);

		//Create a phenontype 1
		final Integer term1 = cvTerm.get(0).getCvTermId();
		final Map<String, Object> insertAttribute1QueryParams = this.createQueryParams(null, null, term1);
		this.insertEntity(insertAttribute1QueryParams);

		final Integer phenontype1PhenotypeId = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(phenontype1PhenotypeId), is(1));

		final LinkedHashSet<String> fieldNames = this.getSelectAuditFieldNames(insertAttribute1QueryParams.keySet());

		//Assert recently created phenontype
		final Map<String, Object> insertAudit = this.getLastAudit(fieldNames);
		this.assertAudit(insertAudit, insertAttribute1QueryParams, 0, phenontype1PhenotypeId);

		//Disable audit
		this.disableEntityAudit();

		//Insert another phenontype
		final Map<String, Object> insertAttribute2QueryParams = this.createQueryParams(null, null,
			cvTerm.get(1).getCvTermId());
		this.insertEntity(insertAttribute2QueryParams);

		//Because the audit was disabled, the phenontype shouldn't be audited
		final Integer phenontype2PhenotypeId = this.getLastInsertIdFromEntity();
		assertThat(this.countEntityAudit(phenontype2PhenotypeId), is(0));

		//Enable audit
		this.enableEntityAudit();

		//Update the phenontype 1
		final Map<String, Object> updateAttribute1QueryParams1 =
			this.createQueryParams(new Random().nextInt(), new Date(), term1);
		this.updateEntity(updateAttribute1QueryParams1, phenontype1PhenotypeId);

		assertThat(this.countEntityAudit(phenontype1PhenotypeId), is(2));

		//Assert recently updated phenontype
		final Map<String, Object> updateAudit = this.getLastAudit(fieldNames);
		this.assertAudit(updateAudit, updateAttribute1QueryParams1, 1, phenontype1PhenotypeId);

		//Disable audit
		this.disableEntityAudit();

		//Update again the entity, because the audit was disable shouldn't be audited
		final Map<String, Object> updateAttribute1QueryParams2 =
			this.createQueryParams(new Random().nextInt(), new Date(), term1);
		this.updateEntity(updateAttribute1QueryParams2, phenontype1PhenotypeId);

		assertThat(this.countEntityAudit(phenontype1PhenotypeId), is(2));

		//Enable audit
		this.enableEntityAudit();

		this.deleteEntity(phenontype1PhenotypeId);
		assertThat(this.countEntityAudit(phenontype1PhenotypeId), is(3));

		//Assert recently deleted phenontype
		final Map<String, Object> deletedAudit = this.getLastAudit(fieldNames);
		this.assertAudit(deletedAudit, updateAttribute1QueryParams2, 2, phenontype1PhenotypeId);

		//Disable audit
		this.disableEntityAudit();

		//Delete phenontype 2, because the audit was disable shouldn't be audited
		this.deleteEntity(phenontype2PhenotypeId);
		assertThat(this.countEntityAudit(phenontype2PhenotypeId), is(0));
	}

	protected Map<String, Object> createQueryParams(final Integer updatedBy, final Date updatedDate, final Integer cvtermId) {
		final ExperimentModel experiment = this.daoFactory.getExperimentDao().getExperimentByProjectIdAndLocation(1, this.locationId);

		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("uniquename", RandomStringUtils.randomAlphanumeric(10));
		queryParams.put("name", RandomStringUtils.randomAlphanumeric(10));
		queryParams.put("observable_id", cvtermId);
		queryParams.put("attr_id", cvtermId);
		queryParams.put("value", RandomStringUtils.randomAlphanumeric(10));
		queryParams.put("cvalue_id", cvtermId);
		queryParams.put("status", RandomStringUtils.randomAlphanumeric(10));
		queryParams.put("assay_id", cvtermId);
		queryParams.put("nd_experiment_id", experiment.getNdExperimentId());
		queryParams.put("draft_value", RandomStringUtils.randomAlphanumeric(10));
		queryParams.put("draft_cvalue_id", new Random().nextInt());
		queryParams.put("created_date", new Date());
		queryParams.put("created_by", new Random().nextInt());
		queryParams.put("updated_date", updatedDate);
		queryParams.put("updated_by", updatedBy);

		return queryParams;
	}

	private void assertAudit(final Map<String, Object> audit, final Map<String, Object> entity, final int revType,
		final int phenotypeId) {
		assertThat(new Integer(audit.get("rev_type").toString()), is(revType));
		assertThat(audit.get("uniquename"), is(entity.get("uniquename")));
		assertThat(audit.get("name"), is(entity.get("name")));
		assertThat(audit.get("observable_id"), is(entity.get("observable_id")));
		assertThat(audit.get("attr_id"), is(entity.get("attr_id")));
		assertThat(audit.get("value"), is(entity.get("value")));
		assertThat(audit.get("cvalue_id"), is(entity.get("cvalue_id")));
		assertThat(audit.get("status"), is(entity.get("status")));
		assertThat(audit.get("assay_id"), is(entity.get("assay_id")));
		assertThat(audit.get("nd_experiment_id"), is(entity.get("nd_experiment_id")));
		assertThat(audit.get("draft_value"), is(entity.get("draft_value")));
		assertThat(audit.get("draft_cvalue_id"), is(entity.get("draft_cvalue_id")));
		assertThat(audit.get("created_by"), is(entity.get("created_by")));
		assertThat(audit.get("updated_by"), is(entity.get("updated_by")));
		assertThat(DATE_FORMAT.format(audit.get("created_date")), is(DATE_FORMAT.format(entity.get(("created_date")))));
		if (audit.get("updated_date") == null) {
			assertThat(audit.get("updated_date"), is(entity.get("updated_date")));
		} else {
			assertThat(DATE_FORMAT.format(audit.get("updated_date")), is(DATE_FORMAT.format(entity.get("updated_date"))));
		}
		assertThat(audit.get(PRIMARY_KEY_FIELD), is(phenotypeId));
		assertNotNull(audit.get(AUDIT_PRIMARY_KEY_FIELD));
	}

}
