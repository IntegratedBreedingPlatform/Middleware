package org.generationcp.middleware.service.api;


public interface KeySequenceRegisterService {

	/**
	 * Finds next available sequence numbwe for a given keyPrefix and increments the last known sequence number in the key sequence
	 * registry.
	 * 
	 * <p>
	 * <strong>Important notes regarding concurrency and thread safety:</strong>
	 * 
	 * <p>
	 * Calling code must employ thread synchronizationat the appropriate level - usually the very top level entry point such as a controller
	 * method or a Vaadin action. For example, in case of Germplasm naming (coding) the top level action that uses key sequence registry is
	 * <strong>org.generationcp.breeding.manager.listmanager.dialog.AssignCodesDialog.assignCodes()</strong>. You can see that this method
	 * calls the entire back-end processing logic in a synchronized code block, using AssignCodesDialog class as the lock object which means
	 * only one instance of AssignCodesDialog can enter it at a time. This use case employs pessimistic locking to ensure that the germplasm
	 * coded names with sequences are unique and are in series as much as possible.
	 * 
	 * <p>
	 * Service does implement Hibernate based optimistic locking as ultimate mechanism to ensure that two threads never end up getting the
	 * same sequence number from registry. If explicit thread synchronization is not done, in concurrent access situation, expect
	 * <strong>HibernateOptimisticLockingFailureException</strong> to occur. This is a retriable exception. Callers can catch it and
	 * operation can be retried.
	 */
	int incrementAndGetNextSequence(String keyPrefix);
}
