package org.generationcp.middleware.ruleengine.naming.deprecated.expression;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.generationcp.middleware.service.impl.KeySequenceRegisterServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DeprecatedDoubleHaploidSourceExpressionIntegrationTest extends IntegrationTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(DeprecatedDoubleHaploidSourceExpressionIntegrationTest.class);

    @Test
    public void testDoubleHaploidApplyRuleWithMultipleThreads() throws ExecutionException, InterruptedException {

        final DeprecatedAdvancingSource source = new DeprecatedAdvancingSource();

        final int threads = 10;
        final List<Future<String>> resultingDesignations = new ArrayList<>();

        final ExecutorService threadPool = Executors.newFixedThreadPool(threads);

        final KeySequenceRegisterService keySequenceRegisterService =
                new KeySequenceRegisterServiceImpl(this.sessionProvder);

        for (int i = 1; i <= threads; i++) {
            final Future<String> result = threadPool.submit(new Callable<String>() {
                @Override
                public String call() {
                    final List<StringBuilder> values = new ArrayList<>();
                    values.add(new StringBuilder("WM14AST0001L@0[DHSOURCE]"));
                    final DeprecatedDoubleHaploidSourceExpression
                        doubleHaploidSourceExpression = new DeprecatedDoubleHaploidSourceExpression();
                    doubleHaploidSourceExpression.setKeySequenceRegisterService(keySequenceRegisterService);

                    doubleHaploidSourceExpression.apply(values, source, null);
                    return values.get(0).toString();
                }
            });
            resultingDesignations.add(result);
        }

        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }

        final Set<String> uniqueDesignationSet = new HashSet<>();
        for (final Future<String> future : resultingDesignations) {
            final String generatedDesignation = future.get();
            uniqueDesignationSet.add(generatedDesignation);
            LOG.info("Designation returned: {}.", generatedDesignation);
        }

        Assert.assertEquals("Each thread must return a unique designation.", threads, uniqueDesignationSet.size());
    }

}
