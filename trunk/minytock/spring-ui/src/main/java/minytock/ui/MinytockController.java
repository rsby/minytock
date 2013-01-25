package minytock.ui;

import java.util.Map;

import minytock.spring.BeanInfo;
import minytock.spring.StaticBeanAccessor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/minytock")
public class MinytockController {
	
	@ResponseBody
    @RequestMapping(method = RequestMethod.POST, value="/eligibleBeans" )
	public Map<String, BeanInfo> getEligibleBeans() {
		return StaticBeanAccessor.getInfos();
	}

}
