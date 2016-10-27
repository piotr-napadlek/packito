package org.packito.runner;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.packito.annotations.TestedPackage;
import org.packito.injector.PackitoDependencyInjector;
import org.packito.packagescanner.PackitoPackageScanner;
import org.packito.testscanner.ClassToTest;
import org.packito.testscanner.DependenciesContainer;
import org.packito.testscanner.PackitoInitializationException;
import org.packito.testscanner.PackitoProvidedDependenciesScanner;
import org.packito.testscanner.ProvidedDependenciesScanner;
import org.packito.testscanner.TestClassScanner;
import org.packito.testscanner.util.ScannerUtils;
import org.packito.validation.PackitoTestClassValidator;

import java.io.IOException;
import java.util.List;

/**
 * Compatibility: Junit 4.5 and higher
 */
public class PackitoRunner extends BlockJUnit4ClassRunner {

    ProvidedDependenciesScanner dependenciesScanner = new PackitoProvidedDependenciesScanner();

    public PackitoRunner(Class<?> testedClass) throws InitializationError {
        super(testedClass);
        new PackitoTestClassValidator().validate(testedClass);
    }

    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        DependenciesContainer providedDependencies = dependenciesScanner.getProvidedDependencies(target);
        ClassToTest classToTest = TestClassScanner.findClassToTest(target);
        String packageScope = ScannerUtils.findAnnotationInClassHierarchy(target.getClass(), TestedPackage.class).value();
        List<Class<?>> classesInPackage;
        try {
            classesInPackage = new PackitoPackageScanner().findAllClassesInAPackage(packageScope);
        } catch (IOException e) {
            e.printStackTrace();
            throw new PackitoInitializationException("Could not scan the classes in a package " + packageScope);
        }
        new PackitoDependencyInjector().injectDependenciesDeep(classToTest, classesInPackage, providedDependencies);
        return super.withBefores(method, target, statement);
    }

    @Override
    public void run(RunNotifier notifier) {
        // to keep mockito validation running
        //notifier.addListener(new FrameworkUsageValidator(notifier));
        super.run(notifier);
    }
}
