package minytock.ui;

import java.util.Map;

import minytock.spring.BeanInfo;
import minytock.spring.StaticBeanAccessor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {
	
	@RequestMapping(method = RequestMethod.GET, value = "show")
	public String show() {
		System.out.println("loading dashboard...");
		return "dashboard";
	}
	
	@ResponseBody
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value="eligibleBeans")
	public Map<String, BeanInfo> eligibleBeans() {
		return StaticBeanAccessor.getInfos();
	}

}
