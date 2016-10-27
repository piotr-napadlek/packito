package org.packito.test.a.b.d;

import org.packito.test.a.b.InjectedClass;
import org.packito.test.a.b.SomeDependantBusinessClass;
import org.packito.test.a.c.SomeImportantBusinessLogic;

import javax.inject.Inject;

public class OwnerClassForBPackage {

    @Inject
    private InjectedClass injectedClass;

    @Inject
    private SomeDependantBusinessClass businessClass;

    @Inject
    private SomeImportantBusinessLogic someImportantBusinessLogic;

    public String mixSomeMethods() {
        String hey = injectedClass.addBToString(businessClass.independentMethod("hey"));
        return someImportantBusinessLogic.getMeSomething().concat(hey);
    }

    public InjectedClass getInjectedClass() {
        return injectedClass;
    }

    public SomeDependantBusinessClass getBusinessClass() {
        return businessClass;
    }
}
