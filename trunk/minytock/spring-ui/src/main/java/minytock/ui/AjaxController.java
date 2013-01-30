package minytock.ui;

import java.util.Map;

import minytock.spring.BeanInfo;
import minytock.spring.StaticBeanAccessor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AjaxController {
	
	@ResponseBody
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "eligibleBeans")
	public Map<String, BeanInfo> eligibleBeans() {
		System.out.println(StaticBeanAccessor.getInfos().get("test"));
		return StaticBeanAccessor.getInfos();
	}

}
