package org.pockito.runner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pockito.annotations.MockProvider;
import org.pockito.annotations.MockedDependency;
import org.pockito.annotations.TestedClass;
import org.pockito.annotations.TestedPackage;
import org.pockito.test.a.b.d.OwnerClassForBPackage;
import org.pockito.test.a.c.SomeImportantBusinessLogic;

import java.util.function.Function;

@RunWith(PockitoRunner.class)
@TestedPackage("org.pockito.test.a.b")
public class PockitoRunnerTest2 {

    @TestedClass(autoInstantiate = true)
    private OwnerClassForBPackage ownerClassForBPackage;

    @MockedDependency
    private SomeImportantBusinessLogic someImportantBusinessLogic;

    @MockProvider
    private Function<Class<?>, Object> mockProvider = Mockito::mock;

    @Before
    public void setUp() {
        Mockito.when(someImportantBusinessLogic.getMeSomething()).thenReturn("OK");
    }

    @Test
    public void shouldSetUp() {
        Assert.assertNotNull(ownerClassForBPackage);
        String string = ownerClassForBPackage.mixSomeMethods();
        Assert.assertEquals(ownerClassForBPackage.getInjectedClass(), ownerClassForBPackage.getBusinessClass().getInjectedClass());
        System.out.println(string);
    }
}
