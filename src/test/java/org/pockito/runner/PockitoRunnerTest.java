package org.pockito.runner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pockito.annotations.TestedClass;
import org.pockito.annotations.TestedPackage;
import org.pockito.test.a.SomeDependantBusinessClass;

@RunWith(PockitoRunner.class)
@TestedPackage("org.pockito.runner")
public class PockitoRunnerTest {

    @TestedClass
    SomeDependantBusinessClass businessClass = new SomeDependantBusinessClass();

    @Test
    public void shouldDoSomething() {
        String result = businessClass.independentMethod("aCSds");
        Assert.assertEquals(result, "CSads");
    }
    @Test
    public void shouldDoSomethingElse() {
        String result = businessClass.independentMethod("aCSds");
        Assert.assertEquals(result, "CSads");
    }
}
