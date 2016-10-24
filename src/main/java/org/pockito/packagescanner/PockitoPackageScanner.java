package org.pockito.packagescanner;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PockitoPackageScanner implements PackageScanner{
    @Override
    public List<Class<?>> findAllClassesInAPackage(String packageName) throws IOException {
        return ClassPath.from(this.getClass().getClassLoader())
                .getTopLevelClassesRecursive(packageName)
                .stream().map(ClassPath.ClassInfo::load)
                .collect(Collectors.toList());
    }
}
