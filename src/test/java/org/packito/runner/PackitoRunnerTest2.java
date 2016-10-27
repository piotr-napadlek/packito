package org.packito.runner;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.packito.annotations.MockProvider;
import org.packito.annotations.TestedClass;
import org.packito.annotations.TestedPackage;
import org.packito.test.a.b.d.OwnerClassForBPackage;

import java.util.function.Function;

@RunWith(PackitoRunner.class)
@TestedPackage("org.packito.test.a.b")
public class PackitoRunnerTest2 {

    @TestedClass(autoInstantiate = true)
    private OwnerClassForBPackage ownerClassForBPackage;

    @MockProvider
    private Function<Class<?>, Object> mockProvider = Mockito::mock;

    @BeforeClass
    public static void setUp() {
    }

    @Test
    public void shouldSetUp() {
        Assert.assertNotNull(ownerClassForBPackage);
        String string = ownerClassForBPackage.mixSomeMethods();
        Assert.assertEquals(ownerClassForBPackage.getInjectedClass(), ownerClassForBPackage.getBusinessClass().getInjectedClass());
        System.out.println(string);
    }
}
