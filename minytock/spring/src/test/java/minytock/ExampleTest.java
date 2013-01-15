package minytock;

import minytock.delegate.DelegationHandlerCache;
import minytock.spring.MinytockSpringRunner;
import minytock.spring.SpringAopDelegationHandlerProvider;
import minytock.spy.Spy;
import minytock.test.Mock;
import minytock.test.Ready;
import minytock.test.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactory;
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
	
	@Test
	public void testAopProxy() {
		/*
		Minytock.provider = new SpringAopDelegationHandlerProvider(Spy.get(DelegationHandlerCache.class).from(provider));
		
		TestBean bean = new TestBean();
		ProxyFactory factory = new ProxyFactory();
		factory.setTarget(bean);
		bean = (TestBean) factory.getProxy();
		bean = prepare(bean);
		delegate(bean).to(bean2);
		bean.test("yo");
		*/
	}

}
