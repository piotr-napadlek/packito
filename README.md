# packito
TDD taken on a package level! 

    packito = package + mockito
#What for?
Ever felt tired of writing tests that actually only checked if another classes' method was called to achieve some magic threshold of code coverage?
Ever felt overwhelmed by amount of dependencies you needed to inject just to make some simple assertion without getting NPE?
Ever felt annoyed of having to adjust JUnit test while the change you'd made had actually no influence on a functionality of an interface you've 
provided?
Ever wondered what if we could simply manage which elements of our application will be mocked during tests, and which will be actually real code that we will
 cover with tests?
 
 Well, we have a solution
 
#How?
Let's think: what splits our application into smaller parts that we can further logically divide and manage?
Right.

Packages.

We use packages as a limit of our test scope. Everything, that is outside our tested package get's mocked by default. 
Everything, that is found inside a package, and is required as dependency in tested class dependency tree - get's instantiated and injected.
Of course with their dependencies, if inside the tested package, or a mock, if outside. Simple as that.

#How again?
    package com.mycompany.app.service.somecomponent;
     
    import com.mycompany.app.service.othercomponent.ExternalRequiredDependency;
    import com.mycompany.app.service.othercomponent.ExternalRequiredDependencyImpl;
    import com.mycompany.app.service.othercomponent.ExternalDependency;
     
    import org.junit.Before;
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.mockito.Mockito;
    import org.packito.annotations.*;
    import org.packito.runner.PackitoRunner;
     
    import java.util.function.Function;
     
    @RunWith(PackitoRunner.class)
    @TestedPackage("com.mycompany.app.service.mycomponent") // can be omitted - will scan test class package then
    public class SomeComponentTest {
     
        @TestedClass(autoInstantiate = true)
        private ComponentEndPoint testedObject; // tested class on which we call methods
     
        @ProvidedDependency
        private ExternalRequiredDependency dependencyWeDoNotWishToMock = new ExternalRequiredDependencyImpl();
     
        @MockedDependency
        private ExternalDependency dependencyToMock; // declare to be able to make verifications on a mock
     
        @MockProvider
        private Function<Class<?>, Object> mockProvider = Mockito::mock; // our mock framework of option, 
        // as packito is mocking framework independant
     
        @Before
        public void setUp() {
            // let's set up our mocks a little
            Mockito.when(dependencyToMock.provideSomeValue(Mockito.any()).thenReturn("There you have!");
        }
     
        @Test
        public void someComponentTest() {
            // given
            final String someInput = "Test Provided input";
            // when
            String result = testedObject.callSomeRealMethodRelyingOnDependencies(someInput);
            // then
            Mockito.verify(dependencyToMock, Mockito.times(1)).provideSomeValue(Mockito.any());
            // further assertions with result ...
        }
    
    }
 
#Details
Packito is mocking framework agnostic, though Mockito is strongly recommended. 
Packito is essentially lightweight controlled dependency injection mini - framework.
Packito will just try to make your tests a little bit more meaningful.
