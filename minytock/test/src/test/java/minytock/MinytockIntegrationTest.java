package minytock;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import minytock.junit.MinytockRunner;
import minytock.test.Mock;
import minytock.test.Ready;
import minytock.test.Verify;
import mockit.MockUp;
import mockit.Mockit;
import net.sf.cglib.proxy.Mixin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static minytock.test.MinytockTest.*;
import static org.junit.Assert.assertEquals;

@RunWith(MinytockRunner.class)
public class MinytockIntegrationTest {
	
	@Ready
	TestBean bean;
	
	@Ready
	TestBean bean2;
	
	@Before
	public void setUp() {
		bean = new TestBean();
	}
	
	@Test
	public void test() {
		
		Minytock.real(bean2).test("asdf");
		
		final String expected = "lordy";
		
		Mock<TestBean> mock = new Mock<TestBean>() {
			
			@Verify(calls = 2)
			void test(String msg) {
				System.out.println("mock");
				assertEquals(expected, msg);
			}
			
		};
		
		System.out.println("mock class" + mock.getClass());
		
		delegate(bean).to(mock);
		
		bean.test(expected);
		
		bean.test(expected);
		
		verify(bean);
		
		remove(bean);
		
		bean.test(expected);
		
		verify(bean);
		
		assertEquals("original", get(String.class).from(bean));
		
		set("new").on(bean);
		
		assertEquals("new", get(String.class).from(bean));
		
		remove(bean);
	}
	
	@Test
	public void test2() {
		
		delegate(bean2).to(bean);
		real(bean).field = "fghhh";
		
		delegate(bean).to(new Object() {
			@SuppressWarnings("unused")
			void test(String msg) {
				System.out.println(msg + "sup" + real(bean).field);;
			}
		});
		
		bean2.test("asdf");
		
		remove(bean);
		
		bean2.test("ffff");
		
	}
	
	/*
	
	@Test
	public void testCgLib() throws IOException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		
		Mock<TestBean> mock = new Mock<TestBean>() {
			
			@Verify(calls = 2)
			public void test(String msg) {
				System.out.println("mock" + msg);
			}
			
		};
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassVisitor cv = new ClassAdapter(cw);

		ClassReader cr = new ClassReader(this.getClass().getResourceAsStream("/minytock/TestBean.class"));
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		ClassReader cr2 = new ClassReader(this.getClass().getResourceAsStream("/minytock/OtherBean.class"));
		ClassNode cn2 = new ClassNode();
		cr2.accept(cn2, 0);
		
		
		MergeAdapter ma = new MergeAdapter(cv, cn);
		ma.visit(Opcodes.V1_6, 33, "HelloWorld", null, "java/lang/Object", new String[] {});
		ma.visitEnd();
		
		MergeAdapter ma2 = new MergeAdapter(cv, cn2);
		ma2.visit(Opcodes.V1_6, 33, "HelloWorld", null, "java/lang/Object", new String[] {});
		ma2.visitEnd();
		
		
		FileOutputStream fos = new FileOutputStream("target/generated-sources/HelloWorld.class");
		fos.write(cw.toByteArray());
		fos.close();
		
		
		Class clazz = ClassLoader.getSystemClassLoader().loadClass("HelloWorld");
		
		
		
		for (Method m : TestBean.class.getMethods()) {
			if (m.getName().equals("blah")) System.out.println(m.getName());
		}
		
		for (Method m : TestBean.class.getDeclaredMethods()) {
			if (m.getName().equals("blah")) System.out.println(m.getName());
		}
		
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals("test")) System.out.println(m.getName());
		}
		
		for (Method m : clazz.getDeclaredMethods()) {
			if (m.getName().equals("test")) System.out.println(m.getName());
		}

	}
	
	public class MergeAdapter extends ClassAdapter {
		
		  private ClassNode cn;
		  private String cname;
		
		  public MergeAdapter(ClassVisitor cv, ClassNode cn) {
		    super(cv);
		    this.cn = cn;
		  }
		
		  public void visit(int version, int access, String name,
		      String signature, String superName, String[] interfaces) {
		    super.visit(version, access, name, signature, superName, interfaces);
		    this.cname = name;
		  }
		
		  public void visitEnd() {
			  System.out.println(cn.interfaces);
		    for (Iterator it = cn.fields.iterator(); it.hasNext();) {
		      ((FieldNode) it.next()).accept(this);
		    }
		    for (Iterator it = cn.methods.iterator(); it.hasNext();) {
		      MethodNode mn = (MethodNode) it.next();
		      String[] exceptions = new String[mn.exceptions.size()];
		      mn.exceptions.toArray(exceptions);
		      MethodVisitor mv = cv.visitMethod(mn.access, mn.name, mn.desc, mn.signature, exceptions);
		      mn.instructions.resetLabels();
		      mn.accept(new RemappingMethodAdapter(mn.access, mn.desc, mv, new SimpleRemapper(cname, cn.name)));
		    }
		    super.visitEnd();
		  }
		}
	
	
	
	
	public static class Service {
		public void doWork() {}
		public void blah1() {}
		public void blah2() {}
		public void blah3() {}
		public void blah4() {}
	}
	
	@Test
	public void testDelegationPlusVerificationSpeed() {
		
		//Minytock.provider = new DelegationHandlerProviderImpl(new FastDelegationHandlerCache());
		//Minytock.provider = new DelegationHandlerProviderImpl(new ExperimentalDelegationHandlerCache());
		
		Service service1 = new Service();
		service1 = Minytock.prepare(service1);
		
		long start = System.currentTimeMillis();
		long numCalls = 1000;
		for (int i = 0; i < numCalls; i++) {
			Minytock.delegate(service1).to(new Mock(){public @Verify void doWork() {}});
			service1.doWork();
			minytock.test.MinytockTest.verify(service1);
		}
		double proxyMs = (System.currentTimeMillis() - start) / (numCalls * 1.0d);
		
		start = System.currentTimeMillis();
		for (int i = 0; i < numCalls; i++) {
			new MockUp<Service>() {
				@mockit.Mock(invocations = 1)
				public void doWork() {}
			};
			service1.doWork();
			Mockit.tearDownMocks();
		}
		double jmockitMs = (System.currentTimeMillis() - start) / (numCalls * 1.0d);
		
		System.out.println();
		
		System.out.println("Mock delegation comparison:  ");
		
		System.out.println("proxy took " + proxyMs + " ms per mocking+execution+verification");
		
		System.out.println("jmockit took " + jmockitMs  + " ms per mocking+execution+verification");
		
		System.out.println("minytock faster than jmockit by " + jmockitMs / proxyMs  + " times");
	}
	
	*/

}
