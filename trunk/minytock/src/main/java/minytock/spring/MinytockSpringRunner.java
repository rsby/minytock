package minytock.spring;

import minytock.Minytock;
import minytock.delegate.ThreadLocalDelegationHandlerCache;
import minytock.junit.AfterBefores;
import minytock.test.ReadyAssistant;
import minytock.test.Ready;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class MinytockSpringRunner extends SpringJUnit4ClassRunner {

	ReadyAssistant assistant;

	@SuppressWarnings("unchecked")
	public MinytockSpringRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		Minytock.getProvider().setCache(new ThreadLocalDelegationHandlerCache());
		assistant = new ReadyAssistant(clazz, Ready.class);
	}
	
	@Override 
	public void run(RunNotifier rn) {
		super.run(rn);
		Minytock.clearAll();
	}
	
	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		assistant.prepare(test);
		return test;
	}
	
	@Override
	protected Statement withBefores(FrameworkMethod frameworkMethod, Object testInstance, Statement statement) {
		return super.withBefores(frameworkMethod, testInstance, new AfterBefores(statement, assistant, testInstance));
	}

}
