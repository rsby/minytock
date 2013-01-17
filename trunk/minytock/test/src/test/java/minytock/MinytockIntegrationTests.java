package minytock;

import minytock.junit.MinytockRunner;
import minytock.test.Mock;
import minytock.test.Ready;
import minytock.test.Verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static minytock.test.MinytockTest.*;
import static org.junit.Assert.assertEquals;

@RunWith(MinytockRunner.class)
public class MinytockIntegrationTests {
	
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
				System.out.println("mock");
				assertEquals(expected, msg);
			}
			
		};
		
		System.out.println("mock class" + mock.getClass());
		
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
		
		bean2.test("asdf");
		
		remove(bean);
		
		bean2.test("ffff");
		
	}

}