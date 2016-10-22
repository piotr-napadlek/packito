package org.pockito.runner;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.mockito.internal.runners.util.FrameworkUsageValidator;
import org.pockito.validation.PockitoTestClassValidator;

/**
 * Compatibility: Junit 4.5 and higher
 */
public class PockitoRunner extends BlockJUnit4ClassRunner {


    public PockitoRunner(Class<?> testedClass) throws InitializationError {
        super(testedClass);
        new PockitoTestClassValidator().validate(testedClass);
    }

    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        return super.withBefores(method, target, statement);
    }

    @Override
    public void run(RunNotifier notifier) {
        // to keep mockito validation running
        notifier.addListener(new FrameworkUsageValidator(notifier));
        super.run(notifier);
    }
}
