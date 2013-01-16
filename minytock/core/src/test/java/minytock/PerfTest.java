package minytock;

import mockit.Mock;
import mockit.MockUp;

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
	}
	
	@Test
	public void testPerf() {
		
		Service service1 = new Service();
		Service service2 = new Service();
		
		service1 = Minytock.prepare(service1);
		Minytock.delegate(service1).to(service2);
		
		double proxyMs = this.getMsPerCall(service1);
		double normalMs = this.getMsPerCall(service2);
		
		new MockUp<Service>() {
			@Mock
			public void doWork() {}
		};
		
		double jmockitMs = this.getMsPerCall(service2);
		
		System.out.println("For repeated calls to the same mock:  ");
		
		System.out.println("proxy took " + proxyMs + " ms");
		
		System.out.println("normal took " + normalMs  + " ms");
		
		System.out.println("mockit took " + jmockitMs  + " ms");
		
		System.out.println("minytock slower than plain java by " + proxyMs / normalMs  + " times");
		
		System.out.println("jmockit slower than plain java by " + jmockitMs / normalMs  + " times");
		
		System.out.println("minytock faster than jmockit by " + jmockitMs / proxyMs  + " times");
		
		
		Minytock.remove(service1);
		long start = System.currentTimeMillis();
		long numCalls = 100;
		for (int i = 0; i < numCalls; i++) {
			Minytock.delegate(service1).to(new Object(){public void doWork() {}});
			service1.doWork();
		}
		proxyMs = (System.currentTimeMillis() - start) / (numCalls * 1.0d);
		
		start = System.currentTimeMillis();
		for (int i = 0; i < numCalls; i++) {
			new MockUp<Service>() {
				@Mock
				public void doWork() {}
			};
			service1.doWork();
		}
		jmockitMs = (System.currentTimeMillis() - start) / (numCalls * 1.0d);
		
		
		System.out.println("For repeated calls to new mocks:  ");
		
		System.out.println("proxy took " + proxyMs + " ms");
		
		System.out.println("jmockit took " + jmockitMs  + " ms");
		
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
