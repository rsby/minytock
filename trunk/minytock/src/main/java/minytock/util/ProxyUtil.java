package minytock.util;

import java.lang.reflect.Proxy;

public class ProxyUtil {
	
	public static boolean isProxyClass(Class<?> c) {
		return Proxy.isProxyClass(c) || c.getName().contains("$$EnhancerByCGLIB$$");  //Enhancer.isEnhanced(c); - we don't use this since it requires cglib on the classpath - may reconsider to require
	}

}
