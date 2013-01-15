package minytock.util;

import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Enhancer;

public class ProxyUtil {
	
	public static boolean isProxyClass(Class<?> c) {
		return Proxy.isProxyClass(c) || Enhancer.isEnhanced(c);
	}

}
