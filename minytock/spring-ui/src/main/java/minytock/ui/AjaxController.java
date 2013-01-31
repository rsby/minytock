package minytock.ui;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AjaxController implements ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(AjaxController.class);
	
	private ApplicationContext applicationContext;
	
	@ResponseBody
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "eligibleBeans")
	public List<BeanInfo> eligibleBeans(String className) throws Exception {
		
		LOG.info("Retreiving eligible beans...");
		
		try {
			Map<String, ?> beans = applicationContext.getParent().getBeansOfType(Class.forName(className));
			LOG.info("Bean found:  " + beans.size());
			return BeanInfo.forBeans(beans);
		} catch (Exception e) {
			LOG.error("Error!", e);
			throw e;
		}
		
		
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
