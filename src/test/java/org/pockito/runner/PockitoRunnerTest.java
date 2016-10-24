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
import org.pockito.test.a.b.SomeDependantBusinessClass;
import org.pockito.test.a.c.SomeImportantBusinessLogic;

import java.util.function.Function;

@RunWith(PockitoRunner.class)
@TestedPackage("org.pockito.test.a.b")
public class PockitoRunnerTest {

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
