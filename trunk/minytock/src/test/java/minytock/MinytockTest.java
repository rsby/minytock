package minytock;

import minytock.delegate.DelegationHandlerCache;
import minytock.junit.MinytockRunner;
import minytock.spring.SpringAopDelegationHandlerProvider;
import minytock.spy.Spy;
import minytock.test.Mock;
import minytock.test.Ready;
import minytock.test.Verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactory;

import static minytock.Minytock.*;
import static org.junit.Assert.assertEquals;

@RunWith(MinytockRunner.class)
public class MinytockTest {
	
	@Ready
	TestBean bean;
	
	@Ready
	TestBean bean2;
	
	@Before
	public void setUp() {
		bean = new TestBean();
	}
	
	@Test
	public void test() {
		
		Minytock.real(bean2).test("asdf");
		
		final String expected = "lordy";
		
		Mock<TestBean> mock = new Mock<TestBean>() {
			
			@Verify(calls = 2)
			void test(String msg) {
				assertEquals(expected, msg);
			}
			
		};
		
		delegate(bean).to(mock);
		
		bean.test(expected);
		
		bean.test(expected);
		
		verify(bean);
		
		remove(bean);
		
		bean.test(expected);
		
		verify(bean);
		
		assertEquals("original", get(String.class).from(bean));
		
		set("new").on(bean);
		
		assertEquals("new", get(String.class).from(bean));
		
		remove(bean);
	}
	
	@Test
	public void test2() {
		
		delegate(bean2).to(bean);
		real(bean).field = "fghhh";
		
		delegate(bean).to(new Object() {
			@SuppressWarnings("unused")
			void test(String msg) {
				System.out.println(msg + "sup" + real(bean).field);;
			}
		});
		
		bean.test("asdf");
		
		remove(bean);
		
		bean2.test("ffff");
		
	}
	
	@Test
	public void testAopProxy() {
		
		Minytock.provider = new SpringAopDelegationHandlerProvider(Spy.get(DelegationHandlerCache.class).from(provider));
		
		bean = new TestBean();
		ProxyFactory factory = new ProxyFactory();
		factory.setTarget(bean);
		bean = (TestBean) factory.getProxy();
		bean = prepare(bean);
		delegate(bean).to(bean2);
		bean.test("yo");
	}

}
