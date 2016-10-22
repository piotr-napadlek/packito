package org.pockito.validation;


import org.pockito.annotations.TestedClass;
import org.pockito.annotations.TestedPackage;
import org.pockito.scanner.util.ScannerUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PockitoTestClassValidator implements TestValidator {

    public void validate(Class<?> klass) throws PockitoValidationException {
        validateClassLevelAnnotationPresence(klass);
        validateFieldLevelAnnotationPresence(klass);
    }

    private void validateClassLevelAnnotationPresence(Class<?> klass) throws PockitoValidationException {
        List<Annotation> classAnnotations = new ArrayList<>();

        ScannerUtils.iterateClassHierarchy(klass, k -> classAnnotations.addAll(Arrays.asList(k.getDeclaredAnnotations())));

        List<? extends Class<? extends Annotation>> annotationClasses =
                classAnnotations.stream().map(Annotation::annotationType).collect(Collectors.toList());
        if (annotationClasses.stream().noneMatch(TestedPackage.class::equals)) {
            throw new PockitoValidationException("Tested class or any of it superclasses should be annotated with @TestedPackage "
                                                         + "to indicate the package which will be scanned for test dependency injection.");
        }
        if (annotationClasses.stream().filter(TestedPackage.class::equals).count() > 1) {
            throw new PockitoValidationException("Found more than one class annotated with @TestedPackage in test class hierarchy. "
                                                         + "Please annotate only one class in hierarchy to avoid confusion");
        }
    }

    private void validateFieldLevelAnnotationPresence(Class<?> klass) throws PockitoValidationException {
        Map<Field, List<Annotation>> fieldAnnotations = new HashMap<>();

        ScannerUtils.iterateClassHierarchy(klass, k -> {
            for (Field field : k.getDeclaredFields()) {
                fieldAnnotations.put(field, Arrays.asList(field.getDeclaredAnnotations()));
            }
        });
        if (fieldAnnotations.values().stream().flatMap(List::stream).map(Annotation::annotationType).noneMatch(TestedClass.class::equals)) {
            throw new PockitoValidationException("Found no field marked as @TestedObject in class hierarchy. At least one is needed "
                                                         + "to process the test.");
        }
        if (fieldAnnotations.values().stream().flatMap(List::stream).map(Annotation::annotationType).filter(TestedClass.class::equals).count() > 1) {
            throw new PockitoValidationException("Found more than one field marked as @TestedObject. For now only one @TestedClass is allowed.");
        }
    }

}
