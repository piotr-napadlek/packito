package org.packito.packagescanner;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackitoIndependentPackageScanner implements PackageScanner {

    @Override
    public List<Class<?>> findAllClassesInAPackage(String packageName) throws IOException {
        Map<File, ClassLoader> classLoaderResources = getClassLoaderResources(PackitoIndependentPackageScanner.class.getClassLoader());
        Map<String, ClassLoader> classForClassLoader = scanResources(classLoaderResources);
        return classForClassLoader.entrySet().stream()
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
            classesForClassLoaders.putAll(scanResource(scannedResources, file.getKey(), file.getValue()));
        }
        return classesForClassLoaders;
    }

    private Map<String, ClassLoader> scanResource(Set<File> scannedResources, File file, ClassLoader classLoader) {
        Map<String, ClassLoader> classNameForClassLoader = new HashMap<>();
        if (scannedResources.add(file) && file.exists()) {
            if (file.isDirectory()) {
                scanDirectory(file, classLoader, "")
                        .forEach(res -> classNameForClassLoader.put(res, classLoader));
            } else {
                scanJar(file, classLoader, scannedResources)
                        .forEach(classNameForClassLoader::put);
            }
        }
        return classNameForClassLoader;
    }

    private Set<String> scanDirectory(File file, ClassLoader classLoader, String packagePrefix) {
        File[] files = file.listFiles();
        Set<String> classNames = new HashSet<>();
        if (files == null) {
            return Collections.emptySet();
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

    private Map<String, ClassLoader> scanJar(File file, ClassLoader classLoader, Set<File> scannedResources) {
        JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
        Manifest manifest;
        try {
            manifest = jarFile.getManifest();
            if (manifest == null) {
                return Collections.emptyMap();
            }
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        Map<String, ClassLoader> classForClassLoader = scanJarManifest(classLoader, file, manifest, scannedResources);
        classForClassLoader.putAll(scanJarEntries(classLoader, jarFile));
        try {
            jarFile.close();
        } catch (IOException e) { }
        return classForClassLoader;
    }

    private Map<String, ClassLoader> scanJarEntries(ClassLoader classLoader, JarFile jarFile) {
        Map<String, ClassLoader> filesInJar = new HashMap<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (!jarEntry.isDirectory() && !jarEntry.getName().equals(JarFile.MANIFEST_NAME) && jarEntry.getName().endsWith(".class")) {
                filesInJar.put(jarEntry.getName().replaceAll("/", ".").replaceAll(".class", ""), classLoader);
            }
        }
        return filesInJar;
    }

    private Map<String, ClassLoader> scanJarManifest(ClassLoader classLoader, File jarFile, Manifest manifest, Set<File> scannedResources) {

        String classPathAttribute = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);
        if (classPathAttribute == null) {
            return new HashMap<>();
        }
        Set<File> filesToScan = Stream.of(classPathAttribute.split(" "))
                .filter(s -> s != null && !s.isEmpty()).map(s -> {
                    try {
                        return new URL(jarFile.toURI().toURL(), s);
                    } catch (MalformedURLException e) {
                        return null;
                    }
                })
                .filter(u -> u != null && u.getProtocol().equals("file"))
                .map(url -> new File(url.getFile()))
                .collect(Collectors.toSet());
        Map<String, ClassLoader> classForClassLoader = new HashMap<>();
        filesToScan.forEach(file -> classForClassLoader.putAll(this.scanResource(scannedResources, file, classLoader)));
        return classForClassLoader;
    }
}
