package org.packito.runner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.packito.annotations.MockProvider;
import org.packito.annotations.MockedDependency;
import org.packito.annotations.TestedClass;
import org.packito.annotations.TestedPackage;
import org.packito.test.a.b.d.OwnerClassForBPackage;
import org.packito.test.a.c.SomeImportantBusinessLogic;

import java.util.function.Function;

@RunWith(PackitoRunner.class)
@TestedPackage("org.packito.test.a.b")
public class PackitoRunnerTest2 {

    @TestedClass(autoInstantiate = true)
    private OwnerClassForBPackage ownerClassForBPackage;

    @MockedDependency
    private SomeImportantBusinessLogic someImportantBusinessLogic;

    @MockProvider
    private Function<Class<?>, Object> mockProvider = Mockito::mock;

    @Before
    public void setUp() {
        Mockito.when(someImportantBusinessLogic.getMeSomething()).thenReturn("Here you are");
    }

    @Test
    public void shouldSetUp() {
        Assert.assertNotNull(ownerClassForBPackage);
        String string = ownerClassForBPackage.mixSomeMethods();
        Assert.assertEquals(ownerClassForBPackage.getInjectedClass(), ownerClassForBPackage.getBusinessClass().getInjectedClass());
        System.out.println(string);
    }
}
