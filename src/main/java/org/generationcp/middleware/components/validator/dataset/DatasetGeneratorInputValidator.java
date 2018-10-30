package org.generationcp.middleware.components.validator.dataset;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.dataset.DatasetGeneratorInput;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
public class DatasetGeneratorInputValidator {

	@Autowired
	private StudyService studyService;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private Environment environment;

	private Integer maxAllowedSubobservationUnits;

	@PostConstruct
	public void init() {
		maxAllowedSubobservationUnits = Integer.parseInt(environment.getProperty("maximum.number.of.sub.observation.parent.unit"));
	}

	public void validate(final Integer studyId, final DatasetGeneratorInput o, final Errors errors) {

		if (DataSetType.findById(o.getDatasetTypeId()) == null) {
			errors.reject("dataset.type.invalid", new String[] {String.valueOf(o.getDatasetTypeId())}, "");
		}

		if (o.getDatasetName() != null && o.getDatasetName().length() > 100) {
			errors.reject("dataset.name.exceed.length");
		}

		final List<StudyInstance> studyInstances = this.studyService.getStudyInstances(studyId);

		Function<StudyInstance, Integer> studyInstancesToIds = new Function<StudyInstance, Integer>() {

			public Integer apply(StudyInstance i) {
				return i.getInstanceDbId();
			}
		};

		List<Integer> studyInstanceIds = Lists.transform(studyInstances, studyInstancesToIds);

		if (o.getInstanceIds().length == 0 || !studyInstanceIds.containsAll(Arrays.asList(o.getInstanceIds()))) {
			errors.reject("dataset.invalid.instances");
		}

		final Study study = studyDataManager.getStudy(studyId);
		try {
			Variable ontologyVariable = this.ontologyVariableDataManager.getVariable(study.getProgramUUID(), o.getSequenceVariableId(), true, true);

			if (ontologyVariable == null || !ontologyVariable.getVariableTypes().contains(VariableType.OBSERVATION_UNIT)) {
				errors.reject("dataset.invalid.obs.unit.variable", new String[] {String.valueOf(o.getSequenceVariableId())}, "");
			}

		} catch (MiddlewareException e) {
			errors.reject("dataset.invalid.obs.unit.variable", new String[] {String.valueOf(o.getSequenceVariableId())}, "");
		}

		if (o.getNumberOfSubObservationUnits() > maxAllowedSubobservationUnits) {
			errors.reject("dataset.invalid.number.subobs.units", new String[] {String.valueOf(maxAllowedSubobservationUnits)}, "");
		}
	}

}
