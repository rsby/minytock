package minytock.ui;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DashboardController {
	
	@RequestMapping(method = RequestMethod.GET, value = "dashboard")
	public String dashboard() {
		return "dashboard";
	}

}
