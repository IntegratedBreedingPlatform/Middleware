package org.generationcp.middleware.liquibase;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.generationcp.middleware.domain.labelprinting.LabelPrintingPresetDTO;
import org.generationcp.middleware.domain.labelprinting.PresetDTO;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpdatePresetsTask implements liquibase.change.custom.CustomTaskChange {

	private static final List<String> TOOL_SECTIONS = Arrays.asList(
		"DATASET_LABEL_PRINTING_PRESET", "LOT_LABEL_PRINTING_PRESET",
		"GERMPLASM_LABEL_PRINTING_PRESET", "GERMPLASM_LIST_LABEL_PRINTING_PRESET",
		"STUDY_ENTRIES_LABEL_PRINTING_PRESET");

	private static final String UPDATE_PRESET = "update program_preset set configuration = '%s' where program_preset_id = %s";
	private static final Integer MAX_FIELD_STATIC_ID = 63;
	private static final Integer MAX_FIXED_TYPE_INDEX = 10000;
	private ObjectMapper jacksonMapper;
	private JdbcConnection dbConn;

	@Override
	public void execute(final Database database) throws CustomChangeException {

		this.jacksonMapper = new ObjectMapper();
		this.jacksonMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		Statement selectStatement = null;
		Statement updateStatement = null;

		try {
			this.dbConn = (JdbcConnection) database.getConnection();
			selectStatement = dbConn.createStatement();
			updateStatement = dbConn.createStatement();

			final ResultSet rs = selectStatement.executeQuery(
				"SELECT * FROM program_preset where (configuration  not like '%VARIABLE%' and configuration not like '%STATIC%') "
					+ "and tool_section in (" + UpdatePresetsTask.TOOL_SECTIONS.stream().collect(Collectors.joining("','", "'", "'"))
					+ ")");

			while (rs.next()) {
				final String programPresetId = rs.getString("program_preset_id");
				final String configuration = rs.getString("configuration");

				try {
					final LabelPrintingPresetDTO labelPrintingPresetDTO =
						this.jacksonMapper.readValue(configuration, LabelPrintingPresetDTO.class);
					final List<List<String>> selectedFields = new ArrayList<>();
					final Statement finalSelectStatement = selectStatement;
					labelPrintingPresetDTO.getSelectedFields().stream().forEach(list -> {
						selectedFields.add(list.stream().map((fieldId) -> {
							try {
								return this.concatenateFieldTypeName(fieldId);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}).filter(Objects::nonNull).collect(Collectors.toList()));
					});

					labelPrintingPresetDTO.setSelectedFields(selectedFields);
					if (!labelPrintingPresetDTO.getBarcodeSetting().isAutomaticBarcode()) {
						final List<String> barCodeFields = labelPrintingPresetDTO.getBarcodeSetting().getBarcodeFields().stream()
							.map((fieldId) -> {
								try {
									return this.concatenateFieldTypeName(fieldId);
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}).filter(Objects::nonNull).collect(Collectors.toList());
						labelPrintingPresetDTO.getBarcodeSetting().setBarcodeFields(barCodeFields);
					}

					final String updatedConfiguration =
						this.jacksonMapper.writerWithView(PresetDTO.View.Configuration.class).writeValueAsString(labelPrintingPresetDTO);

					final String updateSql = String.format(UpdatePresetsTask.UPDATE_PRESET, updatedConfiguration, programPresetId);
					updateStatement.execute(updateSql);
				} catch (IOException e1) {
					dbConn.rollback();
					throw new CustomChangeException(e1);
				}
			}
			dbConn.commit();
		} catch (final SQLException | DatabaseException e) {
			throw new CustomChangeException(e);
		} finally {
			try {
				selectStatement.close();
				updateStatement.close();
			} catch (final SQLException ex) {
				throw new CustomChangeException(ex);
			}
		}

	}

	private String concatenateFieldTypeName(final String fieldId) throws SQLException, DatabaseException {
		final Integer id = Integer.valueOf(fieldId);
		if (id <= UpdatePresetsTask.MAX_FIELD_STATIC_ID) {
			return "STATIC_" + id;
		} else if (isVariable(id)) {
			return "VARIABLE_" + id;
		} else if (isVariable(id - UpdatePresetsTask.MAX_FIXED_TYPE_INDEX)) {
			return "VARIABLE_" + (id - UpdatePresetsTask.MAX_FIXED_TYPE_INDEX);
		} else if (isName(id - UpdatePresetsTask.MAX_FIXED_TYPE_INDEX)) {
			return "NAME_" + (id - UpdatePresetsTask.MAX_FIXED_TYPE_INDEX);
		}
		return null;
	}

	private boolean isVariable(final Integer fieldId) throws SQLException, DatabaseException {
		final Statement selectStatement = this.dbConn.createStatement();
		final ResultSet rs = selectStatement.executeQuery("SELECT * FROM CVTERM WHERE CVTERM_ID = " + fieldId);
		final boolean isVariable = rs.next();
		selectStatement.close();
		return isVariable;
	}

	private boolean isName(final Integer fieldId) throws SQLException, DatabaseException {
		final Statement selectStatement = this.dbConn.createStatement();
		final ResultSet rs = selectStatement.executeQuery("SELECT * FROM udflds WHERE ftable='NAMES' AND ftype='NAME' and fldno = " + fieldId);
		final boolean isName = rs.next();
		selectStatement.close();
		return isName;
	}

	@Override
	public String getConfirmationMessage() {
		return null;
	}

	@Override
	public void setUp() throws SetupException {

	}

	@Override
	public void setFileOpener(final ResourceAccessor resourceAccessor) {

	}

	@Override
	public ValidationErrors validate(final Database database) {
		return null;
	}

}
