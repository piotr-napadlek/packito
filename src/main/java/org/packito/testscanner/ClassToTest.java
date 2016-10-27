package org.packito.testscanner;

import java.lang.reflect.Field;

public class ClassToTest {
    private Class<?> klass;
    private Object instance;
    private boolean autoInstantiate;
    private Field injectionField;
    private Object containingTestClass;

    public Class<?> getKlass() {
        return klass;
    }

    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public boolean isAutoInstantiate() {
        return autoInstantiate;
    }

    public void setAutoInstantiate(boolean autoInstantiate) {
        this.autoInstantiate = autoInstantiate;
    }

    public Field getInjectionField() {
        return injectionField;
    }

    public void setInjectionField(Field injectionField) {
        this.injectionField = injectionField;
    }

    public Object getContainingTestClass() {
        return containingTestClass;
    }

    public void setContainingTestClass(Object containingTestClass) {
        this.containingTestClass = containingTestClass;
    }
}
