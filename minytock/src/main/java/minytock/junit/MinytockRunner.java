package minytock.junit;

import minytock.Minytock;
import minytock.delegate.LocalDelegationCache;
import minytock.test.ReadyAssistant;
import minytock.test.Ready;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class MinytockRunner extends BlockJUnit4ClassRunner {
	
	ReadyAssistant assistant;

	@SuppressWarnings("unchecked")
	public MinytockRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		assistant = new ReadyAssistant(clazz, Ready.class);
		Minytock.PROVIDER.setCache(new LocalDelegationCache());
	}
	
	@Override 
	public void run(RunNotifier rn) {
		super.run(rn);
		Minytock.cleanup();
	}
	
	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		assistant.prepare(test);
		return test;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected Statement withBefores(FrameworkMethod frameworkMethod, Object testInstance, Statement statement) {
		return super.withBefores(frameworkMethod, testInstance, new AfterBefores(statement, assistant, testInstance));
	}

}
