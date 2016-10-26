package org.pockito.packagescanner;

import java.io.IOException;
import java.util.List;

public class PockitoIndependantPackageScanner implements PackageScanner {
    @Override
    public List<Class<?>> findAllClassesInAPackage(String packageName) throws IOException {

        return null;
    }
}
