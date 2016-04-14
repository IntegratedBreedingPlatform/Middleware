package org.generationcp.middleware.components.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Optional;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * {@link ChainValidator} is an abstract so this test exists to test it's functionality only once and not to test
 * every validator that extends from ChainValidator. For syntactical reasons the FailLastValidatorUT creates a
 * {@link TestAbstractFailLastValidator} but the tested behavior belongs to the FailFastValidator.
 *
 * {@link TestAbstractFailLastValidator} is known by the test so the only behavior tested belongs to the ChainValidator.
 * {@link TestDummyContext} is a context to be validated by the validator.
 */
public class ChainValidatorUT {

	public static final String ERROR_MESSAGE = "Validation Rule 1 failed";
	public static final String ANOTHER_ERROR_MESSAGE = "Validation Rule 2 failed";

	public static final int EXPECTED_ERROR_LIST_SIZE = 1;
	private static final int EXPECTED_ERROR_LIST_SIZE_2 = 2;

	ChainValidator<TestDummyContext> validator;

	TestDummyContext context;

	@Mock
	ValidationRule<TestDummyContext> successRule;

	@Mock
	ValidationRule<TestDummyContext> failedRule;

	@Mock
	ValidationRule<TestDummyContext> anotherFailedRule;

	@Mock
	ValidationRule<TestDummyContext> unexpectedFailureRule;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		context = new TestDummyContext();
		doThrow(new RuntimeException()).when(unexpectedFailureRule).validate(context);
		when(failedRule.validate(any(TestDummyContext.class))).thenReturn(Optional.of(ERROR_MESSAGE));
		when(anotherFailedRule.validate(any(TestDummyContext.class))).thenReturn(Optional.of(ANOTHER_ERROR_MESSAGE));
		when(successRule.validate(any(TestDummyContext.class))).thenReturn(Optional.<String>absent());

	}

		/**
	 * Validator returns a specific error per {@link ValidationRule} that fails.
	 */
	@Test
	public void validatorReturnsRuleSpecificErrorMessageGatherByRuleCollection() throws Exception {
		validator = stubValidator(failedRule);
		((TestAbstractFailLastValidator) validator).setRule(anotherFailedRule);

		ErrorCollection errorCollection = validator.validate(context);

		assertThat(errorCollection).hasSize(EXPECTED_ERROR_LIST_SIZE_2);
		assertThat(errorCollection).contains(ERROR_MESSAGE,ANOTHER_ERROR_MESSAGE);


	}
	/**
	 * Validator returns an empty {@link ErrorCollection} if context is valid.
	 */
	@Test
	public void validatorPassWhenRuleCollectionReturnEmptyErrorCollection() throws Exception {
		validator = stubValidator(successRule);

		ErrorCollection errorCollection = validator.validate(context);

		assertThat(errorCollection).isEmpty();

	}

	private ChainValidator<TestDummyContext> stubValidator(ValidationRule<TestDummyContext> rule) {
		validator = new TestAbstractFailLastValidator();
		((TestAbstractFailLastValidator) validator).setRule(rule);

		return validator;
	}

	private class TestAbstractFailLastValidator extends ChainValidator<TestDummyContext> {
		public void setRule(ValidationRule<TestDummyContext> rule){
			add(rule);
		}
	}


	private class TestDummyContext {

	}
}
