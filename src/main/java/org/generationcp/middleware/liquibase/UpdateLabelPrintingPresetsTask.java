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
import java.util.stream.Collectors;

public class UpdateLabelPrintingPresetsTask implements liquibase.change.custom.CustomTaskChange {

	private static final Integer MAX_FIELD_STATIC_ID = 63;
	private ObjectMapper jacksonMapper;

	@Override
	public void execute(final Database database) throws CustomChangeException {

		final List<String> tool_sections = Arrays.asList(
			"DATASET_LABEL_PRINTING_PRESET", "LOT_LABEL_PRINTING_PRESET",
			"GERMPLASM_LABEL_PRINTING_PRESET", "GERMPLASM_LIST_LABEL_PRINTING_PRESET",
			"STUDY_ENTRIES_LABEL_PRINTING_PRESET");

		this.jacksonMapper = new ObjectMapper();
		this.jacksonMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		JdbcConnection dbConn = null;
		Statement selectStatement = null;
		Statement updateStatement = null;

		try {
			dbConn = (JdbcConnection) database.getConnection();
			dbConn.setAutoCommit(false);
			selectStatement = dbConn.createStatement();
			updateStatement = dbConn.createStatement();

			ResultSet rs = selectStatement.executeQuery(
				"SELECT * FROM program_preset where tool_section in (" +
					tool_sections.stream().collect(Collectors.joining("','", "'", "'"))
					+ ")");

			while (rs.next()) {
				final String program_preset_id = rs.getString("program_preset_id");
				final String configuration = rs.getString("configuration");

				try {
					final LabelPrintingPresetDTO labelPrintingPresetDTO =
						this.jacksonMapper.readValue(configuration, LabelPrintingPresetDTO.class);
					final List<List<String>> selectedFields = new ArrayList<>();
					labelPrintingPresetDTO.getSelectedFields().stream().forEach(list -> {
						selectedFields.add(list.stream().map((fieldId) -> {
							return this.concatenateFieldTypeName(fieldId);
						}).collect(Collectors.toList()));
					});

					labelPrintingPresetDTO.setSelectedFields(selectedFields);
					if (!labelPrintingPresetDTO.getBarcodeSetting().isAutomaticBarcode()) {
						final List<String> barCodeFields = labelPrintingPresetDTO.getBarcodeSetting().getBarcodeFields().stream()
							.map((fieldId) -> {
								return this.concatenateFieldTypeName(fieldId);
							}).collect(Collectors.toList());
						labelPrintingPresetDTO.getBarcodeSetting().setBarcodeFields(barCodeFields);
					}

					final String UpdatedConfiguration =
						this.jacksonMapper.writerWithView(PresetDTO.View.Configuration.class).writeValueAsString(labelPrintingPresetDTO);

					final String updateSql = String.format(//
						"update program_preset set configuration = '%s' where program_preset_id = %s", UpdatedConfiguration, program_preset_id);
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

	private String concatenateFieldTypeName(final String fieldId) {
		if (Integer.valueOf(fieldId) > UpdateLabelPrintingPresetsTask.MAX_FIELD_STATIC_ID) {
			return "VARIABLE_" + fieldId;
		} else {
			return "STATIC_" + fieldId;
		}
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
