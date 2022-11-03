package org.generationcp.middleware.ruleengine.generator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.ruleengine.pojo.ImportedCross;
import org.generationcp.middleware.ruleengine.resolver.KeyComponentValueResolver;
import org.generationcp.middleware.ruleengine.resolver.LocationAbbreviationResolver;
import org.generationcp.middleware.ruleengine.resolver.LocationResolver;
import org.generationcp.middleware.ruleengine.resolver.SeasonResolver;
import org.generationcp.middleware.ruleengine.service.GermplasmNamingProperties;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SeedSourceGenerator {

	private static final String MULTIPARENT_BEGIN_CHAR = "[";
	private static final String MULTIPARENT_END_CHAR = "]";
	private static final String SEEDSOURCE_SEPARATOR = "/";

	@Resource
	private GermplasmNamingProperties germplasmNamingProperties;

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	public SeedSourceGenerator() {

	}

	public String generateSeedSource(final Collection<ObservationUnitData> observations,
		final List<MeasurementVariable> conditions, final String selectionNumber,
		final String plotNumber, final String studyName, final String plantNumber, final Map<String, String> locationIdNameMap,
		final Map<Integer, StudyInstance> studyInstanceMap,
		final List<MeasurementVariable> environmentVariables) {

		if ("0".equals(plotNumber)) {
			return Name.UNKNOWN;
		}

		final KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();

		final KeyComponentValueResolver nameResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return studyName;
			}

			@Override
			public boolean isOptional() {
				return false;
			}
		};

		final KeyComponentValueResolver plotNumberResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return plotNumber;
			}

			@Override
			public boolean isOptional() {
				return false;
			}
		};

		final KeyComponentValueResolver selectionNumberResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return selectionNumber;
			}

			@Override
			public boolean isOptional() {
				return true;
			}
		};

		final KeyComponentValueResolver plantNumberResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return plantNumber;
			}

			@Override
			public boolean isOptional() {
				// Setting to not optional so that "-" wont be appended before plant number when it is specified
				return false;
			}
		};

		final Map<Integer, MeasurementVariable> environmentVariablesByTermId =
			environmentVariables.stream().collect(Collectors.toMap(MeasurementVariable::getTermId, v -> v));


		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.NAME, nameResolver);
		keyComponentValueResolvers.put(KeyComponent.PLOTNO, plotNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.SELECTION_NUMBER, selectionNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.PLANT_NO, plantNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.LOCATION,
			new LocationResolver(conditions, observations, locationIdNameMap));
		keyComponentValueResolvers.put(KeyComponent.LABBR,
			new LocationAbbreviationResolver(observations, studyInstanceMap));
		keyComponentValueResolvers.put(KeyComponent.SEASON,
			new SeasonResolver(this.ontologyVariableDataManager, conditions, observations,
				environmentVariablesByTermId));

		return service.generateKey(new SeedSourceTemplateProvider(this.germplasmNamingProperties,
			ContextHolder.getCurrentCrop()), keyComponentValueResolvers);
	}

	public String generateSeedSourceForCross(final Pair<ObservationUnitRow, ObservationUnitRow> environmentRow,
		final Pair<List<MeasurementVariable>, List<MeasurementVariable>> conditions,
		final Pair<Map<String, String>, Map<String, String>> locationIdNameMap,
		final Pair<Map<Integer, StudyInstance>, Map<Integer, StudyInstance>> studyInstanceMap,
		final Pair<List<MeasurementVariable>, List<MeasurementVariable>> environmentVariables, final ImportedCross crossInfo) {

		final List<String> generatedSeedSources = new ArrayList<>();

		final Integer femalePlotNo = crossInfo.getFemalePlotNo();
		final String femaleSeedSource =
			this.generateSeedSource(environmentRow.getLeft() == null ? new ArrayList<>() : environmentRow.getLeft().getVariables().values(),
				conditions.getLeft(), null, femalePlotNo != null? femalePlotNo.toString() : "", crossInfo.getFemaleStudyName(), null,
				locationIdNameMap.getLeft(), studyInstanceMap.getLeft(), environmentVariables.getLeft());

		final List<Integer> malePlotNos = crossInfo.getMalePlotNos();
		for (final Integer malePlotNo : malePlotNos) {
			final String maleSeedSource =
				this.generateSeedSource(environmentRow.getRight() == null ? new ArrayList<>() : environmentRow.getRight().getVariables().values(),
					conditions.getRight(), null, malePlotNo != null ? malePlotNo.toString() : "", crossInfo.getMaleStudyName(), null,
					locationIdNameMap.getRight(), studyInstanceMap.getRight(),
					environmentVariables.getRight());
			generatedSeedSources.add(maleSeedSource);
		}
		if (malePlotNos.size() > 1) {
			return femaleSeedSource + SeedSourceGenerator.SEEDSOURCE_SEPARATOR + SeedSourceGenerator.MULTIPARENT_BEGIN_CHAR
				+ StringUtils.join(generatedSeedSources, ", ") + SeedSourceGenerator.MULTIPARENT_END_CHAR;
		}

		return femaleSeedSource + SeedSourceGenerator.SEEDSOURCE_SEPARATOR + generatedSeedSources.get(0);
	}


	protected void setGermplasmNamingProperties(final GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}

}
