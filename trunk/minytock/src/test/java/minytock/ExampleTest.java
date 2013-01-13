package minytock;

import minytock.spring.MinytockSpringRunner;
import minytock.test.Mock;
import minytock.test.Ready;
import minytock.test.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static minytock.Minytock.*;

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
