### basic junit + spring example ###

java code

```
@RunWith(MinytockSpringRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class ExampleTest {
	
   @Autowired
   SomeService serviceUnderTest;
	
   @Autowired
   SomeOtherService serviceToMock;
	
   @Ready
   SomeBean beanToMock;
	
   @Test
   public void testDoSomething() {
		
      delegate(serviceToMock).to(new Mock<SomeOtherService>() {
			
         @Verify
         SomeBean getSomething() {
            return beanToMock;
         }
			
      });
		
      serviceUnderTest.doSomething();
		
      verify(serviceToMock);
   }

}
```

config code

```

    <bean class="minytock.spring.MinytockPostProcessor"/>

```

### what's going on in the example ###

the MinytockPostProcessor prepares all the beans for delegation/mocking.  the @Ready annotation assigns an empty mock to null values and replaces non-null values with a mockable/delegatable proxy.

the rest of it is pretty self-explanatory:  the delegate().to() chain will delegate method calls from the service to the mock for any methods that the mock declares (any methods not specified in the mock will still be executed as usual).  the @Verify annotation signifies that the method must be called exactly once (it has attributes for min, max and exact number, but one call is the default).  important to note that each object can delegate to only one other object at a time.  assigning a new delegate will replace any existing one.

and that's pretty much it.  junit delegations are thread-local by default, so running multiple tests in parallel won't screw it up, whereas delegations outside of junit tests are not thread-local - this allows minytock to be used on a deployed application.

the entire java api is accessed statically through the minytock.Minytock class, and it has a few other features like a get/set functionality for getting and setting private fields.


### weird features ###

  * you can delegate an object to itself - `delegate(service).to(service);`
  * you can access the real object from within a mock - `this.realObject.doSomething();`