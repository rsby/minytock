package minytock.ui;

public class BeanInfo {
	
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

}
