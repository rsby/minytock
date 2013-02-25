package minytock.spring;

import minytock.Minytock;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class TargetedEmptyMockConfigHandler implements MockConfigHandler {
	
	private String beanName;
	
	public TargetedEmptyMockConfigHandler(String ref) {
		beanName = ref;
	}

	@Override
	public void init(ConfigurableListableBeanFactory beanFactory) {
	}

	@Override
	public boolean isApplicable(String beanName) {
		return this.beanName.equals(beanName);
	}

	@Override
	public Object getMock(Object bean) throws Exception {
		return Minytock.delegate(Minytock.prepare(bean)).to(MinytockFactoryBean.getFor(AopUtils.getTargetClass(bean)).getObject());
	}

}
