package org.packito.test.a.b;

import org.packito.test.a.c.SomeImportantBusinessLogic;

import javax.inject.Inject;
import java.util.Arrays;

public class SomeDependantBusinessClass {

    @Inject
    private SomeImportantBusinessLogic someImportantBusinessLogic;

    @Inject
    private InjectedClass injectedClass;

    public String independentMethod(String input) {
        char[] chars = input.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    public String someDependantMethod() {
        return injectedClass.addBToString(independentMethod(someImportantBusinessLogic.getMeSomething()));
    }

    public InjectedClass getInjectedClass() {
        return injectedClass;
    }
}
