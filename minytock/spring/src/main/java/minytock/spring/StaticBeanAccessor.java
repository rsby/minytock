package minytock.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class StaticBeanAccessor implements ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(StaticBeanAccessor.class);
	
	private static ApplicationContext applicationContext;
	private static Map<String, BeanInfo> infos = new ConcurrentHashMap<String, BeanInfo>();
	
	public StaticBeanAccessor() {
		LOG.warn("Instantiating the minytock StaticBeanAccessor.  This indicates that minytock has been deployed.");
		infos.put("test", new BeanInfo());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		StaticBeanAccessor.applicationContext = applicationContext;
	}
	
	public static Map<String, BeanInfo> getInfos() {
		return infos;
	}

}
