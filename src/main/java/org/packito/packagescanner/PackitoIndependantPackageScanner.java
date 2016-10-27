package org.packito.packagescanner;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackitoIndependantPackageScanner implements PackageScanner {

    @Override
    public List<Class<?>> findAllClassesInAPackage(String packageName) throws IOException {
        Map<File, ClassLoader> classLoaderResources = getClassLoaderResources(PackitoIndependantPackageScanner.class.getClassLoader());
        return scanResources(classLoaderResources).entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(packageName))
                .map(entry -> {
            try {
                return entry.getValue().loadClass(entry.getKey());
            } catch (ClassNotFoundException e) {
                System.out.println("Class " + entry.getKey() + " not found.");
                return null;
            }
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<String, ClassLoader> scanResources(Map<File, ClassLoader> classLoaderResources) {
        Set<File> scannedResources = new HashSet<>();
        Map<String, ClassLoader> classesForClassLoaders = new HashMap<>();
        for (Map.Entry<File, ClassLoader> file : classLoaderResources.entrySet()) {
            if (scannedResources.add(file.getKey())) {
                if (file.getKey().exists()) {
                    if (file.getKey().isDirectory()) {
                        scanDirectory(file.getKey(), file.getValue(), "")
                                .forEach(res -> classesForClassLoaders.put(res, file.getValue()));
                    }
                }
            }
        }
        return classesForClassLoaders;
    }

    private List<String> scanDirectory(File file, ClassLoader classLoader, String packagePrefix) {
        File[] files = file.listFiles();
        List<String> classNames = new ArrayList<>();
        if (files == null) {
            return Collections.emptyList();
        }
        Stream.of(files).forEach(f -> {
            if (f.isDirectory()) {
                classNames.addAll(scanDirectory(f, classLoader, packagePrefix + f.getName() + "."));
            } else {
                String resourceName = packagePrefix + f.getName().replaceAll(".class", "");
                if (!resourceName.equals(JarFile.MANIFEST_NAME)) {
                    classNames.add(resourceName);
                }
            }
        });
        return classNames;
    }

    private Map<File, ClassLoader> getClassLoaderResources(ClassLoader classLoader) {
        Map<File, ClassLoader> resources = new HashMap<>();
        if (classLoader.getParent() != null) {
            resources.putAll(getClassLoaderResources(classLoader.getParent()));
        }
        if (classLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            Stream.of(urlClassLoader.getURLs())
                    .filter(url -> url.getProtocol().equals("file"))
                    .map(url -> new File(url.getFile()))
                    .filter(file -> !resources.containsKey(file))
                    .forEach(file -> resources.put(file, classLoader));
        }
        return resources;
    }
}
