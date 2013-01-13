package minytock.spring;

import minytock.Minytock;
import minytock.delegate.LocalDelegationCache;
import minytock.test.Ready;
import minytock.test.ReadyAssistant;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

public class MinytockTestExecutionListener extends DependencyInjectionTestExecutionListener {

	private ReadyAssistant assistant;
	
	@SuppressWarnings("unchecked")
	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		Minytock.PROVIDER.setCache(new LocalDelegationCache());
		assistant = new ReadyAssistant(testContext.getTestClass(), Ready.class);
		super.beforeTestClass(testContext);
	}

	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {
		super.prepareTestInstance(testContext);
		assistant.prepare(testContext.getTestInstance());
	}

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		super.beforeTestMethod(testContext);
		assistant.afterBefores(testContext.getTestInstance());
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		super.afterTestMethod(testContext);
		Minytock.cleanup();
	}

}
