package minytock.ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MinytockController implements ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	private Map<String, BeanInfo> infos = new ConcurrentHashMap<String, BeanInfo>();
	
	@ResponseBody
    @RequestMapping(method = RequestMethod.POST, value="/minytock/eligibleBeans" )
	public Map<String, BeanInfo> getEligibleBeans() {
		this.infos.put("test", new BeanInfo());
		return infos;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
