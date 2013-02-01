package minytock.ui;

import java.util.ArrayList;
import java.util.List;

import minytock.Minytock;
import minytock.spring.SpringDelegationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AjaxController implements ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(AjaxController.class);
	
	@Autowired
	private SpringDelegationRegistry registry;
	private ApplicationContext applicationContext;
	
	@ResponseBody
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "eligibleBeans")
	public List<BeanInfo> eligibleBeans() throws Exception {
		
		List<BeanInfo> infos = new ArrayList<BeanInfo>();
		
		try {
			for (String beanName : registry.getBeanNames()) {
				Object bean = applicationContext.getParent().getBean(beanName);
				infos.add(new BeanInfo(beanName, Minytock.real(bean)));
			}
			return infos;
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
