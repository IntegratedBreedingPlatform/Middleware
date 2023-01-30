package org.generationcp.middleware.ruleengine.generator;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.ruleengine.naming.context.AdvanceContext;
import org.generationcp.middleware.ruleengine.resolver.HabitatDesignationResolver;
import org.generationcp.middleware.ruleengine.resolver.KeyComponentValueResolver;
import org.generationcp.middleware.ruleengine.resolver.LocationAbbreviationResolver;
import org.generationcp.middleware.ruleengine.resolver.LocationResolver;
import org.generationcp.middleware.ruleengine.resolver.ProjectPrefixResolver;
import org.generationcp.middleware.ruleengine.resolver.SeasonResolver;
import org.generationcp.middleware.ruleengine.service.GermplasmNamingProperties;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BreedersCrossIDGenerator {

	protected static final List<Integer> ENVIRONMENT_VARIABLE_TYPES = Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId());

	@Resource
	private GermplasmNamingProperties germplasmNamingProperties;

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	public String generateBreedersCrossID(final Collection<ObservationUnitData> observations) {

		final KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();
		final List<MeasurementVariable> studyEnvironmentVariables = AdvanceContext.getStudyEnvironmentVariables();
		final Map<Integer, MeasurementVariable> environmentVariablesByTermId = AdvanceContext.getEnvironmentVariablesByTermId();

		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.PROJECT_PREFIX,
			new ProjectPrefixResolver(this.ontologyVariableDataManager, studyEnvironmentVariables, observations,
				environmentVariablesByTermId));
		keyComponentValueResolvers.put(KeyComponent.HABITAT_DESIGNATION,
			new HabitatDesignationResolver(this.ontologyVariableDataManager, studyEnvironmentVariables, observations,
				environmentVariablesByTermId));
		keyComponentValueResolvers.put(KeyComponent.SEASON,
			new SeasonResolver(this.ontologyVariableDataManager, studyEnvironmentVariables, observations,
				environmentVariablesByTermId));
		keyComponentValueResolvers.put(KeyComponent.LOCATION,
			new LocationResolver(studyEnvironmentVariables, observations, AdvanceContext.getLocationsNamesByIds()));
		keyComponentValueResolvers.put(KeyComponent.LABBR,
			new LocationAbbreviationResolver(observations, AdvanceContext.getStudyInstancesByInstanceNumber()));
		return service
			.generateKey(new BreedersCrossIDTemplateProvider(this.germplasmNamingProperties), keyComponentValueResolvers);
	}

	protected void setGermplasmNamingProperties(final GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}
}
