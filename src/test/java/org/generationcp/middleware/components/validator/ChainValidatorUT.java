package org.generationcp.middleware.components.validator;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * {@link ChainValidator} is an abstract so this test exists to test it's functionality only once and not to test
 * every validator that extends from ChainValidator. For syntactical reasons the ChainValidatorUT creates a
 * {@link TestChainValidator} but the tested behavior belongs to the ChainValidator.
 *
 * {@link TestChainValidator} is known by the test so the only behavior tested belongs to the ChainValidator.
 * {@link TestDummyContext} is a context to be validated by the validator.
 */
public class ChainValidatorUT {

	public static final String ERROR_MESSAGE = "Validation Rule 1 failed";
	public static final String ANOTHER_ERROR_MESSAGE = "Validation Rule 2 failed";

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
		ErrorMessage errorMessage = new ErrorMessage(ERROR_MESSAGE);
		ErrorMessage anotherErrorMessage = new ErrorMessage(ANOTHER_ERROR_MESSAGE);
		when(failedRule.validate(any(TestDummyContext.class))).thenReturn(Optional.of(errorMessage));
		when(anotherFailedRule.validate(any(TestDummyContext.class))).thenReturn(Optional.of(anotherErrorMessage));
		when(successRule.validate(any(TestDummyContext.class))).thenReturn(Optional.<ErrorMessage>absent());

	}

		/**
	 * Validator returns a specific error per {@link ValidationRule} that fails.
	 */
	@Test
	public void validatorReturnsRuleSpecificErrorMessageGatherByRuleCollection() throws Exception {
		validator = stubValidator(failedRule);
		((TestChainValidator) validator).setRule(anotherFailedRule);

		ErrorCollection errorCollection = validator.validate(context);

		assertThat(errorCollection).hasSize(EXPECTED_ERROR_LIST_SIZE_2);

		Iterator<ErrorMessage> iterator = errorCollection.iterator();
		assertThat(iterator.next().getKey()).isEqualToIgnoringCase(ERROR_MESSAGE);
		assertThat(iterator.next().getKey()).isEqualToIgnoringCase(ANOTHER_ERROR_MESSAGE);




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
		validator = new TestChainValidator();
		((TestChainValidator) validator).setRule(rule);

		return validator;
	}

	private class TestChainValidator extends ChainValidator<TestDummyContext> {
		public void setRule(ValidationRule<TestDummyContext> rule){
			add(rule);
		}
	}


	private class TestDummyContext {

	}
}
