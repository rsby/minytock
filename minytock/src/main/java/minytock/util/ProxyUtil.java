package minytock.util;

import java.lang.reflect.Proxy;

public class ProxyUtil {
	
	public static boolean isProxyClass(Class<?> c) {
		return Proxy.isProxyClass(c) || c.getName().contains("EnhancerByCGLIB");
	}

}
