package minytock.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minytock.Minytock;

public class BeanInfo {
	
	private static final Logger LOG = LoggerFactory.getLogger(BeanInfo.class);
	
	private String name;
	private String className;
	
	public BeanInfo(String name, Object bean) {
		this.name=  name;
		this.className = bean.getClass().getName();
	}

	public String getName() {
		return name;
	}
	
	public String getClassName() {
		return className;
	}
	
	public static List<BeanInfo> forBeans(Map<String, ?> beans) {
		List<BeanInfo> infos = new LinkedList<BeanInfo>();
		for (Entry<String, ?> entry : beans.entrySet()) {
			Object bean = Minytock.real(entry.getValue());
			if (!bean.equals(entry.getValue())) {
				LOG.info("bean:  " + entry.getKey());
				infos.add(new BeanInfo(entry.getKey(), bean));
			}
		}
		return infos;
	}

}
