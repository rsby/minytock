package minytock.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import minytock.Minytock;
import minytock.spring.SpringDelegationRegistry;
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
	
	@Autowired
	private SpringDelegationRegistry registry;
	private ApplicationContext applicationContext;
	
	@ResponseBody
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "eligibleBeans")
	public List<BeanInfo> eligibleBeans() throws Exception {
		
		List<BeanInfo> infos = new ArrayList<BeanInfo>();
		
		for (String beanName : registry.getBeanNames()) {
			try {
				Object bean = applicationContext.getParent().getBean(beanName);
				infos.add(new BeanInfo(beanName, Minytock.real(bean)));
			} catch (Exception e) {
				//blah
			}
			
		}
		return infos;
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "delegate.html")
	public void delegate(String beanName, String methodCode) throws CannotCompileException, InstantiationException, IllegalAccessException, UnsupportedEncodingException, ClassNotFoundException {
		String decodedMethodCode = URLDecoder.decode(methodCode, "UTF-8");
		 ClassPool pool = ClassPool.getDefault();
		 pool.appendClassPath(new LoaderClassPath(this.getClass().getClassLoader()));
		 String delegateclassName = beanName + "MinytockDelegate";
		 CtClass delegateClass;
		try {
			delegateClass = pool.get(delegateclassName);
		} catch (NotFoundException e) {
			delegateClass = pool.makeClass(delegateclassName);
		}
		 try {
			delegateClass.addMethod(CtNewMethod.make(decodedMethodCode, delegateClass));
		} catch (CannotCompileException e) {
			
		}
		 Class<?> clazz = delegateClass.toClass();
		 Object delegate = clazz.newInstance();
		 Minytock.delegate(applicationContext.getBean(beanName)).to(delegate);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
