package minytock;

import minytock.spring.MinytockTestExecutionListener;
import minytock.test.Mock;
import minytock.test.Ready;
import minytock.test.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static minytock.Minytock.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TestExecutionListeners(MinytockTestExecutionListener.class)
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
			@Verify(calls = 1)
			public SomeBean getSomething() {
				return beanToMock;
			}
		});
		
		serviceUnderTest.doSomething();
		
		verify(serviceToMock, beanToMock);
		
		removeAll(serviceToMock, beanToMock);
	}

}
