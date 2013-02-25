package minytock.spring;

import minytock.Minytock;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class TargetedRefMockConfigHandler implements MockConfigHandler {
	
	private String targetRef;
	private String mockRef;
	private ConfigurableListableBeanFactory beanFactory;
	
	public TargetedRefMockConfigHandler(String targetRef, String mockRef) {
		this.targetRef = targetRef;
		this.mockRef = mockRef;
	}

	@Override
	public void init(ConfigurableListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public boolean isApplicable(String beanName) {
		return targetRef.equals(beanName);
	}

	@Override
	public Object getMock(Object bean) {
		return Minytock.delegate(Minytock.prepare(bean)).to(beanFactory.getBean(mockRef));
	}

}
