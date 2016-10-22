package org.pockito.scanner.util;

import java.util.function.Consumer;

public class ScannerUtils {

    private ScannerUtils() {
    }

    public static void iterateClassHierarchy(Class<?> klass, Consumer<Class> klassConsumer) {
        Class scannedClass = klass;
        while (!isObject(scannedClass)) {
            klassConsumer.accept(scannedClass);
            scannedClass = klass.getSuperclass();
        }
    }

    private static boolean isObject(Class scannedClass) {
        return scannedClass == null || scannedClass.equals(Object.class);
    }
}
