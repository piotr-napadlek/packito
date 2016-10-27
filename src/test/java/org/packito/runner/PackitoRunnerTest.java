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
import org.packito.test.a.b.SomeDependantBusinessClass;
import org.packito.test.a.c.SomeImportantBusinessLogic;

import java.util.function.Function;

@RunWith(PackitoRunner.class)
@TestedPackage("org.packito.test.a.b")
public class PackitoRunnerTest {

    @TestedClass(autoInstantiate = true)
    private SomeDependantBusinessClass businessClass;

    @MockedDependency
    private SomeImportantBusinessLogic someImportantBusinessLogic;

    @MockProvider
    private Function<Class<?>, ?> mockProvider = Mockito::mock;

    @Before
    public void setUp() {
        Mockito.when(someImportantBusinessLogic.getMeSomething()).thenReturn("Suprise, it's not something!");
    }

    @Test
    public void shouldDoSomething() {
        String result = businessClass.independentMethod("aCSds");
        Assert.assertEquals(result, "CSads");
        Mockito.verifyNoMoreInteractions(someImportantBusinessLogic);
    }

    @Test
    public void shouldDoSomethingElse() {
        String result = businessClass.independentMethod("aCSds");
        Assert.assertEquals(result, "CSads");
    }

    @Test
    public void testDependantMethod() {
        System.out.println(businessClass.someDependantMethod());
        Mockito.verify(someImportantBusinessLogic, Mockito.times(1)).getMeSomething();
    }
}
