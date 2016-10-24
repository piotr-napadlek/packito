package org.pockito.runner;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.pockito.annotations.TestedPackage;
import org.pockito.injector.PockitoDependencyInjector;
import org.pockito.packagescanner.PockitoPackageScanner;
import org.pockito.testscanner.ClassToTest;
import org.pockito.testscanner.DependenciesContainer;
import org.pockito.testscanner.PockitoInitializationException;
import org.pockito.testscanner.PockitoProvidedDependenciesScanner;
import org.pockito.testscanner.ProvidedDependenciesScanner;
import org.pockito.testscanner.TestClassScanner;
import org.pockito.testscanner.util.ScannerUtils;
import org.pockito.validation.PockitoTestClassValidator;

import java.io.IOException;
import java.util.List;

/**
 * Compatibility: Junit 4.5 and higher
 */
public class PockitoRunner extends BlockJUnit4ClassRunner {

    ProvidedDependenciesScanner dependenciesScanner = new PockitoProvidedDependenciesScanner();

    public PockitoRunner(Class<?> testedClass) throws InitializationError {
        super(testedClass);
        new PockitoTestClassValidator().validate(testedClass);
    }

    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        DependenciesContainer providedDependencies = dependenciesScanner.getProvidedDependencies(target);
        ClassToTest classToTest = TestClassScanner.findClassToTest(target);
        String packageScope = ScannerUtils.findAnnotationInClassHierarchy(target.getClass(), TestedPackage.class).value();
        List<Class<?>> classesInPackage;
        try {
            classesInPackage = new PockitoPackageScanner().findAllClassesInAPackage(packageScope);
        } catch (IOException e) {
            e.printStackTrace();
            throw new PockitoInitializationException("Could not scan the classes in a package " + packageScope);
        }
        new PockitoDependencyInjector().injectDependenciesDeep(classToTest, classesInPackage, providedDependencies);
        return super.withBefores(method, target, statement);
    }

    @Override
    public void run(RunNotifier notifier) {
        // to keep mockito validation running
        //notifier.addListener(new FrameworkUsageValidator(notifier));
        super.run(notifier);
    }
}
