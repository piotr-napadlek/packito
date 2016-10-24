package org.pockito.packagescanner;

import java.io.IOException;
import java.util.List;

public interface PackageScanner {
    List<Class<?>> findAllClassesInAPackage(String packageName) throws IOException;
}
