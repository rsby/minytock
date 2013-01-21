package minytock.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MinytockSpringSchemaHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		this.registerBeanDefinitionParser("config", new MinytockSpringConfigParser());
	}

}
