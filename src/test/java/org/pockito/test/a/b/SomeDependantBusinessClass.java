package org.pockito.test.a.b;

import org.pockito.test.a.c.SomeImportantBusinessLogic;

import javax.inject.Inject;
import java.util.Arrays;

public class SomeDependantBusinessClass {

    @Inject
    private SomeImportantBusinessLogic someImportantBusinessLogic;

    public String independentMethod(String input) {
        char[] chars = input.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    public String someDependantMethod() {
        return independentMethod(someImportantBusinessLogic.getMeSomething());
    }

}
