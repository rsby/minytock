package minytock;

import minytock.delegate.DefaultDelegationHandlerCache;
import minytock.delegate.DelegationHandlerProviderImpl;
import minytock.delegate.FastDelegationHandlerCache;
import minytock.delegate.ThreadLocalDelegationHandlerCache;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mockit;
import net.sf.cglib.proxy.Mixin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * a really crap perf test
 * 
 * @author reesbyars
 *
 */
public class PerfTest {
	
	public static class Service {
		public void doWork() {}
		public void blah1() {}
		public void blah2() {}
		public void blah3() {}
		public void blah4() {}
	}
	
	int numCalls = 100;
	
	@Before
	public void setUp() {
		//Minytock.provider = new DelegationHandlerProviderImpl(new ThreadLocalDelegationHandlerCache());
		Minytock.provider = new DelegationHandlerProviderImpl(new DefaultDelegationHandlerCache());
		//Minytock.provider = new DelegationHandlerProviderImpl(new FastDelegationHandlerCache());
	}
	
	@After
	public void tearDown() {
		Mockit.tearDownMocks();
	}
	
	@Test
	public void testPreparationSpeed() {
		
		//Minytock.provider = new DelegationHandlerProviderImpl(new FastDelegationHandlerCache());
		//Minytock.provider = new DelegationHandlerProviderImpl(new ExperimentalDelegationHandlerCache());
		
		Service service1 = new Service();
		service1 = Minytock.prepare(service1);
		
		long start = System.currentTimeMillis();
		//long numCalls = 1000;
		for (int i = 0; i < numCalls; i++) {
			service1 = Minytock.prepare(new Service()); //really, this wouldn't have to happen for each new mock, but minytock wins even with it
			Minytock.delegate(service1).to(new Object(){public void doWork() {}});
			service1.doWork();
		}
		double proxyMs = (System.currentTimeMillis() - start) / (numCalls * 1.0d);
		
		start = System.currentTimeMillis();
		for (int i = 0; i < numCalls; i++) {
			new MockUp<Service>() {
				@Mock
				public void doWork() {}
			};
			service1.doWork();
		}
		double jmockitMs = (System.currentTimeMillis() - start) / (numCalls * 1.0d);
		
		System.out.println();
		
		System.out.println("Mock preparation + delegation comparison:  ");
		
		System.out.println("proxy took " + proxyMs + " ms");
		
		System.out.println("jmockit took " + jmockitMs  + " ms");
		
		System.out.println("minytock faster than jmockit by " + jmockitMs / proxyMs  + " times");
	}
	
	@Test
	public void testDelegationSpeed() {
		
		//Minytock.provider = new DelegationHandlerProviderImpl(new FastDelegationHandlerCache());
		//Minytock.provider = new DelegationHandlerProviderImpl(new ExperimentalDelegationHandlerCache());
		
		Service service1 = new Service();
		service1 = Minytock.prepare(service1);
		
		long start = System.currentTimeMillis();
		//long numCalls = 1000;
		for (int i = 0; i < numCalls; i++) {
			Minytock.delegate(service1).to(new Object(){public void doWork() {}});
			service1.doWork();
		}
		double proxyMs = (System.currentTimeMillis() - start) / (numCalls * 1.0d);
		
		start = System.currentTimeMillis();
		for (int i = 0; i < numCalls; i++) {
			new MockUp<Service>() {
				@Mock
				public void doWork() {}
			};
			service1.doWork();
		}
		double jmockitMs = (System.currentTimeMillis() - start) / (numCalls * 1.0d);
		
		System.out.println();
		
		System.out.println("Mock delegation comparison:  ");
		
		System.out.println("proxy took " + proxyMs + " ms");
		
		System.out.println("jmockit took " + jmockitMs  + " ms");
		
		System.out.println("minytock faster than jmockit by " + jmockitMs / proxyMs  + " times");
	}
	
	@Test
	public void testPartialMockExecutionSpeed_call() {
		
		Service service1 = new Service();
		Service service2 = new Service();
		
		service1 = Minytock.prepare(service1);
		Minytock.delegate(service1).to(new Object(){public void doWork() {}}).doWork();
		
		
		double proxyMs = this.getMsPerCall(service1);
		double normalMs = this.getMsPerCall(service2);
		
		new MockUp<Service>() {
			@Mock
			public void doWork() {}
		};
		
		double jmockitMs = this.getMsPerCall(service2);
		
		System.out.println();
		
		System.out.println("Mock execution comparison (partial, with call):  ");
		
		System.out.println("proxy took " + proxyMs + " ms");
		
		System.out.println("normal took " + normalMs  + " ms");
		
		System.out.println("mockit took " + jmockitMs  + " ms");
		
		System.out.println("minytock slower than plain java by " + proxyMs / normalMs  + " times");
		
		System.out.println("jmockit slower than plain java by " + jmockitMs / normalMs  + " times");
		
		System.out.println("minytock faster than jmockit by " + jmockitMs / proxyMs  + " times");
		
	}
	
	@Test
	public void testPartialMockExecutionSpeed_dontCall() {
		
		Service service1 = new Service();
		Service service2 = new Service();
		
		service1 = Minytock.prepare(service1);
		Minytock.delegate(service1).to(new Object()).doWork();
		
		
		double proxyMs = this.getMsPerCall(service1);
		double normalMs = this.getMsPerCall(service2);
		
		new MockUp<Service>() {
			@Mock
			public void doWork() {}
		};
		
		double jmockitMs = this.getMsPerCall(service2);
		
		System.out.println();
		
		System.out.println("Mock execution comparison (partial, without call):  ");
		
		System.out.println("proxy took " + proxyMs + " ms");
		
		System.out.println("normal took " + normalMs  + " ms");
		
		System.out.println("mockit took " + jmockitMs  + " ms");
		
		System.out.println("minytock slower than plain java by " + proxyMs / normalMs  + " times");
		
		System.out.println("jmockit slower than plain java by " + jmockitMs / normalMs  + " times");
		
		System.out.println("minytock faster than jmockit by " + jmockitMs / proxyMs  + " times");
		
	}
	
	@Test
	public void testFullMockExecutionSpeed() {
		
		Service service1 = new Service();
		Service service2 = new Service();
		
		service1 = Minytock.prepare(service1);
		Minytock.delegate(service1).to(new Service(){public void doWork() {}}).doWork();
		
		
		double proxyMs = this.getMsPerCall(service1);
		double normalMs = this.getMsPerCall(service2);
		
		new MockUp<Service>() {
			@Mock
			public void doWork() {}
		};
		
		double jmockitMs = this.getMsPerCall(service2);
		
		System.out.println();
		
		System.out.println("Mock execution comparison (full):  ");
		
		System.out.println("proxy took " + proxyMs + " ms");
		
		System.out.println("normal took " + normalMs  + " ms");
		
		System.out.println("mockit took " + jmockitMs  + " ms");
		
		System.out.println("minytock slower than plain java by " + proxyMs / normalMs  + " times");
		
		System.out.println("jmockit slower than plain java by " + jmockitMs / normalMs  + " times");
		
		System.out.println("minytock faster than jmockit by " + jmockitMs / proxyMs  + " times");
		
	}
	
	private double getMsPerCall(Service service) {
		long start = System.currentTimeMillis();
		long numCalls = 100000;
		for (int i = 0; i < numCalls; i++) {
			service.doWork();
		}
		return (System.currentTimeMillis() - start) / (numCalls * 1.0d);
	}
	
	

}
