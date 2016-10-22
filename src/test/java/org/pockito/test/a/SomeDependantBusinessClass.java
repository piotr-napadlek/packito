package org.pockito.test.a;

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
}
