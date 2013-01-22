package minytock.spring;

import java.util.ArrayList;
import java.util.List;

import minytock.Minytock;
import minytock.delegate.DefaultDelegationHandlerCache;
import minytock.delegate.DelegationHandlerCache;
import minytock.delegate.DelegationHandlerProvider;
import minytock.delegate.DelegationHandlerProviderImpl;
import minytock.delegate.FastDelegationHandlerCache;
import minytock.delegate.ThreadLocalDelegationHandlerCache;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class MinytockSpringConfigParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		
		String cacheType = element.getAttribute("cache-type");
		DelegationHandlerCache cache = null;
		if ("fast".equals(cacheType)) {
			cache = new FastDelegationHandlerCache();
		} else if ("parallel-testing".equals(cacheType)) {
			cache = new ThreadLocalDelegationHandlerCache();
		} else {
			cache = new DefaultDelegationHandlerCache();
		}
		
		String aopMode = element.getAttribute("aop-compatible");
		DelegationHandlerProvider provider = null;
		String postProcessorName = null;
		if ("true".equals(aopMode)) {
			provider = new SpringAopDelegationHandlerProvider(cache);
			postProcessorName = "minytock.spring.MinytockAopPostProcessor";
		} else {
			provider = new DelegationHandlerProviderImpl(cache);
			postProcessorName = "minytock.spring.DelegationPostProcessor";
		}
		
		Minytock.provider = provider;
		
		List<Element> includeFilterElements = DomUtils.getChildElementsByTagName(element, "include-filter");
		List<String> includeFilterExpressions = new ArrayList<String>();
		for (Element includeFilterElement : includeFilterElements) {
			String expression = StringUtils.trimAllWhitespace(includeFilterElement.getAttribute("expression"));
			includeFilterExpressions.add(expression);
		}
		if (includeFilterExpressions.isEmpty()) {
			includeFilterExpressions.add("*");
		}
		
		List<Element> excludeFilterElements = DomUtils.getChildElementsByTagName(element, "exclude-filter");
		List<String> excludeFilterExpressions = new ArrayList<String>();
		for (Element excludeFilterElement : excludeFilterElements) {
			String expression = StringUtils.trimAllWhitespace(excludeFilterElement.getAttribute("expression"));
			excludeFilterExpressions.add(expression);
		}
		
		parserContext.registerBeanComponent(
				new BeanComponentDefinition(
						BeanDefinitionBuilder
						.genericBeanDefinition(postProcessorName)
						.addConstructorArgValue(includeFilterExpressions)
						.addConstructorArgValue(excludeFilterExpressions)
						.getBeanDefinition(), "minytockPostProcessor"));
		
		//parse the auto-mock elements and register an MinytockFactoryPostProcessor with the list of auto-mock types
		List<Element> autoMockElements = DomUtils.getChildElementsByTagName(element, "auto-mock");
		List<String> autoMockTypes = new ArrayList<String>();
		for (Element autoMockElement : autoMockElements) {
			String type = StringUtils.trimAllWhitespace(autoMockElement.getAttribute("type"));
			autoMockTypes.add(type);
		}
		parserContext.registerBeanComponent(
				new BeanComponentDefinition(
						BeanDefinitionBuilder
						.genericBeanDefinition(MinytockFactoryPostProcessor.class)
						.addConstructorArgValue(autoMockTypes)
						.getBeanDefinition(), "minytockEmptyMockFactory"));
						
		return null;
	}

}
