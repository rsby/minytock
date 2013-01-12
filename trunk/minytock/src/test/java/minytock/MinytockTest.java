package minytock;

import minytock.junit.MinytockRunner;
import minytock.test.Mock;
import minytock.test.Ready;
import minytock.test.Verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
		//TODO handle delegating empty mocks to themselves
	}
	
	@Test
	public void test() {
		
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

}
